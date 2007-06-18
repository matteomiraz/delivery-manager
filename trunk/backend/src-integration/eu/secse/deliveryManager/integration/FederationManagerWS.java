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

import javax.annotation.EJB;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.secse.deliveryManager.IFederationManager;
import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.exceptions.NotJoinedException;
import eu.secse.deliveryManager.model.PromotionDetails;

@Stateless
@WebService(name = "IFederationManager", serviceName = "FederationManager", targetNamespace = "http://secse.eu/deliveryManager/federationManager")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL, parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
public class FederationManagerWS implements IFederationManagerWS {

	static final Log log = LogFactory.getLog(IFederationManagerWS.class);

	@EJB IFederationManager wrapped;

	/**
	 * @see eu.secse.deliveryManager.IFederationManager#discardPromotion(long)
	 */
	@WebMethod public void discardPromotion(
			@WebParam(name="promotionId") long promotionId) throws NotFoundException {
		log.debug("FederationManagerWS.discardPromotion()");
		wrapped.discardPromotion(promotionId);
	}

	/**
	 * @see eu.secse.deliveryManager.IFederationManager#getDetails(long)
	 */
	@WebMethod public PromotionDetails getDetails(
			@WebParam(name="promotionId") long promotionId) {
		log.debug("FederationManagerWS.getDetails()");
		return wrapped.getDetails(promotionId);
	}

	/**
	 * @see eu.secse.deliveryManager.IFederationManager#getJoinedFederations()
	 */
	@WebMethod public String[] getJoinedFederations() {
		log.debug("FederationManagerWS.getJoinedFederations()");
		return wrapped.getJoinedFederations();
	}

	/**
	 * @see eu.secse.deliveryManager.IFederationManager#getPromotions(java.lang.String)
	 */
	@WebMethod public long[] getPromotions(
			@WebParam(name="federationName") String federationName) throws NotJoinedException {
		log.debug("FederationManagerWS.getPromotions()");
		return wrapped.getPromotions(federationName);
	}

	/**
	 * @see eu.secse.deliveryManager.IFederationManager#isJoinedFederation(java.lang.String)
	 */
	@WebMethod public boolean isJoinedFederation(
			@WebParam(name="federationName") String federationName) {
		log.debug("FederationManagerWS.isJoinedFederation()");
		return wrapped.isJoinedFederation(federationName);
	}

	/**
	 * @see eu.secse.deliveryManager.IFederationManager#joinFederation(java.lang.String)
	 */
	@WebMethod public void joinFederation(
			@WebParam(name="federationName") String federationName) {
		log.debug("FederationManagerWS.joinFederation()");
		wrapped.joinFederation(federationName);
	}

	/**
	 * @see eu.secse.deliveryManager.IFederationManager#leaveFederation(java.lang.String)
	 */
	@WebMethod public void leaveFederation(
			@WebParam(name="federationName") String federationName) throws NotJoinedException {
		log.debug("FederationManagerWS.leaveFederation()");
		wrapped.leaveFederation(federationName);
	}

	/**
	 * @see eu.secse.deliveryManager.IFederationManager#promoteFacetSpecification(java.lang.String, java.lang.String, java.lang.String)
	 */
	@WebMethod public long promoteFacetSpecification(
			@WebParam(name="serviceId") String serviceId, 
			@WebParam(name="facetSpecificationSchemaId") String facetSpecificationSchemaId, 
			@WebParam(name="federationName") String federationName) throws NotJoinedException, NotFoundException {
		System.out.println("FederationManagerWS.getPromotionIdByElementId()");
		return wrapped.promoteFacetSpecification(serviceId, facetSpecificationSchemaId, federationName);
	}

	/**
	 * @see eu.secse.deliveryManager.IFederationManager#promoteServiceFacets(java.lang.String, java.lang.String)
	 */
	@WebMethod public long promoteServiceFacets(
			@WebParam(name="serviceId") String serviceId, 
			@WebParam(name="federationName") String federationName) throws NotJoinedException, NotFoundException {
		log.debug("FederationManagerWS.getPromotionIdByElementId()");
		return wrapped.promoteServiceFacets(serviceId, federationName);
	}

	/**
	 * @see eu.secse.deliveryManager.IFederationManager#promoteServiceSpecifications(java.lang.String, java.lang.String)
	 */
	@WebMethod public long promoteServiceSpecifications(
			@WebParam(name="serviceId") String serviceId, 
			@WebParam(name="federationName") String federationName) throws NotJoinedException, NotFoundException {
		log.debug("FederationManagerWS.getPromotionIdByElementId()");
		return wrapped.promoteServiceSpecifications(serviceId, federationName);
	}	

	@WebMethod 
	public PromotionDetails getPromotionByServiceId(
			@WebParam(name="federationName") String federationName, 
			@WebParam(name="serviceId") String serviceId) throws NotJoinedException, NotFoundException {
		log.debug("FederationManagerWS.getPromotionIdByServiceId()");
		return wrapped.getPromotionByServiceId(federationName, serviceId);
	}

	@WebMethod 
	public PromotionDetails getPromotionByFacetSchemaId(
			@WebParam(name="federationName") String federationName, 
			@WebParam(name="serviceId") String serviceId, 
			@WebParam(name="facetSchemaId") String facetSchemaId) throws NotJoinedException, NotFoundException {
		log.debug("FederationManagerWS.getPromotionIdByElementId()");
		return wrapped.getPromotionByFacetSchemaId(federationName, serviceId, facetSchemaId);
	}
}
