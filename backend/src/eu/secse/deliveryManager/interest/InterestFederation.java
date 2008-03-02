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

import eu.secse.deliveryManager.model.DFederation;
import eu.secse.deliveryManager.model.Deliverable;

public class InterestFederation implements Interest {

	private static final long serialVersionUID = -2110769789843050273L;

	private String federationID;

	public InterestFederation(String federationID) {
		this.federationID = federationID;
	}

	public boolean isCoveredBy(Interest filter) {
		return filter instanceof InterestFederation &&
			this.federationID.equals(((InterestFederation)filter).federationID);
	}

	public boolean matches(Deliverable elem) {
		
		if(!(elem instanceof DFederation)){
			return false;
		}
			
		DFederation dfed = (DFederation)elem;
		boolean result = this.federationID.equals(dfed.getFederationId());
		
		return result;
	}

	public String getFederationName() {
		return this.federationID;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": fedID=" + this.federationID;
	}
}
