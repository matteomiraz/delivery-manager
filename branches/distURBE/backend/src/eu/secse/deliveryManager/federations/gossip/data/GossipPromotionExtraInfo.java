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

package eu.secse.deliveryManager.federations.gossip.data;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import eu.secse.deliveryManager.data.FederatedPromotion;
import eu.secse.deliveryManager.data.FederatedPromotionExtraInfo;

@Entity
@NamedQuery(name=GossipPromotionExtraInfo.get_pending_promotions,query="from GossipPromotionExtraInfo where pending = true")
public class GossipPromotionExtraInfo extends
		FederatedPromotionExtraInfo {
	
	public static final String get_pending_promotions="get_pending_gossip_promotions";
	
	private boolean pending;
	
	@Temporal(value=TemporalType.TIMESTAMP)
	private Date promotionTimestamp;
	
	 GossipPromotionExtraInfo() {
		super();		
	}

	public GossipPromotionExtraInfo(FederatedPromotion prom, boolean pending, long timestamp) {
		super(prom);		
		this.pending = pending;
		this.promotionTimestamp=new Date(timestamp);
	}

	/** A pending promotion is a promotion that could not be completed because the partial view was empty.
	 *  When the partial view is filled again, the promotion can be resumed (resending the DService) 
	 *  
	 * @return
	 */
	public boolean isPending() {
		return pending;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
	}

	public Date getPromotionTimestamp() {
		return promotionTimestamp;
	}

	public void setPromotionTimestamp(Date promotionTimestamp) {
		this.promotionTimestamp = promotionTimestamp;
	}
	
}
