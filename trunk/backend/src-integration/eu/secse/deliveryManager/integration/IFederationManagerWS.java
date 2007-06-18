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


package eu.secse.deliveryManager.integration;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebParam;

import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.exceptions.NotJoinedException;
import eu.secse.deliveryManager.model.PromotionDetails;

@Remote
public interface IFederationManagerWS {

	/**
	 * Discard a promotion made.
	 * @param promotionId the id of the promotion 
	 * @throws NotFoundException the promotion is not found
	 */
	@WebMethod
	public void discardPromotion(@WebParam(name="promotionId") long promotionId) throws NotFoundException;

	/**
	 * Get the detail of a promotion
	 * @param promotionId the id of the promotion
	 * @return the details for the promotion
	 */
	@WebMethod
	public PromotionDetails getDetails(@WebParam(name="promotionId") long promotionId);

	/**
	 * Get the list of the federations joined
	 * @return the list of the federations
	 */
	@WebMethod
	public String[] getJoinedFederations();

	/**
	 * Get the list of the promotions made in a federation
	 * @param federationName the name of the federation
	 * @return the ids of the promotions made in that federation
	 * @throws NotJoinedException the federation is not joined
	 */
	@WebMethod
	public long[] getPromotions(@WebParam(name="federationName") String federationName) throws NotJoinedException;

	/**
	 * Check if the delivery manager is joined in a federation
	 * @param federationName the federation name
	 * @return true if the delivery manager is in that federation.
	 */
	@WebMethod
	public boolean isJoinedFederation(@WebParam(name="federationName") String federationName);

	/**
	 * Join a particular federation
	 * @param federationName the name of the federation to join
	 */
	@WebMethod
	public void joinFederation(@WebParam(name="federationName") String federationName);

	/**
	 * Leave a federation
	 * @param federationName the name of the federation to leave
	 * @throws NotJoinedException the delivery manager is not in that federation
	 */
	@WebMethod
	public void leaveFederation(@WebParam(name="federationName") String federationName) throws NotJoinedException;

	/**
	 * Promote an additional-information facet in the specified federation
	 * @param serviceId the id of the service to promote
	 * @param facetSpecificationSchemaId the id of the additional information facet
	 * @param federationName the name of the federation
	 * @return the id of the promotion
	 * @throws NotJoinedException if the delivery manager is not in the specified federation 
	 * @throws NotFoundException if there are errors while promoting the specified element 
	 */
	@WebMethod
	public long promoteFacetSpecification(@WebParam(name="serviceId") String serviceId, @WebParam(name="facetSpecificationSchemaId") String facetSpecificationSchemaId, @WebParam(name="federationName") String federationName) throws NotJoinedException, NotFoundException;

	/**
	 * Promote a service and all its facets in the specified federation
	 * @param serviceId the id of the service to promote
	 * @param federationName the name of the federation
	 * @return the id of the promotion
	 * @throws NotJoinedException if the delivery manager is not in the specified federation 
	 * @throws NotFoundException if there are errors while promoting the specified element 
	 */
	@WebMethod
	public long promoteServiceFacets(@WebParam(name="serviceId") String serviceId, @WebParam(name="federationName") String federationName) throws NotJoinedException, NotFoundException;

	/**
	 * Promote a service (with all its non-additional-information facets) in the specified federation
	 * @param serviceId the id of the service to promote
	 * @param federationName the name of the federation
	 * @return the id of the promotion
	 * @throws NotJoinedException if the delivery manager is not in the specified federation 
	 * @throws NotFoundException if there are errors while promoting the specified element 
	 */
	@WebMethod
	public long promoteServiceSpecifications(@WebParam(name="serviceId") String serviceId, @WebParam(name="federationName") String federationName) throws NotJoinedException, NotFoundException;

	@WebMethod
	public PromotionDetails getPromotionByServiceId(@WebParam(name="federationName") String federationName, @WebParam(name="serviceId") String serviceId) throws NotJoinedException, NotFoundException;

	@WebMethod
	public PromotionDetails getPromotionByFacetSchemaId(@WebParam(name="federationName") String federationName, @WebParam(name="serviceId") String serviceId, @WebParam(name="facetSchemaId") String facetSchemaId) throws NotJoinedException, NotFoundException;
}
