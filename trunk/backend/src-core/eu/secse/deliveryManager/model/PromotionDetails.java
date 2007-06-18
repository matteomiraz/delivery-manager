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

import java.io.Serializable;

import eu.secse.deliveryManager.IFederationManager;

/**
 * Rappresent a promotion of a particular element into a federation.
 * @see IFederationManager
 * @author matteo
 */
public class PromotionDetails implements Serializable {

	private static final long serialVersionUID = 2125288181252298261L;

	/** the id of this promotion */
	private long promotionId;
	
	/** the id of the service (required) */
	private String serviceId;
	/** are we sharing all additional information facets belonging to the serviceId? (valid only if facetSchemaId is null) */
	private boolean shareAllFacets;
	
	/** the id if the schema we are promoting (can be null, if we are not promoting any particular (none or all) facet additional information schema */
	private String facetSchemaId;

	/** the name of the federation */
	private String federationName;

	public PromotionDetails() { /* empty constructor */ }

	/**
	 * Promote a service in a federation. if shareAllFacets is true, we are sharing all additional information facets belonging to 
	 * this service.
	 */
	public PromotionDetails(long promotionId, String federationName, String serviceId, boolean shareAllFacets) {
		this.promotionId = promotionId;
		this.federationName = federationName;
		this.serviceId = serviceId;
		this.shareAllFacets = shareAllFacets;
		this.facetSchemaId = null;
	}

	/**
	 * Promote a particular additional information facets (belonging to a particular service) to the federation.
	 */
	public PromotionDetails(long promotionId, String federationName, String serviceId, String facetSchemaId) {
		this.promotionId = promotionId;
		this.federationName = federationName;
		this.serviceId = serviceId;
		this.shareAllFacets = false;
		this.facetSchemaId = facetSchemaId;
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
	 * @return the federationName
	 */
	public String getFederationName() {
		return federationName;
	}

	/**
	 * @param federationName the federationName to set
	 */
	public void setFederationName(String federationName) {
		this.federationName = federationName;
	}

	/**
	 * @return the promotionId
	 */
	public long getPromotionId() {
		return promotionId;
	}

	/**
	 * @param promotionId the promotionId to set
	 */
	public void setPromotionId(long promotionId) {
		this.promotionId = promotionId;
	}

	/**
	 * @return the serviceId
	 */
	public String getServiceId() {
		return serviceId;
	}

	/**
	 * @param serviceId the serviceId to set
	 */
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	/**
	 * @return the shareAllFacets
	 */
	public boolean isShareAllFacets() {
		return shareAllFacets;
	}

	/**
	 * @param shareAllFacets the shareAllFacets to set
	 */
	public void setShareAllFacets(boolean shareAllFacets) {
		this.shareAllFacets = shareAllFacets;
	}

	
}
