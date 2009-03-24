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


public class ContactAnswer extends AbstractMessage {

	private static final long serialVersionUID = -2124442689706370570L;
	
	private Collection<PromotionHeader> headers;
		
	public ContactAnswer(String src, String dest, String federation) {
		super(src, dest, federation);	
	}

	public Collection<PromotionHeader> getHeaders() {
		return headers;
	}

	public void setHeaders(Collection<PromotionHeader> headers) {
		this.headers = headers;
	}

	public Message createCopy(String destination) {
		ContactAnswer ca=new ContactAnswer(getSender(),destination,getFederationId());
		ca.setHeaders(new Vector<PromotionHeader>(headers));
		return ca;
	}

	
	

}
