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


package eu.secse.deliveryManager;

import java.io.Serializable;

/**
 * This class hold an interest. (used by eu.secse.deliveryManager.IDeliveryManager)
 * @author matteo
 */
public class GenericInterest implements Serializable {

	private static final long serialVersionUID = -2540423402682854638L;
	
	private String serviceID;
	private String facetSchemaId;
	private String facetSchema;
	private String xpath;
	private boolean additionalInformation;
	
	public GenericInterest() { /* empty constructor */ }

	/**
	 * @param serviceID
	 * @param facetSchemaId
	 * @param facetSchema
	 * @param xpath
	 * @param additionalInformation
	 */
	public GenericInterest(String serviceID, String facetSchemaId, String facetSchema, String xpath, boolean additionalInformation) {
		super();
		this.serviceID = serviceID;
		this.facetSchemaId = facetSchemaId;
		this.facetSchema = facetSchema;
		this.xpath = xpath;
		this.additionalInformation = additionalInformation;
	}

	/**
	 * @return the additionalInformation
	 */
	public boolean isAdditionalInformation() {
		return additionalInformation;
	}

	/**
	 * @param additionalInformation the additionalInformation to set
	 */
	public void setAdditionalInformation(boolean additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	/**
	 * @return the facetSchema
	 */
	public String getFacetSchema() {
		return facetSchema;
	}

	/**
	 * @param facetSchema the facetSchema to set
	 */
	public void setFacetSchema(String facetSchema) {
		this.facetSchema = facetSchema;
	}

	/**
	 * @return the facetSchemaId
	 */
	public String getFacetSchemaId() {
		return facetSchemaId;
	}

	/**
	 * @param facetSchemaId the facetSchemaId to set
	 */
	public void setFacetSchemaId(String facetSchemaId) {
		this.facetSchemaId = facetSchemaId;
	}

	/**
	 * @return the serviceID
	 */
	public String getServiceID() {
		return serviceID;
	}

	/**
	 * @param serviceID the serviceID to set
	 */
	public void setServiceID(String serviceID) {
		this.serviceID = serviceID;
	}

	/**
	 * @return the xpath
	 */
	public String getXpath() {
		return xpath;
	}

	/**
	 * @param xpath the xpath to set
	 */
	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	
}