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

package eu.secse.deliveryManager.webServices;

import java.io.Serializable;

import eu.secse.deliveryManager.core.FacetInterest;

/**
 * This class hold an interest. (used by eu.secse.deliveryManager.IDeliveryManager)
 * @author matteo
 */
public class GenericInterest implements Serializable {

	private static final long serialVersionUID = -2540423402682854638L;
	
	private String serviceID;
	private String facetSchemaId;
	private FacetInterest[] interests;
	private boolean additionalInformation;
	private String description;

	public GenericInterest() { }
	
	public GenericInterest(String serviceID, String facetSchemaId, FacetInterest[] interests, boolean additionalInformation, String description) {
		super();
		this.serviceID = serviceID;
		this.facetSchemaId = facetSchemaId;
		this.interests = interests;
		this.description = description;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public FacetInterest[] getInterests() {
		return interests;
	}
	
	public void setInterests(FacetInterest[] interests) {
		this.interests = interests;
	}
}