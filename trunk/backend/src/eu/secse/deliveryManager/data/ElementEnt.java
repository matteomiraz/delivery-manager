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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MapKey;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Query;

@Entity
@NamedQueries(value={
@NamedQuery(name="IS_ELEMENT_EXTRA_INFO_ALIVE", query="SELECT count(x.id) FROM ElementExtraInfo AS x WHERE x.element = :element AND x.keepsAlive = true"),
@NamedQuery(name="count_alive_federated_elements", query="SELECT count(x.id) FROM ElementEnt AS e JOIN e.federatedElement AS  x  WHERE e = :element AND x.received = true")
})
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class ElementEnt {
	/**
	 * UniqueID of the element
	 */
	@EmbeddedId private ElementEntPK elemPK;
	
	/** this element is created originally in the local registry */
	private boolean ownership;
	
	@OneToMany(mappedBy="element", cascade=CascadeType.ALL)
	@MapKey(name="infoType")
	private Map<String, ElementExtraInfo> extraInfo;

	@OneToMany(fetch=FetchType.LAZY,mappedBy="element", cascade=CascadeType.ALL)
	private Collection<FederatedElement> federatedElement;

	@OneToMany(mappedBy="element", cascade=CascadeType.ALL)
	private Collection<FederatedPromotion> federatedPromotion;

	protected ElementEnt(String id,String type, boolean ownership) {
		super();
		this.elemPK = new ElementEntPK(id, type);
		this.ownership = ownership;
		this.federatedElement = new ArrayList<FederatedElement>();
		this.federatedPromotion = new ArrayList<FederatedPromotion>();
		this.extraInfo=new HashMap<String,ElementExtraInfo>();
	}

	public ElementEnt() { /* emtpy constructor */ }

	public Map<String, ElementExtraInfo> getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(Map<String, ElementExtraInfo> extraInfo) {
		this.extraInfo = extraInfo;
	}


	public boolean isOwnership() {
		return ownership;
	}

	public void setOwnership(boolean ownership) {
		this.ownership = ownership;
	}


	public Collection<FederatedElement> getFederatedElement() {
		return federatedElement;
	}

	public void setFederatedElement(Collection<FederatedElement> federatedElement) {
		this.federatedElement = federatedElement;
	}
	
	public void addFederatedElement(FederatedElement federatedElement) {
		this.federatedElement.add(federatedElement);
	}
	
	public Collection<FederatedPromotion> getFederatedPromotion() {
		return federatedPromotion;
	}
	
	public void setFederatedPromotion(Collection<FederatedPromotion> federatedPromotion) {
		this.federatedPromotion = federatedPromotion;
	}

	public void addFederatedPromotion(FederatedPromotion federatedPromotion) {
		this.federatedPromotion.add(federatedPromotion);
	}

	
	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((elemPK == null) ? 0 : elemPK.hashCode());
		result = PRIME * result + ((extraInfo == null) ? 0 : extraInfo.hashCode());
		result = PRIME * result + ((federatedElement == null) ? 0 : federatedElement.hashCode());
		result = PRIME * result + ((federatedPromotion == null) ? 0 : federatedPromotion.hashCode());
		result = PRIME * result + (ownership ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ElementEnt other = (ElementEnt) obj;
		if (elemPK == null) {
			if (other.elemPK != null)
				return false;
		} else if (!elemPK.equals(other.elemPK))
			return false;
		if (extraInfo == null) {
			if (other.extraInfo != null)
				return false;
		} else if (!extraInfo.equals(other.extraInfo))
			return false;
		if (federatedElement == null) {
			if (other.federatedElement != null)
				return false;
		} else if (!federatedElement.equals(other.federatedElement))
			return false;
		if (federatedPromotion == null) {
			if (other.federatedPromotion != null)
				return false;
		} else if (!federatedPromotion.equals(other.federatedPromotion))
			return false;
		if (ownership != other.ownership)
			return false;
		return true;
	}

	public int getAliveExtraInfo(EntityManager em) {
		Query q = em.createNamedQuery("IS_ELEMENT_EXTRA_INFO_ALIVE");
		q.setParameter("element", this);
		return ((Long) q.getSingleResult()).intValue();
	}
	
	public int getAliveFederatedElements() {
		Vector<FederatedElement> elements=new Vector<FederatedElement>(federatedElement);
		int i=0;
		for (FederatedElement fed:elements) {
			if (fed.isReceived()) i++;
		}
		return i;
	}

	public ElementEntPK getElemPK() {
		return elemPK;
	}

	public void setElemPK(ElementEntPK elemPK) {
		this.elemPK = elemPK;
	}
	
}
