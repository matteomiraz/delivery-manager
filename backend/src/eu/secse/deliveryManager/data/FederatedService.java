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

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@Entity
@NamedQuery(name="SEARCH_FEDERATED_SERVICE2", query="SELECT f FROM FederatedService AS f WHERE f.federation = :federation ")
public class FederatedService extends FederatedElement {

	@OneToMany(mappedBy="service", fetch=FetchType.LAZY, cascade={CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.REMOVE})
	private Collection<FederatedFacet> facets;
	
	public FederatedService() { /* empty constructor */ }
	
	public FederatedService(FederationEnt federation, ElementEnt element, FederatedElementExtraInfo extraInfo) {
		super(federation, element, extraInfo);
		
		if(!(getElement() instanceof ServiceEnt))
			System.err.println("WARNING: Created a FederatedService with an element with type " + getElement().getClass().getName());
	}
	
	public Collection<FederatedFacet> getFacets() {
		return facets;
	}
	
	public void setFacets(Collection<FederatedFacet> facets) {
		this.facets = facets;
	}
	
	public void addFacet(FederatedFacet facet) {
		this.facets.add(facet);
	}
	
	/*
	 * This method is not implemented with a query because ElementEnt as a composite primary key.
	 * La classe della chiave primaria è ElementEntPK, che ha due campi: id e type.
	 * Il campo id viene confuso da jBoss e viene espanso, dando degli errori.
	 * In futuro conviene rinominare questo campo con un altro nome != id
	 */
	public static FederatedService search(EntityManager em, ElementEnt element, FederationEnt federation) {
		try {
			ElementEnt elemResult = em.find(ElementEnt.class, element.getElemPK());
			if(elemResult!= null){
				if(elemResult instanceof ServiceEnt) {
					ServiceEnt servEnt = (ServiceEnt)elemResult;
					for(FederatedElement fe: servEnt.getFederatedElement()){
						if (fe instanceof FederatedService) {
							FederatedService fs = (FederatedService) fe;
							if(fe.getFederation().equals(federation))
								return fs;
						}
					}
				}
			}
			/*Query q = em.createNamedQuery("SEARCH_FEDERATED_SERVICE2");
			q.setParameter("federation", federation);
			System.err.println("Running the XXX query");
			
			return (FederatedService)q.getSingleResult();
			for(FederatedService fs : federatedServices){
				if(fs.getElement().getElemPK().equals(element.getElemPK()))
					return fs;
			}*/
		} catch (Throwable e) {
			return null;
		}
		return null;
	
	}

	public ServiceEnt getService() {
		return (ServiceEnt) getElement();
	}
}
