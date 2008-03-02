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
import javax.persistence.OneToOne;

/**
 * Facet entity. This rappresent a facet specification schema / facet specification xml combo, but since for a given facet schema, in the registry can be present at most one xml, this entity store only the id of the facet schema.
 */
@Entity
public class FacetXmlEnt extends ElementEnt {
	
	public static final String FACET_XML_ENT = "FacetXmlEnt";
	@OneToOne(mappedBy="xml", optional=false)
	private FacetEnt facet;

	public FacetXmlEnt() { /* empty constructor */ }

	public FacetXmlEnt(FacetEnt facet, String id) {
		super(id, FACET_XML_ENT,facet.isOwnership());
		this.facet = facet;
		facet.setXml(this);
	}

	public FacetEnt getFacet() {
		return facet;
	}
	
	public void setFacet(FacetEnt facet) {
		this.facet = facet;
	}
	
	@Override
	public String toString() {
		return "FacetXml " + this.getElemPK().toString();
	}
}