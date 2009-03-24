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
import javax.persistence.NoResultException;
import javax.persistence.Query;

@Entity
@javax.persistence.NamedQuery(name=DeletedFacetSchema.get_by_federation_and_id,query="from DeletedFacetSchema where facetid = :facetid and serviceid = :serviceid and federationid = :federationid")
public class DeletedFacetSchema extends DeletedElement {
	
	public static final String get_by_federation_and_id="get_deleted_facet_schema_by_federation_and_id";
		
	private String serviceid;
	
	
	private String facetid;
	
	
	
	DeletedFacetSchema() {
		super();		
	}





	public DeletedFacetSchema(String federationid, String facetid, String serviceid,long deletiontime) {
		super(federationid,deletiontime);		
		this.facetid = facetid;
		this.serviceid = serviceid;		
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
	
	public static DeletedFacetSchema getDeletedFacetSchema(EntityManager em,String federationid, String serviceid, String facetid) {
		Query q=em.createNamedQuery(DeletedFacetSchema.get_by_federation_and_id);
		q.setParameter("facetid",serviceid);
		q.setParameter("serviceid",facetid);
		q.setParameter("federationid",federationid);
		DeletedFacetSchema delfacet=null;
		try {
			delfacet=(DeletedFacetSchema)q.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
		return delfacet;
	}

	
	

}
