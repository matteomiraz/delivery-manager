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

import java.io.Serializable;

import javax.annotation.EJB;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.secse.deliveryManager.model.DFacetSpecificationSchema;
import eu.secse.deliveryManager.model.DFederation;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.reds.Envelope;

/**
 * Receive the update and the modification from the <b>other</b> registries.
 * 
 * @author matteo
 */
@MessageDriven(activationConfig = {
	@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
	@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/reds")
})
public class JMSListener implements MessageListener {
	
	private static final Log log = LogFactory.getLog(JMSListener.class);

	@EJB private IModelManager iMod;
	@EJB private IFederationModelManager iFed;
	
	public void onMessage(Message msg) {

		// receive an objectMessage
		if(msg instanceof ObjectMessage) {
			try {
				Serializable obj = ((ObjectMessage)msg).getObject(); 
				log.info("Received " + obj.getClass().getSimpleName());
				
				if (obj instanceof Envelope) {
					Deliverable deliverable = ((Envelope) obj).getObject();
					
					if(deliverable instanceof DFederation) {
						DFederation dfed = (DFederation) deliverable;

						if(dfed.getObject() instanceof DService)
							iFed.received(dfed.getFederationName(), (DService) dfed.getObject());
						else if(dfed.getObject() instanceof DFacetSpecificationSchema)
							iFed.received(dfed.getFederationName(), (DFacetSpecificationSchema) dfed.getObject());
						else 
							log.warn("Warning: cannot process type " + dfed.getObject().getClass().getCanonicalName() + " received from the federation " + dfed.getFederationName());

					} else if (deliverable instanceof DService)
						iMod.storeService((DService) deliverable);
					else if (deliverable instanceof DFacetSpecificationSchema) 
						iMod.storeFacet((DFacetSpecificationSchema) deliverable);
					else 
						log.warn("Warning: cannot process type " + deliverable.getClass().getCanonicalName());
				} else
					log.warn("Received unknown message: " + obj.getClass().getCanonicalName());
			} catch (JMSException e) {
				log.warn("Cannot receive the message through the JMS queue. Error: " + e.getMessage());
			}
		} else 
			log.warn("Received a non-object message: " + msg.getClass().getSimpleName());
	}
}
