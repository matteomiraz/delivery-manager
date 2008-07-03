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

package eu.secse.deliveryManager.timeout;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import eu.secse.deliveryManager.data.ElementEnt;
import eu.secse.deliveryManager.data.ElementExtraInfo;

@Entity
public class LeaseExtraInfo extends ElementExtraInfo {
	
	public static final String INFO_TYPE = "DeliveryManager-sharing-LeaseExtraInfo";
	
	/** lease of this element */
	@Temporal(TemporalType.TIMESTAMP)
	private Date lease;

	public Date getLease() {
		return lease;
	}
	
	public void setLease(Date lease) {
		this.lease = lease;
	}

	public LeaseExtraInfo(ElementEnt element, Date lease) {
		super(element, INFO_TYPE, false, false, "");
		this.lease = lease;
	}
	
	LeaseExtraInfo(){ }
}
