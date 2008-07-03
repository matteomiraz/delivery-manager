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

package eu.secse.deliveryManager.data;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import eu.secse.deliveryManager.exceptions.NotFoundException;

@Entity
public class ServiceEnt extends ElementEnt {

	public static final String SERVICE_ENT = "ServiceEnt";

	/**
	 * the owner of the service allows anyone to add additional info facets
	 anyone can add additional info facets in his registry but he can't share the modified information
	 */
	private boolean allowedAddInfo;

	@OneToMany(mappedBy="service", fetch=FetchType.LAZY, cascade={CascadeType.ALL,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
	private Collection<FacetEnt> facets;
	
	ServiceEnt() { /* empty constructor */ }

	public ServiceEnt(String id, boolean ownership, boolean allowedAddInfo) {
		super(id,SERVICE_ENT, ownership);
		this.allowedAddInfo = allowedAddInfo;
		this.facets = new ArrayList<FacetEnt>();
	}

	public boolean isAllowedAddInfo() {
		return allowedAddInfo;
	}

	public void setAllowedAddInfo(boolean allowedAddInfo) {
		this.allowedAddInfo = allowedAddInfo;
	}

	public Collection<FacetEnt> getFacets() {
		return facets;
	}

	public void setFacets(Collection<FacetEnt> facets) {
		this.facets = facets;
	}

	public void addFacet(FacetEnt facet) {
		this.facets.add(facet);
	}

	@Override
	public String toString() {
		return "Service " + this.getElemPK().toString();
	}
	
	public static ServiceEnt searchByID(EntityManager em, String serviceID) throws NotFoundException {
		ServiceEnt m = em.find(ServiceEnt.class, new ElementEntPK(serviceID, SERVICE_ENT));
		if(m != null) return m;
		throw new NotFoundException("The service with id " + serviceID + " is not found in ServiceEnt");
	}

	
}