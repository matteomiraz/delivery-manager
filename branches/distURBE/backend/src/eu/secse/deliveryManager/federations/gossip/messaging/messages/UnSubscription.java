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


public class UnSubscription extends AbstractMessage {

	private String unsubscribingId;
	private boolean mustswap;	
	private String alternative;
	
	private static final long serialVersionUID = -8645654204649130800L;

	public UnSubscription(String src, String dest, String federation) {
		super(src, dest, federation);
		mustswap=false;
		alternative=null;
	}

	public String getAlternative() {
		return alternative;
	}

	public void setAlternative(String alternative) {
		this.mustswap=true;
		this.alternative = alternative;
	}

	public boolean isMustswap() {
		return mustswap;
	}

	public void setMustswap(boolean mustswap) {
		this.mustswap = mustswap;
	}

	public String getUnsubscribingId() {
		return unsubscribingId;
	}

	public void setUnsubscribingId(String unsubscribingId) {
		this.unsubscribingId = unsubscribingId;
	}

	public Message createCopy(String destination) {
		UnSubscription unsub=new UnSubscription(getSender(),destination,getFederationId());
		unsub.setMustswap(isMustswap());
		if (isMustswap()) {
			unsub.setAlternative(getAlternative());
		}
		unsub.setUnsubscribingId(getUnsubscribingId());
		return unsub;
	}

}
