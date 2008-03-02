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
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.secse.deliveryManager.notify.data.Event;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/notify")
	})
public class NotifyJmsListener implements MessageListener{
	
	@PersistenceContext(unitName="deliveryManager")
	protected EntityManager em;
	
	private static final Log log = LogFactory.getLog(NotifyJmsListener.class);
	
	public void onMessage(Message message) {
		if(message instanceof ObjectMessage){
			try{
				
				Serializable obj = ((ObjectMessage)message).getObject(); 
				if(obj instanceof Event){
					Event e = (Event)obj;
					log.info("Received event to:"+ e.getEmail()+" subject: "+e.getSubject()+" body: "+e.getMessage());
					em.persist((Event)obj);
				}else
					log.warn("Received unknown message: " + obj.getClass().getCanonicalName());
			} catch (JMSException e) {
				log.warn("Cannot receive the message through the JMS queue. Error: " + e.getMessage());
			} 
		}else 
			log.warn("Received a non-object message: " + message.getClass().getSimpleName());
		
	}
}
