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

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

@Entity
@NamedQuery(name="SEARCH_FEDERATED_XML", query="SELECT f FROM FederatedXml AS f WHERE f.federation = :federation AND f.element = :element")
public class FederatedXml extends FederatedElement {

	@OneToOne(mappedBy="xml", optional=false)
	private FederatedFacet facet;

	public FederatedXml() { }
	
	public FederatedXml(FederationEnt federation, ElementEnt element, FederatedElementExtraInfo extraInfo) {
		super(federation, element, extraInfo);
		
		if(!(getElement() instanceof FacetXmlEnt))
			System.err.println("WARNING: Created a FederatedXml with an element with type " + getElement().getClass().getName());

	}
	
	public FederatedFacet getFacet() {
		return facet;
	}
	
	public void setFacet(FederatedFacet facet) {
		this.facet = facet;
	}

	public FacetXmlEnt getXml() {
		return (FacetXmlEnt) getElement();
	}
	
	/*
	 * This method is not implemented with a query because ElementEnt as a composite primary key.
	 * La classe della chiave primaria è ElementEntPK, che ha due campi: id e type.
	 * Il campo id viene confuso da jBoss e viene espanso, dando degli errori.
	 * In futuro conviene rinominare questo campo con un altro nome != id
	 */
	public static FederatedXml search(EntityManager em, ElementEnt element, FederationEnt federation) {
		try {
			ElementEnt elemResult = em.find(ElementEnt.class, element.getElemPK());
			if(elemResult!= null){
				if(elemResult instanceof FacetXmlEnt) {
					FacetXmlEnt facetXmlEnt = (FacetXmlEnt)elemResult;
					for(FederatedElement fe: facetXmlEnt.getFederatedElement()){
						if (fe instanceof FederatedXml) {
							FederatedXml fx = (FederatedXml) fe;
							if(fe.getFederation().equals(federation))
								return fx;
						}
					}
				}
			}
		}
		/*try {
			Query q = em.createNamedQuery("SEARCH_FEDERATED_XML");
			q.setParameter("federation", federation);
			q.setParameter("element", element);
			return (FederatedXml) q.getSingleResult();
		} */catch (Throwable e) {
			return null;
		}
		return null;
	}
}
