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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

@Entity
@NamedQuery(name="SEARCH_FEDERATED_FACET", query="SELECT f FROM FederatedFacet AS f WHERE f.federation = :federation AND f.element = :element")
public class FederatedFacet extends FederatedElement {

	//Added cascade to handle deletion of service
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	private FederatedService service;

	@OneToOne(optional=true, fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	private FederatedXml xml;
	
	public FederatedFacet() {	}
	
	public FederatedFacet(FederatedService service,FederationEnt federation, ElementEnt element, FederatedElementExtraInfo extraInfo) {
		super(federation, element, extraInfo);
		
		if(!(getElement() instanceof FacetEnt))
			System.err.println("WARNING: Created a FederatedFacet with an element with type " + getElement().getClass().getName());
		
		else {
			this.service=service;
			FacetEnt facet = (FacetEnt) getElement();
			if(!this.service.getElement().equals(facet.getService()))
				System.err.println("WARNING: FederatedFacet consistency error");
		}
	}
	
	public FederatedService getService() {
		return service;
	}
	
	public void setService(FederatedService service) {
		this.service = service;
	}
	
	public FederatedXml getXml() {
		return xml;
	}
	
	public void setXml(FederatedXml xml) {
		this.xml = xml;
	}

	public FacetEnt getFacet() {
		return (FacetEnt) getElement();
	}

	/*
	 * This method is not implemented with a query because ElementEnt as a composite primary key.
	 * La classe della chiave primaria è ElementEntPK, che ha due campi: id e type.
	 * Il campo id viene confuso da jBoss e viene espanso, dando degli errori.
	 * In futuro conviene rinominare questo campo con un altro nome != id
	 */
	public static FederatedFacet search(EntityManager em, ElementEnt element, FederationEnt federation) {
		try {
			ElementEnt elemResult = em.find(ElementEnt.class, element.getElemPK());
			if(elemResult!= null){
				if(elemResult instanceof FacetEnt) {
					FacetEnt facetEnt = (FacetEnt)elemResult;
					for(FederatedElement fe: facetEnt.getFederatedElement()){
						if (fe instanceof FederatedFacet) {
							FederatedFacet ff = (FederatedFacet) fe;
							if(fe.getFederation().equals(federation))
								return ff;
						}
					}
				}
			}
		}
		 catch (Throwable e) {
			return null;
		}
		 return null;
	}

}
