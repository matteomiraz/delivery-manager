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
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Stateless
public class MessageSender implements IMessageSender {
	//TODO (optional) implement persistent connections for improved efficiency
	private static final Log log= LogFactory.getLog(IMessageSender.class);
		
	public void send(Message m) {
		String id=m.getDestination();
		
		String[] parts=id.split(":");
		if (parts.length!=2) {
			log.error("Could not send message, invalid destination " +id );
		}
		String address=parts[0];
		String sport=parts[1];
		Socket s=null;
		int port=Integer.parseInt(sport);
		try {
			s=new Socket(address,port);
		} catch (UnknownHostException e) {
			log.error("Could not send message, unknown host " + address );
			return;
		} catch (IOException e) {
			log.error("Could not send message. Connection to "  + id + " failed");
			return;
		}	
		try {
			log.debug("Sending message: " + m.getClass().getName() + " to " + m.getDestination() + " in federation " +m.getFederationId() + " (source set as " + m.getSender() + ")");
			ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
			oos.writeObject(m);
			log.info("Sent message: " + m.getClass().getName() + " to " + m.getDestination() + " in federation " +m.getFederationId() + " (source set as) " + m.getSender());
			log.debug("Message content: " + m.toString());
		} catch (IOException e) {
			log.error("Error while sending message ");
		}
	}
}
