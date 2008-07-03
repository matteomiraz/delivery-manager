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

import eu.secse.deliveryManager.data.FederatedElement;
import eu.secse.deliveryManager.data.FederatedElementExtraInfo;

@Entity
@NamedQueries(value={
		@NamedQuery(name=SecPubSubFedElemExtraInfo.FEDERATED_ELEMENT_EXPIRED, query="SELECT f.elem FROM SecPubSubFedElemExtraInfo AS f WHERE f.timeout < current_timestamp() ")
})
public class SecPubSubFedElemExtraInfo extends FederatedElementExtraInfo{
	public static final String FEDERATED_ELEMENT_EXPIRED = "FederatedElementExpired";
	
	/** renew or lease of this element */
	@Temporal(TemporalType.TIMESTAMP)
	private Date timeout;

	public Date getTimeout() {
		return timeout;
	}

	public void setTimeout(Date timeout) {
		this.timeout = timeout;
	}

	public SecPubSubFedElemExtraInfo() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	public static Collection<FederatedElement> getExpired(EntityManager em){
		return em.createNamedQuery(SecPubSubFedElemExtraInfo.FEDERATED_ELEMENT_EXPIRED).getResultList();
	}

}
