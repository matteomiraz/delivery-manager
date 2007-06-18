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


package eu.secse.deliveryManager.db.filters;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Query;

import eu.secse.deliveryManager.db.federations.Federation;

@Entity
@NamedQueries({
	@NamedQuery(name="FederationInterestByFedName", query="FROM FederationInterest AS fi WHERE fi.federation.federationName = :federazione")
})
public class FederationInterest extends Interest {

	@OneToOne
	private Federation federation;

	FederationInterest() { /* emtpy constructor */ }
	
	public FederationInterest(Federation federation) {
		this.federation = federation;
	}

	public Federation getFederation() {
		return federation;
	}
	
	public void setFederation(Federation federation) {
		this.federation = federation;
	}
	
	public static FederationInterest searchByName(EntityManager em, String federationName) {
		try {
			Query q = em.createNamedQuery("FederationInterestByFedName");
			q.setParameter("federazione", federationName);
			return (FederationInterest) q.getSingleResult();
		} catch (Throwable t) {
			return null;
		}
	}
}
