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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

/**
 * Rappresenta un elemento promosso in una federazione.
 */

@NamedQueries(value={
@NamedQuery(name=FederatedPromotion.getByElementAndFederation, query="SELECT f FROM FederatedPromotion AS f WHERE f.federation = :federation AND f.element = :element"),
@NamedQuery(name=FederatedPromotion.getByFederationEnt, query="FROM FederatedPromotion where federation = :federation")
})
@Entity
public class FederatedPromotion {
	
	@Id
	@GeneratedValue
	private long id;
	
	public static final String getByFederationEnt="get_federated_promotion_by_federation_ent";
	public static final String getByElementAndFederation="SEARCH_FEDERATED_PROMOTION";
	static final String FEDERATED_ELEMENT_TO_RENEW = "FederatedElementToRenew";
	
	@ManyToOne(optional=false)
	private FederationEnt federation;
	
	@ManyToOne(optional=false)
	private ElementEnt element;
	
	@OneToOne(optional=true,cascade=CascadeType.ALL)
	private FederatedPromotionExtraInfo extraInfo;
	
	/** If true (and linked to a service), shares all facets of the service. */
	private boolean shareAll;
	
	protected FederatedPromotion() { /* empty constructor */ }

	//TODO (check): No public constructors, how can I create instances???

	//FederatedPromotion(FederationEnt federation, ElementEnt element, FederatedPromotionExtraInfo extraInfo, boolean shareAll) {
	public FederatedPromotion(FederationEnt federation, ElementEnt element, FederatedPromotionExtraInfo extraInfo, boolean shareAll) {
		this.federation = federation;
		this.element = element;
		this.extraInfo = extraInfo;
		this.shareAll = shareAll;
	}

	public long getId() {
		return id;
	}
	
	public ElementEnt getElement() {
		return element;
	}

	public void setElement(ElementEnt element) {
		this.element = element;
	}

	public FederationEnt getFederation() {
		return federation;
	}

	public void setFederation(FederationEnt federation) {
		this.federation = federation;
	}

	public FederatedPromotionExtraInfo getExtraInfo() {
		return extraInfo;
	}
	
	public void setExtraInfo(FederatedPromotionExtraInfo extraInfo) {
		this.extraInfo = extraInfo;
	}
	
	public boolean isShareAll() {
		return shareAll;
	}
	
	
	/*
	 * This method is not implemented with a query because ElementEnt as a composite primary key.
	 * La classe della chiave primaria è ElementEntPK, che ha due campi: id e type.
	 * Il campo id viene confuso da jBoss e viene espanso, dando degli errori.
	 * In futuro conviene rinominare questo campo con un altro nome != id
	 */
	public static FederatedPromotion search(EntityManager em, ElementEnt element, FederationEnt federation) {
		try {
			ElementEnt elemResult = em.find(ElementEnt.class, element.getElemPK());
			if(elemResult!= null){
				for(FederatedPromotion fp: elemResult.getFederatedPromotion()){
						if(fp.getFederation().equals(federation))
								return fp;
						}
					}
				}
			 catch (Throwable e) {
			return null;
		}
			return null;
	}

}
