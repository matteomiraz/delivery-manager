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

package eu.secse.deliveryManager.interest;

import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.Deliverable;

/**
 * This class allows declaring an interest on a particular service,
 * identified by its id. After the subscription to this class, all service
 * specification facets are forwarded to the local registry.
 * @see long InterestManager.subscribeService(String serviceId)
 */
public class InterestService implements Interest {

	private static final long serialVersionUID = -1506528069141046335L;

	private String serviceID;

	public InterestService(String serviceID) {
		this.serviceID = serviceID;
	}
	
	public String getServiceID() {
		return this.serviceID;
	}

	public boolean matches(Deliverable elem) {			

		return elem instanceof DService &&
			this.serviceID.equals(((DService) elem).getServiceID());
	}
	
	public boolean isCoveredBy(Interest filter) {
		return this.equals(filter);
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((this.serviceID == null) ? 0 : this.serviceID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		final InterestService other = (InterestService) obj;
		if (this.serviceID == null) {
			if (other.serviceID != null)
				return false;
		} else if (!this.serviceID.equals(other.serviceID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": sid=" + this.serviceID;
	}
}
