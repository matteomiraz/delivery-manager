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

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Query;

import eu.secse.deliveryManager.exceptions.NotFoundException;

@Entity
@NamedQueries({
	@NamedQuery(name=FederatedFacet.FEDERATED_FACET_SEARCH, query="FROM FederatedFacet WHERE elementId = :elementId"),
	@NamedQuery(name=FederatedFacet.FEDERATED_FACET_SEARCH_2, query="FROM FederatedFacet WHERE elementId = :elementId AND federation = :federation")
})
public class FederatedFacet extends FederatedElement {

	static final String FEDERATED_FACET_SEARCH = "FederatedFacetSearch";
	static final String FEDERATED_FACET_SEARCH_2 = "FederatedFacetSearch2";
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	private FederatedService service;

	public FederatedFacet(Federation federation, FederatedService service, String schemaId, boolean original) {
		super(federation, schemaId, original);
		this.service = service;
	}
	
	public FederatedFacet() { }

	public FederatedService getService() {
		return service;
	}

	/** Get the list of the elements by element id. */
	@SuppressWarnings("unchecked")
	public static Collection<FederatedFacet> getByFacet(EntityManager em, String schemaId) throws NotFoundException {
		try {
			Query q = em.createNamedQuery(FEDERATED_FACET_SEARCH);
			q.setParameter("elementId", schemaId);
			return q.getResultList();
		} catch (Throwable e) {
			throw new NotFoundException(e.getMessage());
		}
	}

	/** Get the list of the elements by element id. */
	@SuppressWarnings("unchecked")
	public static FederatedFacet getByFacetFederation(EntityManager em, String schemaId, Federation federation) throws NotFoundException {
		try {
			Query q = em.createNamedQuery(FEDERATED_FACET_SEARCH_2);
			q.setParameter("federation", federation);
			q.setParameter("elementId", schemaId);
			return (FederatedFacet) q.getSingleResult();
		} catch (Throwable e) {
			throw new NotFoundException(e.getMessage());
		}
	}
}