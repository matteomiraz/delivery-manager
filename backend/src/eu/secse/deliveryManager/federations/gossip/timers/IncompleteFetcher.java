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

package eu.secse.deliveryManager.federations.gossip.timers;

import eu.secse.deliveryManager.federations.gossip.data.DeliveryManagerGossipInfo;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.PromotionHeader;

public interface IncompleteFetcher {
	
	/** Ask the fetcher to fetch the details of the promotion
	 * 
	 * @param header
	 */
	public void queueElement(PromotionHeader header, DeliveryManagerGossipInfo from);
	
	/** Remove the promotion from the queue, because details have been received
	 * 
	 * @param header
	 */
	public void removeFromQueue(PromotionHeader header);
}
