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
@NamedQuery(name=IncompleteFacetXML.get_by_xml_id,query="from IncompleteFacetXML where xmlId=:xmlid"),
@NamedQuery(name=IncompleteFacetXML.get_by_serviceid,query="from IncompleteFacetXML where serviceId=:serviceid and sourcedm=:sourcedm")
})
public class IncompleteFacetXML extends IncompleteElement {

	public static final String get_by_xml_id="get_incomplete_xml_by_xml_id";
	public static final String get_by_serviceid="get_incomplete_xml_by_service_id";
	
	private String serviceId;
	private String facetSchemaId;
	private String xmlId;
	private boolean isAddInfo;
	private long facetTimeStamp;

	public IncompleteFacetXML(DeliveryManagerGossipInfo from, String xmlid, String facetschemaid, String serviceid, boolean isAddInfo, long facetTimestamp) {
		super(from);
		facetSchemaId = facetschemaid;
		serviceId = serviceid;
		xmlId = xmlid;
		this.isAddInfo=isAddInfo;
		this.facetTimeStamp=facetTimestamp;
	}

	IncompleteFacetXML() {
		super();		
	}

	public String getFacetSchemaId() {
		return facetSchemaId;
	}

	public void setFacetSchemaId(String facetSchemaId) {
		this.facetSchemaId = facetSchemaId;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getXmlId() {
		return xmlId;
	}

	public void setXmlId(String xmlId) {
		this.xmlId = xmlId;
	}

	public boolean isAddInfo() {
		return isAddInfo;
	}

	public void setAddInfo(boolean isAddInfo) {
		this.isAddInfo = isAddInfo;
	}

	public long getFacetTimeStamp() {
		return facetTimeStamp;
	}

	public void setFacetTimeStamp(long facetTimeStamp) {
		this.facetTimeStamp = facetTimeStamp;
	}
	
	public static IncompleteFacetXML getIncompleteFacetXML(
			EntityManager em,String serviceid, String facetid, String xmlid) {
		Query q=em.createNamedQuery(IncompleteFacetXML.get_by_xml_id);
		q.setParameter("xmlid",xmlid);
		IncompleteFacetXML incompletexml=null;
		try {
			incompletexml=(IncompleteFacetXML)q.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
		return incompletexml;
	}
	
	/** This method is used to retrieve all incomplete facets belonging to a given service, from a given delivery manager*/
	@SuppressWarnings("unchecked")
	public static Collection<IncompleteFacetXML> getByServiceId(EntityManager em,String serviceid, DeliveryManagerGossipInfo from) {
		Query q=em.createNamedQuery(get_by_serviceid);
		q.setParameter("serviceid",serviceid);
		q.setParameter("sourcedm",from);		
		Collection<IncompleteFacetXML> iface=(Collection<IncompleteFacetXML>)q.getResultList();
		return iface;		
	}
	
	public static Collection<IncompleteFacetXML> getXMLToBeFetched(EntityManager em) {
		Collection<IncompleteElement> incomplete=IncompleteElement.getToBeFetch(em);
		Vector<IncompleteFacetXML> inc_xml=new Vector<IncompleteFacetXML>();
		for (IncompleteElement ele:incomplete) {			
			if (ele instanceof IncompleteFacetXML) {
				inc_xml.add((IncompleteFacetXML)ele);
			}
		}
		return inc_xml;
	}

}
