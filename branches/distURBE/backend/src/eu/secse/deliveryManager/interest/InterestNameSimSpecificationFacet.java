/* This file is part of Delivery Manager.
 * (c) 2007 Matteo Miraz et al., Politecnico di Milano
 *
 * Delivery Manager is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation; either version 2 of the License, or 
 * (at your option) any later version.
 *
 * Delivery Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Delivery Manager; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package eu.secse.deliveryManager.interest;

import it.polimi.elet.si.urbe.similarity.SimilarityException;
import it.polimi.elet.si.urbe.similarity.WSDLSimilarityEngine;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.model.FacetSpec;
import eu.secse.deliveryManager.model.FacetSpecXML;
import eu.secse.deliveryManager.model.XMLCommons;

public class InterestNameSimSpecificationFacet implements Interest {

	private static final boolean DEBUG = false;
	
	private static final long serialVersionUID = 8684903789784507923L;

	private final String name;

	/** Facet Specification Schema Document */
	private final String schemaDocument;
	/** Facet Specification Schema Document as DOM Document */
	private transient Document dom;

	/** use the compiled version of xpath queries? */
	private static final boolean COMPILE_XPATH = true;
	
	/** Main XPath Expression: selects the set of interesting nodes */
	private final String xpathExpression;
	/** Main Compiled XPath Expression: selects the set of interesting nodes */
	private transient XPathExpression xpathExpressionCompiled;
	
	/** Node XPath Expression: given a matching node, extract a string. Default: node::self() */
	private final String xpathExpression2;
	/** Node Compiled XPath Expression: given a matching node, extract a string. Default: node::self() */
	private transient XPathExpression xpathExpressionCompiled2;
	
	private final String[] queries;
	private final double threshold;
	
	private transient WSDLSimilarityEngine simEngine;
	
	public InterestNameSimSpecificationFacet(String facetSpecificationSchemaDocument, String xpath, String[] queries, double threshold) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		this(null, facetSpecificationSchemaDocument, xpath, "self::node()", queries, threshold);
	}	
	
	public InterestNameSimSpecificationFacet(String name, String facetSpecificationSchemaDocument, String xpath, String xpath2, String[] queries, double threshold) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		if(name != null) {
			this.name = name;
		} else {
			this.name = xpath;
		}
		
		this.queries = queries;
		this.threshold = threshold;
		
		this.schemaDocument = facetSpecificationSchemaDocument;
		if(schemaDocument != null) this.dom = XMLCommons.newDocumentBuilder().parse(new InputSource(new StringReader(this.schemaDocument)));

		this.xpathExpression = xpath;
		this.xpathExpression2 = xpath2;
		// check the xpath
		if(xpathExpression != null) this.xpathExpressionCompiled = XPathFactory.newInstance().newXPath().compile(xpathExpression);
		if(xpathExpression2 != null) this.xpathExpressionCompiled2 = XPathFactory.newInstance().newXPath().compile(xpathExpression2);
		// free memory
		this.xpathExpressionCompiled = null;
		this.xpathExpressionCompiled2 = null;
	}

	public String getName() {
		return name;
	}
	
	public String getSchemaDocument() {
		return this.schemaDocument;
	}
	
	public String getXpathExpression() {
		return this.xpathExpression;
	}
	
	public String getXpathExpression2() {
		return this.xpathExpression2;
	}
	
	public boolean matches(Deliverable elem) {
		if (elem instanceof DService) {
			DService srv = (DService) elem;
			
			if(srv.getSpecType() == null) return false;
			
			for (FacetSpec facet : srv.getSpecType()) {
				FacetSpecXML dfs = facet.getFacetSpecificationXML();
				
				// if is expressed an xpath constraint, check if the xml is present
				if(xpathExpressionCompiled == null || dfs != null) {
					// check if the schema is present or if is the same
					if (schemaDocument == null  || XMLCommons.compare(this.getDocumentDOM(), facet.getDocumentDOM())) {
						
						// if no xpath constraint, this facet matches to the filter!
						if(xpathExpression == null) return true;
						
						try {	//	compile the xpath expression
							if(COMPILE_XPATH && this.xpathExpression != null && this.xpathExpressionCompiled == null) 
								this.xpathExpressionCompiled = XPathFactory.newInstance().newXPath().compile(this.xpathExpression);

							if(COMPILE_XPATH && this.xpathExpression2 != null && this.xpathExpressionCompiled2 == null) 
								this.xpathExpressionCompiled2 = XPathFactory.newInstance().newXPath().compile(this.xpathExpression2);

						} catch (Exception e) {
							// Non dovrebbe mai capitare!
							assert(false);
						}

						try {
							NodeList nodeList;
							if(COMPILE_XPATH) nodeList = (NodeList) this.xpathExpressionCompiled.evaluate(dfs.getDocumentDOM(), XPathConstants.NODESET);
							else nodeList = (NodeList) XMLCommons.newXPath().evaluate(this.xpathExpression, dfs.getDocumentDOM(), XPathConstants.NODESET);
							
							for(int j = 0; j < nodeList.getLength(); j++) {
								Node node = nodeList.item(j);
								
								String str;
								if(COMPILE_XPATH) str = this.xpathExpressionCompiled2.evaluate(node);
								else str = XMLCommons.newXPath().evaluate(this.xpathExpression2, node);
								
								for (String query : queries) {
									double value = getSimEngine().nameSim(query, str);
									System.out.println("nameSim(" + query + ", " + str + ") = " + value);
									
									if(value >= threshold) return true;
								}
							}
						} catch (XPathExpressionException e) {
							// Non dovrebbe mai capitare!
							assert(false);
						} catch (SimilarityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}

		return false;
	}

	public float getSimilarity(Deliverable elem) {
		if (elem instanceof DService) {
			DService srv = (DService) elem;
			
			if(srv.getSpecType() == null) return 0;
			
			for (FacetSpec facet : srv.getSpecType()) {
				FacetSpecXML dfs = facet.getFacetSpecificationXML();
				
				// if is expressed an xpath constraint, check if the xml is present
				if(xpathExpressionCompiled == null || dfs != null) {
					// check if the schema is present or if is the same
					if (schemaDocument == null  || XMLCommons.compare(this.getDocumentDOM(), facet.getDocumentDOM())) {
						
						// if no xpath constraint, this facet matches to the filter!
						if(xpathExpression == null) return 1;
						
						try {	//	compile the xpath expression
							if(COMPILE_XPATH && this.xpathExpression != null && this.xpathExpressionCompiled == null) 
								this.xpathExpressionCompiled = XPathFactory.newInstance().newXPath().compile(this.xpathExpression);

							if(COMPILE_XPATH && this.xpathExpression2 != null && this.xpathExpressionCompiled2 == null) 
								this.xpathExpressionCompiled2 = XPathFactory.newInstance().newXPath().compile(this.xpathExpression2);

						} catch (Exception e) {
							// Non dovrebbe mai capitare!
							assert(false);
						}

						try {
							NodeList nodeList;
							if(COMPILE_XPATH) nodeList = (NodeList) this.xpathExpressionCompiled.evaluate(dfs.getDocumentDOM(), XPathConstants.NODESET);
							else nodeList = (NodeList) XMLCommons.newXPath().evaluate(this.xpathExpression, dfs.getDocumentDOM(), XPathConstants.NODESET);
							
							double max = 0.0;
							for(int j = 0; j < nodeList.getLength(); j++) {
								Node node = nodeList.item(j);
								
								String str;
								if(COMPILE_XPATH) str = this.xpathExpressionCompiled2.evaluate(node);
								else str = XMLCommons.newXPath().evaluate(this.xpathExpression2, node);
								
								for (String query : queries) {
									double value = getSimEngine().nameSim(query, str);
									if(value > max) max = value;
									if(DEBUG) System.out.println("        sim('" + query + "', '" + str + "') = " + value + " (" + max + ")");
								}
							}
							return (float) max;
						} catch (XPathExpressionException e) {
							// Non dovrebbe mai capitare!
							assert(false);
						} catch (SimilarityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}

		return 0;
	}

	private WSDLSimilarityEngine getSimEngine() throws SimilarityException {
		if(simEngine == null) simEngine = WSDLSimilarityEngine.getInstance();
		return simEngine;
	}
	
	private Document getDocumentDOM() {
		try {
			if(this.dom == null && schemaDocument != null) 
				this.dom = XMLCommons.newDocumentBuilder().parse(new InputSource(new StringReader(this.schemaDocument)));
		} catch(SAXException e) {
			// Non dovrebbe mai capitare!
			assert(false);
		} catch (IOException e) {
			// Non dovrebbe mai capitare!
			assert(false);
		} catch (ParserConfigurationException e) {
			// Non dovrebbe mai capitare!
			assert(false);
		}
		return this.dom;
	}
	
	public boolean isCoveredBy(Interest filter) {
		return this.equals(filter);
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((this.schemaDocument == null) ? 0 : this.schemaDocument.hashCode());
		result = PRIME * result + ((this.xpathExpression == null) ? 0 : this.xpathExpression.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		final InterestNameSimSpecificationFacet other = (InterestNameSimSpecificationFacet) obj;
		if (this.schemaDocument == null) {
			if (other.schemaDocument != null)
				return false;
		} else if (!XMLCommons.compare(this.getDocumentDOM(), other.getDocumentDOM()))
			return false;
		if (this.xpathExpression == null) {
			if (other.xpathExpression != null)
				return false;
		} else if (!this.xpathExpression.equals(other.xpathExpression))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "\n" + this.getClass().getSimpleName() + ": " + name;
	}
}
