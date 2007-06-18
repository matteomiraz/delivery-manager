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


package eu.secse.deliveryManager.db.federations;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Query;

import eu.secse.deliveryManager.exceptions.NotFoundException;

@Entity
@NamedQueries({
	@NamedQuery(name=FederatedService.FEDERATED_SERVICE_SEARCH, query="FROM FederatedService WHERE elementId = :elementId"),
	@NamedQuery(name=FederatedService.FEDERATED_SERVICE_SEARCH_2, query="FROM FederatedService WHERE elementId = :elementId AND federation = :federation")
})
public class FederatedService extends FederatedElement {

	static final String FEDERATED_SERVICE_SEARCH = "FederatedServiceSearch";
	static final String FEDERATED_SERVICE_SEARCH_2 = "FederatedServiceSearch2";

	private boolean shareAllFacets;
	
	@OneToMany(mappedBy="service", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private Collection<FederatedFacet> promotedFacets;
	
	public FederatedService() { }
	
	public FederatedService(Federation federation, String serviceId, boolean shareAllFacets, boolean original) {
		super(federation, serviceId, original);
		this.shareAllFacets = shareAllFacets;
	}
	
	public boolean isShareAllFacets() {
		return shareAllFacets;
	}
	
	public Collection<FederatedFacet> getPromotedFacets() {
		return promotedFacets;
	}
	
	/** Get the list of the elements by element id. */
	@SuppressWarnings("unchecked")
	public static Collection<FederatedService> getByService(EntityManager em, String serviceId) throws NotFoundException {
		try {
			Query q = em.createNamedQuery(FEDERATED_SERVICE_SEARCH);
			q.setParameter("elementId", serviceId);
			return q.getResultList();
		} catch (Throwable e) {
			throw new NotFoundException(e.getMessage());
		}
	}

	/** Get the list of the elements by element id. */
	@SuppressWarnings("unchecked")
	public static FederatedService getByServiceFederation(EntityManager em, Federation fed, String serviceId) throws NotFoundException {
		try {
			Query q = em.createNamedQuery(FEDERATED_SERVICE_SEARCH_2);
			q.setParameter("federation", fed);
			q.setParameter("elementId", serviceId);
			return (FederatedService) q.getSingleResult();
		} catch (Throwable e) {
			throw new NotFoundException(e.getMessage());
		}
	}
}
