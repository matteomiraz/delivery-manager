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
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Query;

@Entity
@NamedQuery(name=RequestedFacetXML.get_by_id,query="from RequestedFacetXML where serviceid = :serviceid and facetid = :facetid and xmlid= :xmlid")
public class RequestedFacetXML extends RequestedElement {
	
	private String serviceid;
	private String facetid;
	private String xmlid;
	
	public static final String get_by_id="get_requested_facet_xml_by_id"; 
	public static final String get_by_service_and_facet_id="get_requested_facet_xml_by_service_and_facet_id";
	
	
	RequestedFacetXML() {
		super();	
	}

	public RequestedFacetXML(String facetid, String serviceid, String xmlid) {
		super();
		this.facetid = facetid;
		this.serviceid = serviceid;
		this.xmlid = xmlid;
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
	
	public String getXmlid() {
		return xmlid;
	}
	
	public void setXmlid(String xmlid) {
		this.xmlid = xmlid;
	}
	
	public static RequestedFacetXML getRequestedFacetXML(EntityManager em,String serviceid, String facetid, String xmlid) {
		Query requestedxml=em.createNamedQuery(RequestedFacetXML.get_by_id);
		requestedxml.setParameter("serviceid",serviceid);
		requestedxml.setParameter("facetid",facetid);
		requestedxml.setParameter("xmlid",xmlid);
		RequestedFacetXML reqxml=null;
		try {
			reqxml=(RequestedFacetXML)requestedxml.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
		return reqxml;
	}
}
