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

package eu.secse.deliveryManager.sharing.data;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import eu.secse.deliveryManager.data.ElementEnt;
import eu.secse.deliveryManager.data.ElementExtraInfo;

@Entity
@NamedQueries(value={
		@NamedQuery(name=ReceivedElementExtraInfo.SHARED_ELEMENT_TO_EXPIRE, query="SELECT r.element FROM ReceivedElementExtraInfo AS r WHERE r.expire < current_timestamp() ")
})
public class ReceivedElementExtraInfo extends ElementExtraInfo{
	
public static final String SHARED_ELEMENT_TO_EXPIRE = "SharedElementToExpire";
	
public static final String INFO_TYPE = "DeliveryManager-sharing-ReceivedElement";
	/** renew date of this element */
	@Temporal(TemporalType.TIMESTAMP)
	private Date expire;

	public Date getExpire() {
		return expire;
	}

	public void setExpire(Date expire) {
		this.expire = expire;
	}

	public ReceivedElementExtraInfo(ElementEnt element, String infoType,
			boolean keepsAlive, boolean sharing, String notifier) {
		super(element, infoType, keepsAlive, sharing, "deliverymanager/ShareManager/local");
		
	}
	
	public ReceivedElementExtraInfo(){
		super();
	}
	@SuppressWarnings("unchecked")
	public static Collection<ElementEnt> getToExpire(EntityManager em){
		return em.createNamedQuery(SHARED_ELEMENT_TO_EXPIRE).getResultList();
	}
	
}
