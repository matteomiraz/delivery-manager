/* This file is part of Delivery Manager.
 * (c) 2006 Matteo Miraz, Politecnico di Milano
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


package eu.secse.deliveryManager.db.filters;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQuery;
import javax.persistence.Query;

@NamedQuery(name=FacetInterest.FACET_FILTER_BY_SID, query="FROM FacetInterest WHERE serviceId = :serviceId")
@Entity
public class FacetInterest extends Interest {
	
	static final String FACET_FILTER_BY_SID = "FACET_FILTER_BY_SID";
	
	public enum FacetInterestType {
		additionalInformationFacet, additionalInformationFacetById, specificationFacet
	}
	
	@Enumerated(EnumType.ORDINAL)
	private FacetInterestType type;
	
	private String serviceId;
	private String facetSchema;
	private String XPath;
	
	private boolean additionalInformation;
	private String facetSchemaId;
	
	FacetInterest() { /* emtpy constructor */ }
	
	public FacetInterest(FacetInterestType type, String serviceId, String facetSchemaId, String facetSchema, String xpath) {
		switch (type) {
		case additionalInformationFacet:
		case additionalInformationFacetById:
			this.additionalInformation = true;
			break;
		case specificationFacet:
			this.additionalInformation = false;
		}
		
		this.type = type;
		
		this.serviceId = serviceId;
		this.facetSchemaId = facetSchemaId;
		this.facetSchema = facetSchema;
		this.XPath = xpath;
	}
	
	public String getFacetSchema() {
		return facetSchema;
	}
	
	public String getXPath() {
		return XPath;
	}

	public String getServiceId() {
		return serviceId;
	}
	
	public FacetInterestType getType() {
		return type;
	}
	
	public boolean isAdditionalInformation() {
		return additionalInformation;
	}

	public String getFacetSchemaId() {
		return this.facetSchemaId;
	}
	
	@SuppressWarnings("unchecked")
	public static Collection<FacetInterest> getFacetInterestBySid(EntityManager em, String serviceId) {
		Query q = em.createNamedQuery(FACET_FILTER_BY_SID);
		q.setParameter("serviceId", serviceId);
		return q.getResultList();
	}
}
