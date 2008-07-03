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

import javax.ejb.Remote;

import eu.secse.deliveryManager.core.PromotionDetails;
import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.exceptions.NotJoinedException;
import eu.secse.deliveryManager.exceptions.PromotionNotValidException;

@Remote
public interface IFederationManagerWS {

	/**
	 * Discard a promotion made.
	 * @param promotionId the id of the promotion 
	 * @throws NotFoundException the promotion is not found
	 */
	
	public void discardPromotion(long promotionId) throws NotFoundException;

	/**
	 * Get the detail of a promotion
	 * @param promotionId the id of the promotion
	 * @return the details for the promotion
	 */
	public PromotionDetails getDetails(long promotionId);

	/**
	 * Get the list of the federations joined
	 * @return the list of the federations
	 */
	//OLD: public String[] getJoinedFederations();
	public FederationDetails[] getJoinedFederations();

	/**
	 * Get the list of the promotions made in a federation
	 * @param federationName the id of the federation
	 * @return the ids of the promotions made in that federation
	 * @throws NotJoinedException the federation is not joined
	 */
	public long[] getPromotions(String federationName) throws NotJoinedException;

	/**
	 * Check if the delivery manager is joined in a federation
	 * @param federationName the federation id
	 * @return true if the delivery manager is in that federation.
	 */
	public boolean isJoinedFederation(String federationName);

	/**
	 * Join a particular federation
	 * @param federationId the id of the federation to join
	 */	
	//OLD public void joinFederation(@WebParam(name="federationName") String federationName);
	public void joinFederation(String federationId);

	/**
	 * This operation allows leaving the federation with the given name. If the delivery manager does not belong to that federation, a NotJoinedException is thrown.
	 * @param federationId the Id of the federation to leave
	 * @throws NotJoinedException the delivery manager is not in that federation
	 */
	//OLD public void leaveFederation(@WebParam(name="federationName") String federationName) throws NotJoinedException;
	public void leaveFederation(String federationId) throws NotJoinedException;

	/**
	 * Promote an additional-information facet in the specified federation
	 * @param serviceId the id of the service to promote
	 * @param facetSpecificationSchemaId the id of the additional information facet
	 * @param federationId the name of the federation
	 * @return the id of the promotion
	 * @throws NotJoinedException if the delivery manager is not in the specified federation 
	 * @throws NotFoundException if there are errors while promoting the specified element 
	 */
	//OLD public long promoteFacetSpecification(@WebParam(name="serviceId") String serviceId, @WebParam(name="facetSpecificationSchemaId") String facetSpecificationSchemaId, @WebParam(name="federationName") String federationName) throws NotJoinedException, NotFoundException;
	public long promoteFacetSpecification(String serviceId, String facetSpecificationSchemaId,  String federationId) throws NotJoinedException, NotFoundException, PromotionNotValidException;
	
	/**
	 * Promote a service and all its facets in the specified federation
	 * @param serviceId the id of the service to promote
	 * @param federationId the id of the federation
	 * @return the id of the promotion
	 * @throws NotJoinedException if the delivery manager is not in the specified federation 
	 * @throws NotFoundException if there are errors while promoting the specified element 
	 */
	//OLD public long promoteServiceFacets(@WebParam(name="serviceId") String serviceId, @WebParam(name="federationName") String federationName) throws NotJoinedException, NotFoundException;
	public long promoteServiceFacets(String serviceId, String federationId) throws NotJoinedException, NotFoundException, PromotionNotValidException;
	
	/**
	 * Promote a service (with all its non-additional-information facets) in the specified federation
	 * @param serviceId the id of the service to promote
	 * @param federationId the id of the federation
	 * @return the id of the promotion
	 * @throws NotJoinedException if the delivery manager is not in the specified federation 
	 * @throws NotFoundException if there are errors while promoting the specified element 
	 */
	//OLD public long promoteServiceSpecifications(@WebParam(name="serviceId") String serviceId, @WebParam(name="federationName") String federationName) throws NotJoinedException, NotFoundException;
	public long promoteServiceSpecifications( String serviceId,  String federationId) throws NotJoinedException, NotFoundException, PromotionNotValidException;
		
	
	/**
	 * This method allows the user to retrieve the promotion details of a given 
	 * service in a federation.  
	 * 
	 * @param federationId the id of the federation
	 * @param serviceId the id of the service
	 * @returns the details of the promotion
	 * @throws NotFoundException if such a promotion doesn't exist
	 * @throws NotJoinedException if the federation is not joined 
	 * 
	 */
	//OLD public PromotionDetails getPromotionByServiceId(@WebParam(name="federationName") String federationId, @WebParam(name="serviceId") String serviceId) throws NotJoinedException, NotFoundException;
	public PromotionDetails getPromotionByServiceId( String federationId, String serviceId) throws NotJoinedException, NotFoundException;
	
	/**
	 * This method allows the user to retrieve the promotion details of a given 
	 * facet schema in a federation.  
	 * 
	 * @param federationId the id of the federation
	 * @param serviceId the id of the service
	 * @param facetSchemaId the id of the facet schema
	 * @return
	 * @throws NotJoinedException if the federation is not joined 
	 * @throws NotFoundException if such a promotion doesn't exist
	 */
	//OLD public PromotionDetails getPromotionByFacetSchemaId(@WebParam(name="federationName") String federationName, @WebParam(name="serviceId") String serviceId, @WebParam(name="facetSchemaId") String facetSchemaId) throws NotJoinedException, NotFoundException;
	public PromotionDetails getPromotionByFacetSchemaId( String federationId,  String serviceId, String facetSchemaId) throws NotJoinedException, NotFoundException;
	
	//NEW METHODS:
	
	/**
	 * This operation allows to discovery the set of federation typology supported by the delivery manager. Currently
	 * delivery manager supports PubSub and Gossip federation typology but it is possible to
	 * create other federation typology and plug them in the delivery manager architecture    
	 */
	public String[] getSupportedTypology();
	
	
	
	/** 
	 * This operation allows one to create and join in federation with the specified name and typology. 
	 * If it is not possible to join that federation (e.g. the type of the selected federation is not supported 
	 * by the delivery manager), a NotJoinedException is thrown (and the creation of that federation is aborted).
	 *  The method returns the identifier of the created federation.
	 * 
	 */
	public String createFederation(String name,String type) throws NotJoinedException;
	
	/**
	  * This method first verifies if there is at least one FederationDirectory in
	  * the endpoints list. If so, it retrieves from the Federation Directory
	  * all federations. 
	  * 
	  * @author lili
	  * 
	  * @return a collection of all federations
	  * 
	  */
	
	public FederationDetails[] getAllFederations();
	
	/**
	 * This method allow us to retrieve the information about a particular federation, that matches
	 * its name
	 * 
	 * @author lili
	 * @param nameRegExp the regular expression
	 * @return federations that match the given regular expression
	 */
	
	public FederationDetails[] searchFederationByName(String nameRegExp);
	
	/**
	 * This method allow us to retrieve the information about a particular federation, whose unique
	 * identifier is known. If that federation does not exists a null value is returned.
	 * 
	 * @author lili
	 * @param uid
	 * @return
	 */
	public FederationDetails searchFederationByUID(String uid);
	
}
