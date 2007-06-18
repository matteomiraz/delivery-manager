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

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Query;

@NamedQueries({
	@NamedQuery(name=ServiceInterest.SERVICE_INTEREST_BY_SID, query="FROM ServiceInterest WHERE serviceID = :serviceId")
})
@Entity
public class ServiceInterest extends Interest {

	// LAZY: i filtri automatici vengono creati se necessario, ma NON vengono MAI tolti!
	static final String SERVICE_INTEREST_BY_SID = "SERVICE_INTEREST_BY_SID";
	
	private String serviceID;
	
	ServiceInterest() { /* emtpy constructor */ }
	
	public ServiceInterest(String serviceID) {
		this.serviceID = serviceID;
	}

	public String getServiceID() {
		return serviceID;
	}

	@SuppressWarnings("unchecked")
	public static Collection<ServiceInterest> getServiceInterestByServiceID(EntityManager em, String serviceId) {
		Query q = em.createNamedQuery(SERVICE_INTEREST_BY_SID);
		q.setParameter("serviceId", serviceId);
		return q.getResultList();
	}
}
