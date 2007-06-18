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


package eu.secse.deliveryManager.db.data;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import eu.secse.deliveryManager.exceptions.NotFoundException;

/**
 * Facet entity.
 * This rappresent a facet specification schema / facet specification xml combo, but since for a given
 * facet schema, in the registry can be present at most one xml, this entity store only the id of the facet schema.
 * @author matteo
 */
@Entity
@NamedQueries(value={
		@NamedQuery(name=FacetEnt.EXPIRED, query="FROM FacetEnt WHERE ownership = false AND lease < current_timestamp()"),
		@NamedQuery(name=FacetEnt.RENEW, query="FROM FacetEnt WHERE ownership = true AND renew < current_timestamp()")
})
public class FacetEnt {
	
	static final String EXPIRED = "facetTypeEntExpired";
	static final String RENEW = "facetTypeEntRenew";
	
	/** UID of the facet (schema) */
	@Id private String facetSchemaID;

	private boolean ownership;
	
	/** lease timeout */
	@Temporal(TemporalType.TIMESTAMP)
	private Date lease;
	
	/** renew of this type (valid only if the type is shared) */
	@Temporal(TemporalType.TIMESTAMP)
	private Date renew;

	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	private ServiceEnt service;
	
	public FacetEnt() { /* empty constructor */ }

	public FacetEnt(ServiceEnt service, String facetSchemaID, boolean ownership, Date timeout) {
		this.service = service;
		this.facetSchemaID = facetSchemaID;
		this.ownership = ownership;
		if(ownership) {
			this.lease = null;
			this.renew = timeout;
		} else {
			this.lease = timeout;
			this.renew = null;
		}
	}

	/**
	 * @return the lease
	 */
	public Date getLease() {
		return lease;
	}

	/**
	 * @param lease the lease to set
	 */
	public void setLease(Date lease) {
		this.lease = lease;
	}

	/**
	 * Returns the facet schema id
	 * @return the uniqueID
	 */
	public String getFacetSchemaID() {
		return facetSchemaID;
	}

	/**
	 * @return the service
	 */
	public ServiceEnt getService() {
		return service;
	}

	public Date getRenew() {
		return renew;
	}
	
	public void setRenew(Date renew) {
		this.renew = renew;
	}

	public boolean isOwnership() {
		return ownership;
	}
	
	public static FacetEnt searchByID(EntityManager em, String facetSchemaID) throws NotFoundException {
		FacetEnt m = em.find(FacetEnt.class, facetSchemaID);
		if(m != null) return m;
		throw new NotFoundException("The facet with id " + facetSchemaID + " is not found in FacetEnt");
	}

	/**
	 * Get the list of all facet type expired
	 */
	@SuppressWarnings("unchecked")
	public static Collection<FacetEnt> getExpired(EntityManager em) {
		return em.createNamedQuery(EXPIRED).getResultList();
	}

	/**
	 * Get the list of the facet type to be renewed.
	 * Note that a facet type must be renewed only if it was maked as "shared"
	 */
	@SuppressWarnings("unchecked")
	public static Collection<FacetEnt> getRenew(EntityManager em) {
		return em.createNamedQuery(RENEW).getResultList();
	}
}