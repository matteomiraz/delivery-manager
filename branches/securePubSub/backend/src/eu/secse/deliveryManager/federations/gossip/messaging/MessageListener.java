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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

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

public class MessageListener extends Thread {
	private static final Log log = LogFactory.getLog(MessageListener.class);

	private boolean isrunning;
	private int port;	
	private static final String QUEUE_GOSSIP="queue/gossip-messaging";
	
	private QueueSender sender;
	private QueueSession sess;
	
	class MessageConnectionReceiver extends Thread {
		private Socket conn;
//		private IMessageReceiver receiver;
		
		public MessageConnectionReceiver(Socket conn) {
			this.conn=conn;
		}
		
		public void run() {
			
				ObjectInputStream ois=null;
				try {
					ois = new ObjectInputStream(conn.getInputStream());
				} catch (IOException e) {
					log.error("Cannot read stream from socket");					
					return;
				}
				
//				try {
//					InitialContext ctx = new InitialContext();
//					receiver = (IMessageReceiver) ctx.lookup("deliverymanager/MessageReceiver/local");
//				} catch (NamingException ne) {
//					log.error("Cannot lookup message receiver: exiting");
//					return;
//				}
				
				Queue queue;
			    try {
					InitialContext ctx = new InitialContext();
					queue = (Queue) ctx.lookup(QUEUE_GOSSIP);
					QueueConnectionFactory factory = (QueueConnectionFactory) ctx.lookup("ConnectionFactory");
					QueueConnection cnn = factory.createQueueConnection();
					sess = cnn.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
					sender = sess.createSender(queue);
				} catch (JMSException e) {
					log.error("Cannot connect to the JMS queue \"" + QUEUE_GOSSIP+ "\".  Error: " + e.getMessage());
					return;
				} catch (Exception e) {
					log.error("Exeption raised while initlializing the JMS queue. Error: " + e.getMessage());
					return;
				}
				
				Object o=null;
				try {
					o = ois.readObject();
				} catch (IOException e) {
					log.error("Could not read message from stream");
					return;
				} catch (ClassNotFoundException e) {
					log.error("Could not find class: " + e.getMessage());
					return;
				}
				if (o instanceof Message) {
					Message m = (Message) o;
					log.info("Received message from "+ m.getSender());
					//receiver.receive(m);
					try {
						ObjectMessage omess = sess
								.createObjectMessage(m);

						sender.send(omess);
					} catch (JMSException e) {
						log.warn("Could not send message " + m);
					}
				} else {
					log.error("Unknown message object class: "+ o.getClass().getName());
				}
			
		}
	}
	
	public MessageListener(int port) {
		isrunning=true;
		this.port=port;
	}
	
	public void stopListening() {
		isrunning=false;		
	}
	
	public boolean isRunning() {
		return isrunning;		
	}
	
	public void run() {
		ServerSocket servsock=null;
		try {
			servsock=new ServerSocket(port);
			servsock.setSoTimeout(2000);
		} catch (IOException e) {
			log.error("Cannot listen on port: " + port + " closing message reception thread");
			return;
		}	
		while (isrunning) {				
				try {
					Socket c=servsock.accept();
					MessageConnectionReceiver rec=new MessageConnectionReceiver(c);
					rec.start();																	
				} catch (IOException e) {						
					//Do nothing
				}			 
		}
		try {
			servsock.close();
		} catch (IOException e) {
			log.warn("Error while closing the socket");
		}
	}
}