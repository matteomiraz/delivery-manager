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

package eu.secse.deliveryManager.sharing.data.filters;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Query;

import eu.secse.deliveryManager.core.FacetInterest;

@Entity
@NamedQuery(name=InterestFacetEnt.FACET_FILTER_BY_SID, query="FROM InterestFacetEnt WHERE serviceId = :serviceId")
public class InterestFacetEnt extends InterestEnt {
	
	static final String FACET_FILTER_BY_SID = "FACET_FILTER_BY_SID";
	
	public enum FacetInterestType {
		additionalInformationFacet, additionalInformationFacetById, specificationFacet
	}
	
	@Enumerated(EnumType.ORDINAL)
	private FacetInterestType type;
	
	private String serviceId;
	
	@Lob
	private FacetInterest []interest;
	
	private boolean additionalInformation;
	private String facetSchemaId;
	
	InterestFacetEnt() { 	}
	
	public InterestFacetEnt(FacetInterestType type, String serviceId, String facetSchemaId, FacetInterest []interest, String description) {
		super(description);
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
		this.interest = interest;
	}
	
	public FacetInterest[] getFacetInterest() {
		return interest;
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
	public static Collection<InterestFacetEnt> getFacetInterestBySid(EntityManager em, String serviceId) {
		Query q = em.createNamedQuery(FACET_FILTER_BY_SID);
		q.setParameter("serviceId", serviceId);
		return q.getResultList();
	}
}
