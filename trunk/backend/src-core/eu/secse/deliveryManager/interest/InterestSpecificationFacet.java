/* This file is part of Delivery Manager.
 * (c) 2006 Matteo Miraz, Politecnico di Milano
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

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.model.EFacetSpecificationSchema;
import eu.secse.deliveryManager.model.EFacetSpecificationXML;
import eu.secse.deliveryManager.model.XMLCommons;

/**
 * This class allows to declare an interest on some services that has a particular specification facet.
 * The interest is higly configurable, allowing the user to specify different tipology of filters.
 * Note that, even if each parameter is optional, it <b>is required that either the schema or the xpath is not null</b>.<br>
 * List of possible uses and their meaning:
 * <ul>
 *   <li><b>retrive all facet with a particular schema</b>: specify only the facetSchema;
 *   <li><b>retrive all facet with an xml that has some properties</b>: specify only the xpath;
 *   <li><b>retrive all facet with a particular xsd and an xml that has some properties,</b>: specify both the facetSchema and the xpath;
 * </ul>
 */
public class InterestSpecificationFacet implements Interest {

	private static final long serialVersionUID = 8684903789784507923L;

	/** Facet Specification Schema Document */
	private String schemaDocument;
	/** Facet Specification Schema Document as DOM Document */
	private transient Document dom;

	/** shall we compile the xpath? */
	private static final boolean COMPILE_XPATH = true;
	
	/** XPath Header: <b>XPATH_HEADER</b> + xpathExpression + XPATH_FOOTER */
	private static final String XPATH_HEADER = "boolean(";
	/** XPath Footer: XPATH_HEADER + xpathExpression + <b>XPATH_FOOTER</b> */
	private static final String XPATH_FOOTER = ")";
	
	/** XPath Expression: XPATH_HEADER + <b>xpathExpression</b> + XPATH_FOOTER */
	private String facetXmlXpath;

	/** XPath Compiled Expression: XPATH_HEADER + <b>xpathExpression</b> + XPATH_FOOTER */
	private transient XPathExpression xpathExpression;
	
	public InterestSpecificationFacet(String facetSpecificationSchemaDocument, String xpath) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		this.schemaDocument = facetSpecificationSchemaDocument;
		if(schemaDocument != null) this.dom = XMLCommons.newDocumentBuilder().parse(new InputSource(new StringReader(this.schemaDocument)));

		this.facetXmlXpath = xpath;
		// check the xpath
		if(facetXmlXpath != null) this.xpathExpression = XPathFactory.newInstance().newXPath().compile(XPATH_HEADER + xpath + XPATH_FOOTER);
		// free memory
		this.xpathExpression = null;

	}

	public String getSchemaDocument() {
		return this.schemaDocument;
	}
	
	public String getFacetXmlXpath() {
		return this.facetXmlXpath;
	}
	
	public boolean matches(Deliverable elem) {
		if (elem instanceof DService) {
			DService srv = (DService) elem;
			
			for (EFacetSpecificationSchema facet : srv.getSpecType()) {
				EFacetSpecificationXML dfs = facet.getFacetSpecificationXML();
				
				// if is expressed an xpath constraint, check if the xml is present
				if(xpathExpression == null || dfs != null) {
					// check if the schema is present or if is the same
					if (schemaDocument == null  || XMLCommons.compare(this.getDocumentDOM(), facet.getDocumentDOM())) {
						
						// if no xpath constraint, this facet matches to the filter!
						if(xpathExpression == null) return true;
						
						try {	//	compile the xpath expression
							if(COMPILE_XPATH && this.facetXmlXpath != null && this.xpathExpression == null) 
								this.xpathExpression = XPathFactory.newInstance().newXPath().compile(XPATH_HEADER + this.facetXmlXpath + XPATH_FOOTER);
						} catch (Exception e) {
							// Non dovrebbe mai capitare!
							assert(false);
						}

						try {
							if(COMPILE_XPATH) if((Boolean) this.xpathExpression.evaluate(dfs.getDocumentDOM(), XPathConstants.BOOLEAN)) return true;
							else if((Boolean) XMLCommons.newXPath().evaluate(XPATH_HEADER + this.facetXmlXpath + XPATH_FOOTER, dfs.getDocumentDOM(), XPathConstants.BOOLEAN)) return true;
						} catch (XPathExpressionException e) {
							// Non dovrebbe mai capitare!
							assert(false);
						}
					}
				}
			}
		}

		return false;
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
		result = PRIME * result + ((this.facetXmlXpath == null) ? 0 : this.facetXmlXpath.hashCode());
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
		final InterestSpecificationFacet other = (InterestSpecificationFacet) obj;
		if (this.schemaDocument == null) {
			if (other.schemaDocument != null)
				return false;
		} else if (!XMLCommons.compare(this.getDocumentDOM(), other.getDocumentDOM()))
			return false;
		if (this.facetXmlXpath == null) {
			if (other.facetXmlXpath != null)
				return false;
		} else if (!this.facetXmlXpath.equals(other.facetXmlXpath))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": type=" + this.schemaDocument + " xpath=" + this.facetXmlXpath;
	}
}
