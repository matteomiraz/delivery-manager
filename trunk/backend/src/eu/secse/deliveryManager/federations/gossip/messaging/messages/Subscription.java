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

public class Subscription extends AbstractMessage {

	private static final long serialVersionUID = -7556451639976021087L;
	
	private String subscribedid;
	private boolean resubscription;
	private int hopcount;
	
	public Subscription(String src, String dest,
			String federation,String subscriber) {
		super(src, dest, federation);
		this.subscribedid=subscriber;			
		resubscription=false;
		hopcount=-1;
	}
	
	public void setHopCount(int i) {
		this.hopcount=i;		
	}
	
	public int getHopCount() {
		return hopcount;
	}

	
	public String getSubscribedAddress() {
		return subscribedid;
	}


	public boolean isResubscription() {
		return resubscription;
	}


	public void setResubscription(boolean resubscription) {
		this.resubscription = resubscription;
	}

	public Message createCopy(String destination) {
		Subscription s=new Subscription(getSender(),destination,getFederationId(),getSubscribedAddress());
		s.setResubscription(isResubscription());
		s.setHopCount(getHopCount());
		return s;
	}

}
