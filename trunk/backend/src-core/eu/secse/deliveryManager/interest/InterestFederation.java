/* This file is part of Delivery Manager.
 * (c) 2006 Matteo Miraz, Politecnico di Milano
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

	private static final long serialVersionUID = 8535479848226257443L;

	private String federationName;

	public InterestFederation(String federationName) {
		this.federationName = federationName;
	}

	public boolean isCoveredBy(Interest filter) {
		return filter instanceof InterestFederation &&
			this.federationName.equals(((InterestFederation)filter).federationName);
	}

	public boolean matches(Deliverable elem) {
		if(!(elem instanceof DFederation)) 
			return false;
		
		return this.federationName.equals(((DFederation)elem).getFederationName());
	}

	public String getFederationName() {
		return this.federationName;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": fed=" + this.federationName;
	}
}
