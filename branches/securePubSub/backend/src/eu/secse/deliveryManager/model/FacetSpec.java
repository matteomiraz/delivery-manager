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

package eu.secse.deliveryManager.model;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This is an embeddable facet Schema.
 * If you want to send a specification service type, you have to 
 * add an instance of this class in a DService.
 * If you want to send an additional information type, you have to use
 * the <code>DFacetType</code> class. 
 *
 * @author matteo
 */
public class FacetSpec implements Serializable {

	private static final long serialVersionUID = -3944382103249919707L;

	/** Facet Specification Schema unique id */
	private String schemaID;

	/** Facet Specification Schema name */
	private String name;

	/** Facet Specification Schema */
	private String document;
	/** Facet Specification Schema as DOM Document */
	private transient Document dom;

	/** The timestamp of the facet (managed by the delivery manager) 
	 * Aiuta a capire quale versione del FacetXml è associata a questa Facet*/
	private Date dmTimestamp;
	
	private String typeName;
	
	private String timestamp;
	private String isoTimestamp;
	
	protected FacetSpecXML facetSpecificationXML;

	/**
	 * @param schemaID
	 * @param name
	 * @param document
	 * @param typeName
	 * @param facetSpecificationXML
	 */
	public FacetSpec(String schemaID, String name, String document, String typeName, FacetSpecXML facetSpecificationXML, String timestamp, String isoTimestamp) {
		super();
		this.schemaID = schemaID;
		this.name = name;
		this.document = document;
		this.typeName = typeName;
		this.facetSpecificationXML = facetSpecificationXML;
		this.timestamp = timestamp;
		this.isoTimestamp = isoTimestamp;
		this.dmTimestamp = null;
	}

	/**
	 * @return the document
	 */
	public String getDocument() {
		return document;
	}

	/**
	 * @return the facetSpecificationXML
	 */
	public FacetSpecXML getFacetSpecificationXML() {
		return facetSpecificationXML;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the schemaID
	 */
	public String getSchemaID() {
		return schemaID;
	}

	/**
	 * @return the typeName
	 */
	public String getTypeName() {
		return typeName;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	
	public String getIsoTimestamp() {
		return isoTimestamp;
	}

	public Document getDocumentDOM() {
		try {
			if(this.dom == null) 
				this.dom = XMLCommons.newDocumentBuilder().parse(new InputSource(new StringReader(this.document)));
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

	@Override
	public String toString() {
		return "EFacet schemaID:" + this.schemaID  + "; name:" + this.name;
	}
	
	public Date getDmTimestamp() {
		return dmTimestamp;
	}
	
	public void setDmTimestamp(Date dmTimestamp) {
		this.dmTimestamp = dmTimestamp;
	}
}
