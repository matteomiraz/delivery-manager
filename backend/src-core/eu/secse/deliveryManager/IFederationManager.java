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

import javax.ejb.Remote;

import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.exceptions.NotJoinedException;
import eu.secse.deliveryManager.model.PromotionDetails;

@Remote
public interface IFederationManager {

	String[] getJoinedFederations();

	void joinFederation(String federationName);

	void leaveFederation(String federationName) throws NotJoinedException;

	/** Share a service and all its specification facets */
	long promoteServiceSpecifications(String serviceId, String federationName) throws NotJoinedException, NotFoundException;
	
	/** Share a service and all its facets (specification or additional information) */
	long promoteServiceFacets(String serviceId, String federationName) throws NotJoinedException, NotFoundException;
	
	/** Share a particular additional information facet */
	long promoteFacetSpecification(String serviceId, String facetSpecificationSchemaId, String federationName) throws NotJoinedException, NotFoundException;

	long[] getPromotions(String federationName) throws NotJoinedException;

	PromotionDetails getDetails(long id);

	void discardPromotion(long id) throws NotFoundException;

	boolean isJoinedFederation(String federationName);

	PromotionDetails getPromotionByServiceId(String federationName, String serviceId) throws NotJoinedException, NotFoundException;

	PromotionDetails getPromotionByFacetSchemaId(String federationName, String serviceId, String facetSchemaId) throws NotJoinedException, NotFoundException;
}
