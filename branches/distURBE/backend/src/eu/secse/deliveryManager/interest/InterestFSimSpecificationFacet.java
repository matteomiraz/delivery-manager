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
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.model.FacetSpec;
import eu.secse.deliveryManager.model.FacetSpecXML;
import eu.secse.deliveryManager.model.XMLCommons;

public class InterestFSimSpecificationFacet implements Interest {

	private static final long serialVersionUID = 8684903789784507923L;

	/** Facet Specification Schema Document */
	private final String schemaDocument;
	/** Facet Specification Schema Document as DOM Document */
	private transient Document dom;

	private final String query;
	private final double threshold;
	
	private transient WSDLSimilarityEngine simEngine;
	
	public InterestFSimSpecificationFacet(String facetSpecificationSchemaDocument, String query, double threshold) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		this.query = query;
		this.threshold = threshold;
		
		this.schemaDocument = facetSpecificationSchemaDocument;
		if(schemaDocument != null) this.dom = XMLCommons.newDocumentBuilder().parse(new InputSource(new StringReader(this.schemaDocument)));

	}

	public String getSchemaDocument() {
		return this.schemaDocument;
	}
	
	public boolean matches(Deliverable elem) {
		if (elem instanceof DService) {
			DService srv = (DService) elem;

			if(srv.getSpecType() == null) return false;

			for (FacetSpec facet : srv.getSpecType()) {
				FacetSpecXML dfs = facet.getFacetSpecificationXML();

				// check if the schema is present or if is the same
				if (schemaDocument == null  || XMLCommons.compare(this.getDocumentDOM(), facet.getDocumentDOM())) {
					try {
						double value = getSimEngine().fSim(query, dfs.getDocument());
						System.out.println("fSim = " + value);

						if(value >= threshold) return true;
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

		return false;
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
		final int prime = 31;
		int result = 1;
		result = prime * result + ((query == null) ? 0 : query.hashCode());
		result = prime * result
				+ ((schemaDocument == null) ? 0 : schemaDocument.hashCode());
		long temp;
		temp = Double.doubleToLongBits(threshold);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InterestFSimSpecificationFacet other = (InterestFSimSpecificationFacet) obj;
		if (query == null) {
			if (other.query != null)
				return false;
		} else if (!query.equals(other.query))
			return false;
		if (schemaDocument == null) {
			if (other.schemaDocument != null)
				return false;
		} else if (!schemaDocument.equals(other.schemaDocument))
			return false;
		if (Double.doubleToLongBits(threshold) != Double
				.doubleToLongBits(other.threshold))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
