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

import java.io.Serializable;

public class FacetAddInfo extends FacetSpec implements Deliverable {

	private static final long serialVersionUID = -5507984326289576008L;

	private static final String TYPE = "FACET";

	/** Related service's id */
	private String serviceID;	
	
	/** facet type lease */
	private Serializable info;

	/**
	 * @param schemaID
	 * @param name
	 * @param document
	 * @param typeName
	 * @param facetSpecificationXML
	 * @param serviceID
	 * @param info
	 * @param dmTimestamp 
	 */
	public FacetAddInfo(String schemaID, String name, String document, String typeName, FacetSpecXML facetSpecificationXML, String serviceID, Serializable info, String timestamp, String isoTimestamp) {
		super(schemaID, name, document, typeName, facetSpecificationXML, timestamp, isoTimestamp);
		this.serviceID = serviceID;
		this.info = info;
	}

	/**
	 * @return the info
	 */
	public Serializable getInfo() {
		return info;
	}
	
	/**
	 * @return the serviceID
	 */
	public String getServiceID() {
		return serviceID;
	}

	public void setInfo(Serializable info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return "DFacet schemaID:" + this.getSchemaID()  + "; name:" + this.getName() + "; serviceID:" + this.serviceID;
	}
	
	public String getType() {
		return TYPE;
	}
}
