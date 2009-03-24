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


package eu.secse.deliveryManager.notify.core;

import java.io.Serializable;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.secse.deliveryManager.notify.messages.UpdateElement;
import eu.secse.deliveryManager.notify.messages.UpdateFacetSchema;
import eu.secse.deliveryManager.notify.messages.UpdateFacetXml;
import eu.secse.deliveryManager.notify.messages.UpdateService;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue =  "queue/update-notify")
	})
public class JmsUpdateNotify implements MessageListener{
	
	//private static final String QUEUE_REDS = "queue/update-notify";
	
	@EJB private IRegistryEventProcessor registryEventProcessor;

	private static final Log log = LogFactory.getLog(JmsUpdateNotify.class);

	public void onMessage(Message message) {
		if(message instanceof ObjectMessage){
			try{
				Serializable obj = ((ObjectMessage)message).getObject(); 
				if(obj instanceof UpdateElement){
					if (obj instanceof UpdateService) {
						UpdateService updateService = (UpdateService) obj;
						log.info("Received update service message for service "+ updateService.getServiceId());
						if(updateService.isAdded()) registryEventProcessor.serviceAdded(updateService.getServiceId());
						else registryEventProcessor.serviceRemoved(updateService.getServiceId());
						
					} else{
						if (obj instanceof UpdateFacetSchema) {
							UpdateFacetSchema updateFacetSchema = (UpdateFacetSchema) obj;
							log.info("Received update facet schema message for service "+ updateFacetSchema.getServiceId());
							if(updateFacetSchema.isAdded()) registryEventProcessor.facetSchemaAdded(updateFacetSchema.getServiceId(),updateFacetSchema.getSchemaId());
							else registryEventProcessor.facetSchemaRemoved(updateFacetSchema.getServiceId(),updateFacetSchema.getSchemaId());
						}
						else{
							UpdateFacetXml updateFacetXml = (UpdateFacetXml) obj;
							log.info("Received update facet xml message for service "+ updateFacetXml.getServiceId());
							if(updateFacetXml.isAdded()) registryEventProcessor.facetXmlAdded(updateFacetXml.getServiceId(), updateFacetXml.getSchemaId(), updateFacetXml.getXmlId(), updateFacetXml.isAdditionalInformation());
							else registryEventProcessor.facetXmlRemoved(updateFacetXml.getServiceId(), updateFacetXml.getSchemaId(), updateFacetXml.getXmlId(), updateFacetXml.isAdditionalInformation());
						}
					}
					
				}else
					log.warn("Received unknown message: " + obj.getClass().getCanonicalName());
			} catch (JMSException e) {
				log.warn("Cannot receive the message through the JMS queue. Error: " + e.getMessage());
			} 
		}else 
			log.warn("Received a non-object message: " + message.getClass().getSimpleName());
		
		
	}
	

}
