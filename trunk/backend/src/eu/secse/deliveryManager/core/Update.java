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

package eu.secse.deliveryManager.core;


import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.secse.deliveryManager.data.ElementEntPK;
import eu.secse.deliveryManager.data.FacetEnt;
import eu.secse.deliveryManager.data.FacetXmlEnt;
import eu.secse.deliveryManager.data.ServiceEnt;
import eu.secse.deliveryManager.notify.messages.UpdateElement;
import eu.secse.deliveryManager.notify.messages.UpdateFacetSchema;
import eu.secse.deliveryManager.notify.messages.UpdateFacetXml;
import eu.secse.deliveryManager.notify.messages.UpdateService;
import eu.secse.deliveryManager.registry.IRegistryProxy;


@Stateless
@WebService(name = "IUpdate", serviceName = "Update", targetNamespace = "http://secse.eu/deliveryManager/update")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL, parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
public class Update implements IUpdate {

	private static final Log log = LogFactory.getLog(IUpdate.class);

	@EJB private IRegistryProxy registry;

	@EJB private ModelManager modelManager;
	
	private static final String QUEUE_REDS = "queue/update-notify";

	@PersistenceContext(unitName="deliveryManager")
	protected EntityManager em;

	@WebMethod public void addService(
			@WebParam(name="serviceId") String serviceId, 
			@WebParam(name="providerId") long providerId) {
		log.debug("Added service " + serviceId + " by provider: " + providerId);
		send(new UpdateService(serviceId,true));
		
	}


	@WebMethod public void addFacetSpecificationSchema(
			@WebParam(name="serviceId") String serviceId, 
			@WebParam(name="schemaId") String schemaId, 
			@WebParam(name="isAdditionalInformation") boolean isAdditionalInformation, 
			@WebParam(name="providerId") long providerId) {


		// Verifies if facet is locally created
		log.debug("Added facet specification schema " + schemaId + " for service id " + serviceId + "is Facet locally created "+ registry.isFacetLocallyCreated(schemaId) );
		if(registry.isFacetLocallyCreated(schemaId)){
			log.debug("The facet "+schemaId+" is locally created");

			ServiceEnt serviceEnt = modelManager.lookup(serviceId);	
			if(serviceEnt!=null){
				//Verifies if service is locally created
				if(registry.isServiceLocallyCreated(serviceId)){
					log.debug("The service "+serviceId+" is locally created");
					FacetEnt facet = modelManager.lookup(serviceId,schemaId);
					if(facet!=null) modelManager.postInsert(facet);
					else modelManager.add(serviceId,schemaId);
					//log.info("Added facet specification schema " + schemaId + " for service id " + serviceId +" by provider: " + providerId);

				} else {
					log.debug("The service "+serviceId+" isn't locally created");
					FacetEnt facet = modelManager.lookup(serviceId, schemaId);
					if(facet!=null) modelManager.postInsert(facet);
					else facet = modelManager.add(serviceId, schemaId);
					if(facet ==null) log.error("Error: Impossible execute an update for facet "+schemaId+" because the service isn't locally created and the facet is a facet spec");				
				}
			}
		}
		send(new UpdateFacetSchema(serviceId, true, schemaId));
		
	}


	@WebMethod public void addFacetSpecificationXML(
			@WebParam(name="serviceId") String serviceId, 
			@WebParam(name="schemaId") String schemaId, 
			@WebParam(name="xmlId") String xmlId, 
			@WebParam(name="isAdditionalInformation") boolean isAdditionalInformation, 
			@WebParam(name="providerId") long providerId) {
		log.debug("Added facet specification xml id  " + xmlId + ", schema " + schemaId + " for service id " + serviceId);
		if(registry.isFacetLocallyCreated(schemaId)){
			log.debug("The facet "+schemaId+" is locally created");
			ServiceEnt serviceEnt = modelManager.lookup(serviceId);
			if(serviceEnt!=null){
				//Verifies if service is locally created
				if(registry.isServiceLocallyCreated(serviceId)){
					log.debug("The service "+serviceId+" is locally created");
					FacetXmlEnt xml = modelManager.lookup(serviceId, schemaId, xmlId);
					if(xml!=null) modelManager.postInsert(xml);
					else modelManager.add(serviceId, schemaId, xmlId);

				} else {
					log.debug("The service "+serviceId+" isn't locally created");
					if(isAdditionalInformation){
						FacetXmlEnt xml = modelManager.lookup(serviceId, schemaId, xmlId);
						if(xml!=null) modelManager.postInsert(xml);
						else xml = modelManager.add(serviceId, schemaId, xmlId);
						if(xml==null)  log.error("Error: Impossible execute an update for facetXml "+xmlId+" because the service isn't locally created and the facet is a facet spec");
					}

				}
			}
		}
		send(new UpdateFacetXml(serviceId,true, schemaId, xmlId, isAdditionalInformation));
		
	}


