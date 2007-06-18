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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import eu.secse.deliveryManager.exceptions.NotFoundException;

@Entity
@NamedQueries(value={
		@NamedQuery(name=ServiceEnt.EXPIRED, query="FROM ServiceEnt WHERE ownership = false AND lease < current_timestamp()"),
		@NamedQuery(name=ServiceEnt.RENEW, query="FROM ServiceEnt WHERE ownership = true AND renew < current_timestamp()")
})
public class ServiceEnt {
	
	static final String EXPIRED = "serviceEntExpired"; 
	static final String RENEW = "serviceEntToRenew"; 
	
	/** UniqueID of the service */
	@Id private String serviceID;
	
	/** lease timeout */
	@Temporal(TemporalType.TIMESTAMP)
	private Date lease;
	
	/* BEGIN Ownership part */
	/** this service is created originally in the local registry */
	private boolean ownership;

	/** when renew this service (< lease) */
	@Temporal(TemporalType.TIMESTAMP)
	private Date renew;
	
	/** share all facet types and facet instances related to this service */
	private boolean shareFacets;
	/* END Ownership part */

	/** the service is NOT owned by the local system and the remote allows to add additional info facets*/
	private boolean allowedAddInfo;

	@OneToMany(mappedBy="service", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private Collection<FacetEnt> facets;
	

	ServiceEnt() { /* empty constructor */ }

	/** Create an owned service */
	public ServiceEnt(String serviceID, Date lease, Date renew, boolean shareFacets) {
		this.serviceID = serviceID;
		this.lease = lease;
		this.ownership = true;
		this.renew = renew;
		this.facets = new ArrayList<FacetEnt>();
		this.shareFacets = shareFacets;
		this.allowedAddInfo = false;
	}

	/** Create a remote service */
	public ServiceEnt(String serviceID, Date lease, boolean allowedAddInfo) {
		this.serviceID = serviceID;
		this.lease = lease;
		this.ownership = false;
		this.renew = null;
		this.facets = new ArrayList<FacetEnt>();
		this.allowedAddInfo = allowedAddInfo;
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
	 * @return the renew
	 */
	public Date getRenew() {
		return renew;
	}

	/**
	 * @param renew the renew to set
	 */
	public void setRenew(Date renew) {
		this.renew = renew;
	}

	public void setShareFacets(boolean shareFacets) {
		this.shareFacets = shareFacets;
	}
	
	public boolean isShareFacets() {
		return shareFacets;
	}
	
	/**
	 * @return the facetType
	 */
	public Collection<FacetEnt> getFacets() {
		return facets;
	}
	
	public boolean isAllowedAddInfo() {
		return allowedAddInfo;
	}

	/**
	 * @return the ownership
	 */
	public boolean isOwnership() {
		return ownership;
	}
	
	public String getServiceID() {
		return serviceID;
	}
	
	public static ServiceEnt searchByID(EntityManager em, String serviceID) throws NotFoundException {
		ServiceEnt m = em.find(ServiceEnt.class, serviceID);
		if(m != null) return m;
		throw new NotFoundException("The service with id " + serviceID + " is not found in ServiceEnt");
	}
	
	/**
	 * Get the list of all expired services 
	 */
	@SuppressWarnings("unchecked")
	public static Collection<ServiceEnt> getExpired(EntityManager em) {
		return em.createNamedQuery(EXPIRED).getResultList();
	}

	/**
	 * Get the list of the services to be renewed.
	 */
	@SuppressWarnings("unchecked")
	public static Collection<ServiceEnt> getRenew(EntityManager em) {
		return em.createNamedQuery(RENEW).getResultList();
	}
}