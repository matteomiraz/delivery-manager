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

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Facet entity. This rappresent a facet specification schema / facet specification xml combo, but since for a given facet schema, in the registry can be present at most one xml, this entity store only the id of the facet schema.
 */
@Entity
public class FacetEnt extends ElementEnt {
	
	public static final String FACET_ENT = "FacetEnt";

	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	private ServiceEnt service;

	@OneToOne(optional=true, fetch=FetchType.LAZY,cascade={CascadeType.REMOVE})
	private FacetXmlEnt xml;
	
	/** If false the facet is a specification facet; if true is and additional information. */
	private boolean addInfo;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;
	
	public FacetEnt() { /* empty constructor */ }

	public FacetEnt(String id, ServiceEnt service, boolean addInfo, boolean ownership, Date timestamp) {
		super(id,FACET_ENT, ownership);
		this.service = service;
		this.timestamp = timestamp;
		this.addInfo = addInfo;
		service.getFacets().add(this);
		
	}

	public ServiceEnt getService() {
		return service;
	}
	
	public void setService(ServiceEnt service) {
		this.service = service;
	}

	public FacetXmlEnt getXml() {
		return xml;
	}
	
	public void setXml(FacetXmlEnt xml) {
		this.xml = xml;
	}
	
	public boolean isAddInfo() {
		return addInfo;
	}
	
	public void setAddInfo(boolean addInfo) {
		this.addInfo = addInfo;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
	public String toString() {
		return "Facet " + this.getElemPK().toString();
	}
}