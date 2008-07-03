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
		@NamedQuery(name=CreatedElementExtraInfo.SHARED_ELEMENT_TO_RENEW, query="SELECT c.element FROM CreatedElementExtraInfo AS c WHERE c.renew < current_timestamp() "),
		@NamedQuery(name=CreatedElementExtraInfo.ALL, query="SELECT c FROM CreatedElementExtraInfo AS c")
})
public class CreatedElementExtraInfo extends ElementExtraInfo {
	
	public static final String SHARED_ELEMENT_TO_RENEW = "SharedElementToRenew";
	public static final String ALL = "GetAllSharedIds";
	
	public static final String INFO_TYPE = "DeliveryManager-sharing-CreatedElement";
	
	/** renew date of this element */
	@Temporal(TemporalType.TIMESTAMP)
	private Date renew;

	public Date getRenew() {
		return renew;
	}

	public void setRenew(Date renew) {
		this.renew = renew;
	}

	public CreatedElementExtraInfo(ElementEnt element, String infoType,
			boolean keepsAlive, boolean sharing, String notifier) {
		super(element, infoType, keepsAlive, sharing, "deliverymanager/ShareManager/local");
		
	}
	
	public CreatedElementExtraInfo(){
		super();
	}
	
	@SuppressWarnings("unchecked")
	public static Collection<ElementEnt> getToRenew(EntityManager em){
		return em.createNamedQuery(SHARED_ELEMENT_TO_RENEW).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public static Collection<CreatedElementExtraInfo> getALL(EntityManager em){
		return em.createNamedQuery(ALL).getResultList();
	}

}
