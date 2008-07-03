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

package eu.secse.federationDirectory.redsintegration;

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

import polimi.reds.Message;
import eu.secse.federationDirectory.reds.messages.EndpointInfo;
import eu.secse.federationDirectory.reds.messages.EndpointRequest;
import eu.secse.reds.core.ARedsReplyListener;

public class RedsListener extends ARedsReplyListener {	
	
	private static final Log log = LogFactory.getLog(RedsListener.class);
	private static final String QUEUE_REDS = "queue/reds-directory";
		
	private QueueSender sender;
	private QueueSession sess;
	private String endpoint;
	
	
	public RedsListener() {
		super();
	}
	
	public void setEndpoint(String endpoint) {
		this.endpoint=endpoint;
	}
	
	public void onMessage(Message msg) {
		log.info("received: " + msg);
		if (sender==null) {
			
			Queue queue;
		    try {
				InitialContext ctx = new InitialContext();
				queue = (Queue) ctx.lookup(QUEUE_REDS);
				QueueConnectionFactory factory = (QueueConnectionFactory) ctx.lookup("ConnectionFactory");
				QueueConnection cnn = factory.createQueueConnection();
				sess = cnn.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
				sender = sess.createSender(queue);
			} catch (JMSException e) {
				log.error("Cannot connect to the JMS queue \"" + QUEUE_REDS + "\". Stopping reds thread. Error: " + e.getMessage());
				return;
			} catch (Exception e) {
				log.error("Exeption raised while initlializing the JMS queue. Stopping reds thread. Error: " + e.getMessage());
				return;
			}
		}
		
		
		if(msg instanceof EndpointRequest) {			
			log.info("received: " + msg);					
			//Send answer			
			EndpointInfo einfo=new EndpointInfo(endpoint);
			ds.reply(einfo,msg.getID());
			log.info("Replied with endpoint information");
		}  
		else {
			log.info("received: " + msg);
			try {
				ObjectMessage jmsMsg = sess.createObjectMessage(msg);
				sender.send(jmsMsg);
				// sess.commit();
			} catch (JMSException e) {
				log.warn("Cannot send the received message through the JMS queue \"" + QUEUE_REDS + "\". Error: " + e.getMessage());
			}
		} 								
	}

}
