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

package eu.secse.deliveryManager.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class FederatedPromotionExtraInfo {
	
	@Id
	@GeneratedValue
	private long id;
	
	@OneToOne(optional=false, mappedBy="extraInfo")
	private FederatedPromotion prom;
	
	public FederatedPromotionExtraInfo() { /* empty constructor */ }
	
	public FederatedPromotionExtraInfo(FederatedPromotion prom) {
		this.prom=prom;
		prom.setExtraInfo(this);
	}
	
	public long getId() {
		return id;
	}

	public FederatedPromotion getProm() {
		return prom;
	}

	public void setProm(FederatedPromotion prom) {
		this.prom = prom;
	}
	

}
