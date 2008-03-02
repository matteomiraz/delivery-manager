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

import java.util.Collection;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Query;

@Entity
@NamedQueries(value={
@NamedQuery(name=IncompleteFacetSchema.get_by_facet_schema_id,query="from IncompleteFacetSchema where facetid=:facetid"),
@NamedQuery(name=IncompleteFacetSchema.get_by_service,query = "from IncompleteFacetSchema  where serviceid=:serviceid and sourcedm=:sourcedm")
})
public class IncompleteFacetSchema extends
		IncompleteElement {
	
	public static final String get_by_facet_schema_id="get_incomplete_facetschema_by_facetschema_id";
		
	public static final String get_by_service="get_incomplete_facetschema_by_service_id";
	
	private String serviceid;
		
	private long timestamp;
	
	private String facetid;
	
	private boolean isAddInfo;
	
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



	public long getTimestamp() {
		return timestamp;
	}



	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}


	IncompleteFacetSchema() {
		super();		
	}


	public IncompleteFacetSchema(DeliveryManagerGossipInfo from, String facetid, String serviceid, long timestamp, boolean isAddInfo) {
		super(from);
		this.facetid = facetid;
		this.serviceid = serviceid;
		this.timestamp = timestamp;
		this.isAddInfo=isAddInfo;
	}


	public boolean isAddInfo() {
		return isAddInfo;
	}


	public void setAddInfo(boolean isAddInfo) {
		this.isAddInfo = isAddInfo;
	}
	
	/** This method is used to retrieve all incomplete facets belonging to a given service, from a given delivery manager*/
	@SuppressWarnings("unchecked")
	public static Collection<IncompleteFacetSchema> getByServiceId(EntityManager em,String serviceid, DeliveryManagerGossipInfo from) {
		Query q=em.createNamedQuery(get_by_service);
		q.setParameter("serviceid",serviceid);
		q.setParameter("sourcedm",from);		
		Collection<IncompleteFacetSchema> iface=(Collection<IncompleteFacetSchema>)q.getResultList();
		return iface;		
	}


	
	public static IncompleteFacetSchema getIncompleteFacetSchema(
			EntityManager em,String serviceid, String facetid) {
		Query q=em.createNamedQuery(IncompleteFacetSchema.get_by_facet_schema_id);
		q.setParameter("facetid",facetid);
		IncompleteFacetSchema incompletefacet=null;
		try {
			incompletefacet=(IncompleteFacetSchema)q.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
		return incompletefacet;
	}
	
	public static Collection<IncompleteFacetSchema> getSchemasToBeFetch(EntityManager em) {
		Collection<IncompleteElement> elements=IncompleteElement.getToBeFetch(em);
		Vector<IncompleteFacetSchema> incomplete_schema=new Vector<IncompleteFacetSchema>();
		for (IncompleteElement element:elements) {
			if (element instanceof IncompleteFacetSchema) {
				incomplete_schema.add((IncompleteFacetSchema)element);
			}
		}
		return incomplete_schema;
	}

	

}
