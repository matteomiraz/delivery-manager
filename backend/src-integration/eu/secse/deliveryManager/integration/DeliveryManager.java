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

import eu.secse.deliveryManager.IInterestManager;
import eu.secse.deliveryManager.IShareManager;

@Stateless
@WebService(name = "IDeliveryManager", serviceName = "DeliveryManager", targetNamespace = "http://secse.eu/deliveryManager/deliveryManager")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL, parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
public class DeliveryManager implements IDeliveryManager {

	private static final Log log = LogFactory.getLog(IDeliveryManager.class);

	@EJB private IShareManager iSha;
	@EJB private IInterestManager iInt;
	
//	@WebMethod public GenericInterest getInterestById(
//	@WebParam(name="InterestID") long id) 

	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.integration.IDeliveryManager#getAllInterestIds()
	 */
	@WebMethod public long[] getAllInterestIds() {
		log.debug("DeliveryManager.getAllInterestIds()");
		return iInt.getAllInterestIds();
	}
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.integration.IDeliveryManager#getInterestById(long)
	 */
	@WebMethod public eu.secse.deliveryManager.GenericInterest getInterestById(
			@WebParam(name="interestID") long arg0) throws eu.secse.deliveryManager.exceptions.NotSubscribedException {
		log.debug("DeliveryManager.getInterestById()");
		return iInt.getInterestById(arg0);
	}

	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.integration.IDeliveryManager#subscribeAdditionalInformationFacet(java.lang.String, java.lang.String, java.lang.String)
	 */
	@WebMethod public long subscribeAdditionalInformationFacet(
			@WebParam(name="serviceID") String arg0, 
			@WebParam(name="facetSchema") String arg1, 
			@WebParam(name="xpath") String arg2)
	throws eu.secse.deliveryManager.exceptions.NotSubscribedException {
		log.debug("DeliveryManager.subscribeAdditionalInformationFacet()");
		return iInt.subscribeAdditionalInformationFacet(arg0, arg1, arg2);
	}
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.integration.IDeliveryManager#subscribeAdditionalInformationFacetById(java.lang.String, java.lang.String)
	 */
	@WebMethod public long subscribeAdditionalInformationFacetById(
			@WebParam(name="serviceID") String arg0, 
			@WebParam(name="facetSchemaID") String arg1) 
	throws eu.secse.deliveryManager.exceptions.NotSubscribedException {
		log.debug("DeliveryManager.subscribeAdditionalInformationFacetById()");
		return iInt.subscribeAdditionalInformationFacetById(arg0, arg1);
	}
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.integration.IDeliveryManager#subscribeService(java.lang.String)
	 */
	@WebMethod public long subscribeService(
			@WebParam(name="serviceID") String arg0) {
		log.debug("DeliveryManager.subscribeService()");
		return iInt.subscribeService(arg0);
	}
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.integration.IDeliveryManager#subscribeSpecificationFacet(java.lang.String, java.lang.String)
	 */
	@WebMethod public long subscribeSpecificationFacet(
			@WebParam(name="facetSchema") String arg0, 
			@WebParam(name="xpath") String arg1)
	throws eu.secse.deliveryManager.exceptions.NotSubscribedException {
		log.debug("DeliveryManager.subscribeSpecificationFacet()");
		return iInt.subscribeSpecificationFacet(arg0, arg1);
	}
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.integration.IDeliveryManager#unsubscribe(long)
	 */
	@WebMethod public void unsubscribe(
			@WebParam(name="interestID") long arg0) 
	throws eu.secse.deliveryManager.exceptions.NotSubscribedException {
		log.debug("DeliveryManager.unsubscribe()");
		iInt.unsubscribe(arg0);
	}
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.integration.IDeliveryManager#shareAllServiceAdditionalInformations(java.lang.String)
	 */
	@WebMethod public void shareAllServiceAdditionalInformations(
			@WebParam(name="serviceID") String arg0)
	throws eu.secse.deliveryManager.exceptions.NotFoundException {
		log.debug("DeliveryManager.shareAllServiceAdditionalInformations()");
		iSha.shareAllServiceAdditionalInformations(arg0);
	}
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.integration.IDeliveryManager#shareServiceAdditionalInformation(java.lang.String, java.lang.String)
	 */
	@WebMethod public void shareServiceAdditionalInformation(
			@WebParam(name="serviceID") String arg0, 
			@WebParam(name="facetSchemaID") String arg1)
	throws eu.secse.deliveryManager.exceptions.NotFoundException {
		log.debug("DeliveryManager.shareServiceAdditionalInformation()");
		iSha.shareServiceAdditionalInformation(arg0, arg1);
	}
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.integration.IDeliveryManager#shareServiceSpecifications(java.lang.String)
	 */
	@WebMethod public void shareServiceSpecifications(
			@WebParam(name="serviceID") String serviceID)
	throws eu.secse.deliveryManager.exceptions.NotFoundException {
		log.debug("DeliveryManager.shareServiceSpecifications()");
		iSha.shareServiceSpecifications(serviceID);
	}	
}
