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

import eu.secse.deliveryManager.model.FacetAddInfo;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.Deliverable;

/**
 * This class allows to retrive a particular facet additional information, whose id is known.
 * It is possible to specify the related serviceId, in order to speed up the retrival of the interested
 * informations.
 */
public class InterestAdditionalInformationId implements Interest {

	private static final long serialVersionUID = -5981338994022205382L;

	private String serviceID;
	private String facetID;

	public InterestAdditionalInformationId(String serviceID, String facetID) {
		this.serviceID = serviceID;
		this.facetID = facetID;
	}
	
	public String getFacetID() {
		return facetID;
	}
	
	public String getServiceID() {
		return this.serviceID;
	}

	public boolean matches(Deliverable elem) {			
		if (elem instanceof DService && serviceID != null)
			return this.serviceID.equals(((DService) elem).getServiceID());
		
		if (elem instanceof FacetAddInfo) {
			FacetAddInfo facet = (FacetAddInfo) elem;
			return (serviceID == null || serviceID.equals(facet.getServiceID())) &&
				facetID.equals(facet.getSchemaID());
		}
		
		return false;
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
		final InterestAdditionalInformationId other = (InterestAdditionalInformationId) obj;
		if (this.serviceID == null) {
			if (other.serviceID != null)
				return false;
		} else if (!this.serviceID.equals(other.serviceID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": sid=" + this.serviceID + " fid=" + this.facetID;
	}
}
