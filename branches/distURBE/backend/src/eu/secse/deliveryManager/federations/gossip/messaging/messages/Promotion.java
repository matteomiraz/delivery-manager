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

import java.util.Collection;
import java.util.Vector;

import eu.secse.deliveryManager.federations.gossip.messaging.Message;


public class Promotion extends AbstractMessage {
	
	
	private static final long serialVersionUID = 5179132864515045597L;

	public Promotion(String src, String dest, String federation) {
		super(src, dest, federation);		
		payload=new Vector<PromotionHeader>();
	}

	private Collection<PromotionHeader> payload;

	public Collection<PromotionHeader>getPayload() {
		return payload;
	}

	public void setPayload(Collection<PromotionHeader> payload) {
		this.payload = payload;
	}

	public Message createCopy(String destination) {
		Promotion p=new Promotion(getSender(),destination,federationId);
		p.setPayload(new Vector<PromotionHeader>(payload));
		return p;
	}	
	
	@Override
	public String toString() {
		String newline=System.getProperty("line.separator");
		 String promotionheader="Promotion :" +  newline;
		 for (PromotionHeader ph:payload) {
			 promotionheader = promotionheader + ph.toString() + newline;
		 }
		 return promotionheader;
	}
	
}
