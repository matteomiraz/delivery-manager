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

package eu.secse.deliveryManager.federations.gossip.data;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Query;

@Entity
@NamedQueries(value={
@NamedQuery(name=RequestedFacetSchema.get_by_id,query="from RequestedFacetSchema where serviceid = :serviceid and facetid = :facetid"),
@NamedQuery(name=RequestedFacetSchema.get_by_service_id,query="from RequestedFacetSchema where serviceid = :serviceid")
})
public class RequestedFacetSchema extends RequestedElement {
	private String serviceid;
	private String facetid;	
	
	public static final String get_by_id="get_facet_with_pending_details_requested";
	public static final String get_by_service_id="get_facet_with_pending_details_requested_by_service_id";
	
	public RequestedFacetSchema(String facetid, String serviceid) {
		super();
		this.facetid = facetid;
		this.serviceid = serviceid;
	}
	
	
	public RequestedFacetSchema() {
		super();
	}


	public String getFacetid() {
		return facetid;
	}
	
	public void setFacetid(String facetid) {
		this.facetid = facetid;
	}
	
	public String getServiceid() {
		return serviceid;
	}
	
	public void setServiceid(String serviceid) {
		this.serviceid = serviceid;
	}
	
	public static RequestedFacetSchema getRequestedSchema(EntityManager em,String serviceid, String facetid) {
		Query facet=em.createNamedQuery(RequestedFacetSchema.get_by_id);
		facet.setParameter("serviceid",serviceid);
		facet.setParameter("facetid",facetid);
		RequestedFacetSchema requestedfacet=null;
		try {
			requestedfacet=(RequestedFacetSchema)facet.getSingleResult();			
		} catch (NoResultException nre) {
			return null;
		}
		return requestedfacet;
	}
	
}
