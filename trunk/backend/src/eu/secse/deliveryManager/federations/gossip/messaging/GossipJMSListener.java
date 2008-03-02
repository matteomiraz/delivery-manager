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

package eu.secse.deliveryManager.federations.gossip.messaging;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/gossip-messaging")
		})
public class GossipJMSListener implements MessageListener {

	@EJB
	private IMessageReceiver receiver;
	
	private static final Log log = LogFactory.getLog(GossipJMSListener.class);
	
	public void onMessage(Message arg0) {
		if (arg0 instanceof ObjectMessage) {
			try {
			eu.secse.deliveryManager.federations.gossip.messaging.Message m=(eu.secse.deliveryManager.federations.gossip.messaging.Message)(((ObjectMessage)arg0).getObject());
			receiver.receive(m);
			}
			catch (JMSException jexc) {
				log.warn("Cannot receive the message through the JMS queue. Error: " + jexc.getMessage());
			} 
		} else {
			log.warn("Received unknown message: " + arg0.getClass().getCanonicalName());
		}
		

	}

}
