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

import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.secse.deliveryManager.notify.data.Event;

@Stateless
public class JmsSender implements ISender{

	private static final String QUEUE_REDS = "queue/notify";

	private static final Log log = LogFactory.getLog(JmsSender.class);
	
	public void send(Event event){
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
				ObjectMessage jmsMsg = sess.createObjectMessage(event);
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
