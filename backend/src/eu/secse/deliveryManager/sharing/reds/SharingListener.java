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

package eu.secse.deliveryManager.sharing.reds;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import polimi.reds.Message;
import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.model.FacetAddInfo;
import eu.secse.deliveryManager.model.FacetSpec;
import eu.secse.deliveryManager.reds.Envelope;
import eu.secse.deliveryManager.sharing.core.IShareManager;
import eu.secse.reds.core.ARedsListener;


public class SharingListener extends ARedsListener {
	
	private static final Log log = LogFactory.getLog(SharingListener.class);
	
	//private IPubSubProxyMBean coordinator;
	
	//private static final String QUEUE_REDS = "queue/sharing";
	
	
	
	public SharingListener() {
//		try {
//	        MBeanServer server = MBeanServerLocator.locate();
//	        this.coordinator= (IPubSubProxyMBean) MBeanProxyExt.create(IPubSubProxyMBean.class, "DeliveryManager:service=pubSubFederationProxy", server);
//	    } catch (Exception e) {
//	        log.error(e.getMessage());	       
//	    }
	}
	
	
	@Override
	public void onMessage(Message msg) {
		/*QueueSession sess;
		Queue queue;
	    QueueSender sender;

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
		
		// if received a message
		if(msg != null) {
			// if it is a envelop of a model (envelope or EnvelopeFederation)
			if(msg instanceof Envelope) {
				log.info("received: " + msg);

					try {
						ObjectMessage jmsMsg = sess.createObjectMessage(msg);
					    sender.send(jmsMsg);
					    // sess.commit();
					} catch (JMSException e) {
						log.warn("Cannot send the received message through the JMS queue \"" + QUEUE_REDS + "\". Error: " + e.getMessage());
					}
				
			} else {
				log.warn("Unknown message container: " + msg.getClass().getCanonicalName());
			}
		}
		try {
			sess.close();
		} catch (JMSException e) {
			log.warn("Exeption raised while closing the JMS queue. Error: " + e.getMessage());
		} */
		
		// receive an objectMessage
		
		
		// if received a message
		if(msg != null) {
				if (msg instanceof Envelope) {
					log.info("received: " + msg);
					Deliverable deliverable = ((Envelope) msg).getObject();
					if (deliverable instanceof DService){
						try{
							
							
							InitialContext ctx = new InitialContext();
							
							IShareManager shareManager =(IShareManager)ctx.lookup("deliverymanager/ShareManager/local");			
							
							shareManager.received((DService) deliverable);
						
						} catch(NotFoundException ex){
							log.error(ex.getMessage());
						} catch(NamingException e ){
							log.equals(e.getMessage());
						}
					}
						
					else { if (deliverable instanceof FacetSpec) 
						{
					try{
						InitialContext ctx = new InitialContext();
						
						IShareManager shareManager =(IShareManager)ctx.lookup("deliverymanager/ShareManager/local");			
						
						shareManager.received((FacetAddInfo) deliverable);
					} catch(NamingException e ){
						log.equals(e.getMessage());
					}
						}
					else 
						log.warn("Warning: cannot process type " + deliverable.getClass().getCanonicalName());
					}} else
					log.warn("Received unknown message: " + msg.getClass().getCanonicalName());
			
		} }
		
	
	
		
	

}
