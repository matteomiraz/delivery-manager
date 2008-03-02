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

package eu.secse.deliveryManager.federations.gossip.messaging.messages;

import eu.secse.deliveryManager.federations.gossip.messaging.Message;

public class ForwardedSubscription extends AbstractMessage {
	
	
	private static final long serialVersionUID = -6426007312646029061L;
	
	public ForwardedSubscription(String src, String dest, String federation) {
		super(src, dest, federation);		
	}
	
	private String subscribedId;
	private int hopcount;
	
	public int getHopcount() {
		return hopcount;
	}
	public void setHopcount(int hopcount) {
		this.hopcount = hopcount;
	}
	public String getSubscribedId() {
		return subscribedId;
	}
	public void setSubscribedId(String subscribedId) {
		this.subscribedId = subscribedId;
	}
	
	public Message createCopy(String destination) {
		ForwardedSubscription subscription=new ForwardedSubscription(getSender(),destination,getFederationId());
		subscription.setHopcount(getHopcount());
		subscription.setSubscribedId(getSubscribedId());
		return subscription;
	}
	
}
