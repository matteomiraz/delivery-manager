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

import java.io.Serializable;

public abstract class PromotionHeader implements Serializable{
		 String serviceid;		
		 private long timestamp;
		 
		public String getServiceid() {
			return serviceid;
		}

		public PromotionHeader(String serviceid,long timestamp) {
			super();		
			this.serviceid = serviceid;
			this.timestamp=timestamp;
		}

		/** Timestamp of the promotion or of the deletion (depends on the context)
		 * 
		 * @return
		 */
		public long getPromotionTimestamp() {
			return timestamp;
		}

		public void setPromotionTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}
		 
}
