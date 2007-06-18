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

import eu.secse.deliveryManager.IUpdate;

@Stateless
@WebService(name = "IUpdate", serviceName = "Update", targetNamespace = "http://secse.eu/deliveryManager/update")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL, parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
public class UpdateWS implements IUpdateWS {

	static final Log log = LogFactory.getLog(IUpdateWS.class);

	@EJB IUpdate iUpd;

	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.integration.IUpdateWS#addFacetSpecificationSchema(java.lang.String, java.lang.String, boolean, long)
	 */
	@WebMethod public void addFacetSpecificationSchema(
			@WebParam(name="serviceId") String serviceId, 
			@WebParam(name="schemaId") String schemaId, 
			@WebParam(name="isAdditionalInformation") boolean isAdditionalInformation, 
			@WebParam(name="providerId") long providerId) {
		log.debug("UpdateWS.addFacetSpecificationSchema()");
		iUpd.addFacetSpecificationSchema(serviceId, schemaId, isAdditionalInformation, providerId);
	}

	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.integration.IUpdateWS#addFacetSpecificationXML(java.lang.String, java.lang.String, java.lang.String, boolean, long)
	 */
	@WebMethod public void addFacetSpecificationXML(
			@WebParam(name="serviceId") String serviceId, 
			@WebParam(name="schemaId") String schemaId, 
			@WebParam(name="xmlId") String xmlId, 
			@WebParam(name="isAdditionalInformation") boolean isAdditionalInformation, 
			@WebParam(name="providerId") long providerId) {
		log.debug("UpdateWS.addFacetSpecificationXML()");
		iUpd.addFacetSpecificationXML(serviceId, schemaId, xmlId, isAdditionalInformation, providerId);
	}

	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.integration.IUpdateWS#addService(java.lang.String, long)
	 */
	@WebMethod public void addService(
			@WebParam(name="serviceId") String serviceId, 
			@WebParam(name="providerId") long providerId) {
		log.debug("UpdateWS.addService()");
		iUpd.addService(serviceId, providerId);
	}

	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.integration.IUpdateWS#deleteFacetFacetSpecificationXML(java.lang.String, java.lang.String, java.lang.String, boolean, long)
	 */
	@WebMethod public void deleteFacetFacetSpecificationXML(
			@WebParam(name="serviceId") String serviceId, 
			@WebParam(name="schemaId") String schemaId, 
			@WebParam(name="xmlId") String xmlId, 
			@WebParam(name="wasAdditionalInformation") boolean wasAdditionalInformation, 
			@WebParam(name="providerId") long providerId) {
		log.debug("UpdateWS.deleteFacetFacetSpecificationXML()");
		iUpd.deleteFacetFacetSpecificationXML(serviceId, schemaId, xmlId, wasAdditionalInformation, providerId);
	}

	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.integration.IUpdateWS#deleteFacetSpecificationSchema(java.lang.String, java.lang.String, boolean, long)
	 */
	@WebMethod public void deleteFacetSpecificationSchema(
			@WebParam(name="serviceId") String serviceId, 
			@WebParam(name="schemaId") String schemaId, 
			@WebParam(name="wasAdditionalInformation") boolean wasAdditionalInformation, 
			@WebParam(name="providerId") long providerId) {
		log.debug("UpdateWS.deleteFacetSpecificationSchema()");
		iUpd.deleteFacetSpecificationSchema(serviceId, schemaId, wasAdditionalInformation, providerId);
	}

	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.integration.IUpdateWS#deleteService(java.lang.String, long)
	 */
	@WebMethod public void deleteService(
			@WebParam(name="serviceId") String serviceId, 
			@WebParam(name="providerId") long providerId) {
		log.debug("UpdateWS.deleteService()");
		iUpd.deleteService(serviceId, providerId);
	}	
}
