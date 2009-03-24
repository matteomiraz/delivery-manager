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

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class FacetSpecXML implements Serializable {

	private static final long serialVersionUID = 1003133921619875085L;

	/** Facet Specification XML id */
	private String xmlID;
	
	/** Facet Specification XML name */
	private String name;

	/** Facet Document as string */
	private String document;
	/** Facet Document as DOM Document */
	private transient Document dom;

	private String timestamp;
	private String isoTimestamp;
	
	/**
	 * @param xmlID
	 * @param name
	 * @param document
	 */
	public FacetSpecXML(String xmlID, String name, String document, String timestamp, String isoTimestamp) {
		super();
		this.xmlID = xmlID;
		this.name = name;
		this.document = document;
		this.timestamp = timestamp;
		this.isoTimestamp = isoTimestamp;
	}

	/**
	 * @return the document
	 */
	public String getDocument() {
		return document;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the xmlID
	 */
	public String getXmlID() {
		return xmlID;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	
	public String getIsoTimestamp() {
		return isoTimestamp;
	}

	/**
	 * Get the XML document of the facet instance as a DOM document
	 * @return the XML document of the facet instance as a DOM document
	 */
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
		return this.xmlID.toString();
	}
}
