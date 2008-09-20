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

package eu.secse.deliveryManager.federations.securepubsub.data;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import eu.secse.deliveryManager.data.FederatedPromotion;
import eu.secse.deliveryManager.data.FederatedPromotionExtraInfo;

@Entity
@NamedQueries(value={
		@NamedQuery(name=SecPubSubPromotionExtraInfo.FEDERATED_ELEMENT_TO_RENEW, 
				query="SELECT f.prom FROM SecPubSubPromotionExtraInfo AS f WHERE f.timeout < current_timestamp() ")
})
public class SecPubSubPromotionExtraInfo extends FederatedPromotionExtraInfo {
	
	public static final String FEDERATED_ELEMENT_TO_RENEW = "secureFederatedElementToExpire";
	
	public SecPubSubPromotionExtraInfo(){
		super();
	}
	/** renew or lease of this element */
	@Temporal(TemporalType.TIMESTAMP)
	private Date timeout;

	public Date getTimeout() {
		return timeout;
	}

	public void setTimeout(Date timeout) {
		this.timeout = timeout;
	}
	@SuppressWarnings("unchecked")
	public static Collection<FederatedPromotion> getToRenew(EntityManager em){
		return em.createNamedQuery(FEDERATED_ELEMENT_TO_RENEW).getResultList();
	}
}
