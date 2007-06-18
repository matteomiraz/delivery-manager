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


package eu.secse.deliveryManager.model;

import java.util.Date;

public class DFacetSpecificationSchema extends EFacetSpecificationSchema implements Deliverable {

	private static final long serialVersionUID = 3795447056447304549L;

	/** Related service's id */
	private String serviceID;	
	
	/** facet type lease */
	private Date lease;

	/**
	 * @param schemaID
	 * @param name
	 * @param document
	 * @param typeName
	 * @param facetSpecificationXML
	 * @param serviceID
	 * @param lease
	 */
	public DFacetSpecificationSchema(String schemaID, String name, String document, String typeName, EFacetSpecificationXML facetSpecificationXML, String serviceID, Date lease, String timestamp, String isoTimestamp) {
		super(schemaID, name, document, typeName, facetSpecificationXML, timestamp, isoTimestamp);
		this.serviceID = serviceID;
		this.lease = lease;
	}

	/**
	 * @return the lease
	 */
	public Date getLease() {
		return lease;
	}

	/**
	 * @return the serviceID
	 */
	public String getServiceID() {
		return serviceID;
	}

	public void setLease(Date lease) {
		this.lease = lease;
	}

	@Override
	public String toString() {
		return "DFacet schemaID:" + this.getSchemaID()  + "; name:" + this.getName() + "; serviceID:" + this.serviceID;
	}
}
