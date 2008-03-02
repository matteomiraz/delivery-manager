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
@NamedQuery(name=DeletedFacetXML.get_by_id_and_federation,query="from DeletedFacetXML where xmlId = :xmlid and federationid = :federationid and serviceId = :serviceid and facetSchemaId = :facetid")
public class DeletedFacetXML extends DeletedElement {
	
	
	public static final String get_by_id_and_federation="get_deleted_xml_by_id_and_federation";
	private String serviceId;
	private String facetSchemaId;
	private String xmlId;


	public DeletedFacetXML(String federationid, String facetSchemaId, String serviceId, String xmlId,long deletiontime) {
		super(federationid,deletiontime);
		this.facetSchemaId = facetSchemaId;
		this.serviceId = serviceId;
		this.xmlId=xmlId;
	}
	
	

	DeletedFacetXML() {
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
	
	public static DeletedFacetXML getDeletedFacetXML(EntityManager em,String federationid, String serviceid, String facetid, String xmlid) {
		Query q=em.createNamedQuery(DeletedFacetXML.get_by_id_and_federation);
		q.setParameter("xmlid",serviceid);
		q.setParameter("facetid",facetid);
		q.setParameter("serviceid",serviceid);
		q.setParameter("federationid",federationid);
		DeletedFacetXML deletedxml=null;
			try {
				deletedxml=(DeletedFacetXML)q.getSingleResult();
			} catch(NoResultException nre) {
				return null;
			} 
		return deletedxml;
	}
}
