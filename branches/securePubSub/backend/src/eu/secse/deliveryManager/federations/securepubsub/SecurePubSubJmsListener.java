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
package eu.secse.deliveryManager.federations.securepubsub;

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

import eu.secse.deliveryManager.federations.securepubsub.core.ISecPubSubProxy;
import eu.secse.deliveryManager.model.DFederation;
import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.model.DirectMessage;
import eu.secse.deliveryManager.model.MetaData;
import eu.secse.deliveryManager.reds.EnvelopeWithMetadata;
import java.util.Collection;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/secpubsubfed")
})
public class SecurePubSubJmsListener implements MessageListener {

    private static final Log log = LogFactory.getLog(SecurePubSubJmsListener.class);
    @EJB
    private ISecPubSubProxy proxy;

    public void onMessage(Message msg) {

        // receive an objectMessage
        if (msg instanceof ObjectMessage) {
            try {
                Serializable obj = ((ObjectMessage) msg).getObject();

                /*
                 * SecurePubSubProxy requires that at least a message is signed
                 * so it has to come in a EnvelopeWithMetadata
                 */
                if (obj instanceof EnvelopeWithMetadata) {
                    Deliverable deliverable = ((EnvelopeWithMetadata) obj).getObject();
                    Collection<MetaData> metadata = ((EnvelopeWithMetadata) obj).getMetaData();

                    if (deliverable instanceof DFederation) {
                        //The received message is a Federation message
                        //Passing it and the metadata to the SecurePubSubProxy

                        DFederation dfed = (DFederation) deliverable;
                        log.info("Received a secure federation message");

                        proxy.received(dfed.getFederationId(), dfed, metadata);
                    } else {
                        if (deliverable instanceof DirectMessage) {
                            proxy.received((DirectMessage)deliverable,metadata);
                            log.info("Received a direct message");
                        } else {
                            log.warn("Warning: cannot process type " + deliverable.getClass().getCanonicalName());
                        }
                    }
                } else {
                    log.warn("Received unknown message: " + obj.getClass().getCanonicalName());
                }
            } catch (JMSException e) {
                log.warn("Cannot receive the message through the JMS queue. Error: " + e.getMessage());
            }
        } else {
            log.warn("Received a non-object message: " + msg.getClass().getSimpleName());
        }
    }
}