	@WebMethod public void deleteFacetFacetSpecificationXML(
			@WebParam(name="serviceId") String serviceId, 
			@WebParam(name="schemaId") String schemaId, 
			@WebParam(name="xmlId") String xmlId, 
			@WebParam(name="wasAdditionalInformation") boolean wasAdditionalInformation, 
			@WebParam(name="providerId") long providerId) {

		log.debug("Deleted facet specification xml " + xmlId + ", schema " + schemaId + " for service id " + serviceId);
		send(new UpdateFacetXml(serviceId, false, schemaId, xmlId, wasAdditionalInformation));
		
		if(registry.isFacetLocallyCreated(schemaId)){
			log.debug("The facet "+schemaId+" is locally created");
			//Verifies if service is locally created
			if(registry.isServiceLocallyCreated(serviceId)){
				log.debug("The service "+serviceId+" is locally created");
				FacetXmlEnt xml = em.find(FacetXmlEnt.class, new ElementEntPK(xmlId,FacetXmlEnt.FACET_XML_ENT) );
				if(xml!=null){
					modelManager.deleteFromUpdate(xml);
					log.debug("Deleted facet specification xml " + xmlId + ", schema " + schemaId + " for service id " + serviceId);			
				}
			} else {
				log.debug("The service "+serviceId+" isn't locally created");
				FacetEnt facet = em.find(FacetEnt.class, new ElementEntPK(schemaId, FacetEnt.FACET_ENT));
				FacetXmlEnt xml = em.find(FacetXmlEnt.class, new ElementEntPK(xmlId,FacetXmlEnt.FACET_XML_ENT) );
				if(xml!=null){
					if(facet!=null){
						if(facet.isAddInfo()){
							modelManager.deleteFromUpdate(xml);
							log.debug("Deleted facet specification xml " + xmlId + ", schema " + schemaId + " for service id " + serviceId);
						} else log.error("Error: Impossible execute a delte for facetXml "+xmlId+" because the service isn't locally created and the facet is a facet spec");
					}

				}
			}
		}

		

	}

	@WebMethod public void deleteFacetSpecificationSchema(
			@WebParam(name="serviceId") String serviceId, 
			@WebParam(name="schemaId") String schemaId, 
			@WebParam(name="wasAdditionalInformation") boolean wasAdditionalInformation, 
			@WebParam(name="providerId") long providerId) {
		log.debug("Deleted facet specification schema " + schemaId + " for service id " + serviceId);
		
		// Verifies if facet is locally created
		send(new UpdateFacetSchema(serviceId,false, schemaId));
		
			if(registry.isFacetLocallyCreated(schemaId)){
				log.debug("The facet "+schemaId+" is locally created");
				//Verifies if service is locally created
				if(registry.isServiceLocallyCreated(serviceId)){
					log.debug("The service "+serviceId+" is locally created");
					FacetEnt facet = em.find(FacetEnt.class, new ElementEntPK(schemaId,FacetEnt.FACET_ENT) );
					if(facet!=null){
						modelManager.deleteFromUpdate(facet);
						log.debug("Deleted facet specification schema " + schemaId + " for service id " + serviceId);
					}
				} else {
					log.debug("The service "+serviceId+" isn't locally created");
					FacetEnt facet = em.find(FacetEnt.class, new ElementEntPK(schemaId,FacetEnt.FACET_ENT) );
					if(facet!=null){
						if(facet.isAddInfo()){
							modelManager.deleteFromUpdate(facet);
							log.debug("Deleted facet specification schema " + schemaId + " for service id " + serviceId);
						} else log.error("Error: Impossible execute a delete for facet "+schemaId+" because the service isn't locally created and the facet is a facet spec");				
					}
				}
			}
		
			

	}

	@WebMethod public void deleteService(
			@WebParam(name="serviceId") String serviceId, 
			@WebParam(name="providerId") long providerId) {
		log.debug("Deleted service " + serviceId);
	
		send(new UpdateService(serviceId, false));
		
		
		if(registry.isServiceLocallyCreated(serviceId)){
			ServiceEnt srv = em.find(ServiceEnt.class, new ElementEntPK(serviceId, ServiceEnt.SERVICE_ENT));
			if(srv != null) {
				modelManager.deleteFromUpdate(srv);
				log.debug("Deleted service " + serviceId + " by provider "+providerId);
			}

		}
		
		
	}

	private void send(UpdateElement updateElement){
		log.debug("Sending messase to "+QUEUE_REDS+" for service "+updateElement.getServiceId());
		
		QueueSession sess;
		Queue queue;
		QueueSender sender;

		try {
			InitialContext ctx = new InitialContext();
			queue = (Queue) ctx.lookup(QUEUE_REDS);
			QueueConnectionFactory factory = (QueueConnectionFactory) ctx.lookup("ConnectionFactory");
			QueueConnection cnn = factory.createQueueConnection();
			sess = cnn.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			sender = sess.createSender(queue);
			try {
				ObjectMessage jmsMsg = sess.createObjectMessage(updateElement);
				sender.send(jmsMsg);
				// sess.commit();
			} catch (JMSException e) {
				log.warn("Cannot send the received message through the JMS queue \"" + QUEUE_REDS + "\". Error: " + e.getMessage());
			}
		} catch (JMSException e) {
			log.error("Cannot connect to the JMS queue \"" + QUEUE_REDS + "\". Stopping reds thread. Error: " + e.getMessage());
			return;
		} catch (Exception e) {
			log.error("Exeption raised while initlializing the JMS queue. Stopping reds thread. Error: " + e.getMessage());
			return;
		}
	}

}