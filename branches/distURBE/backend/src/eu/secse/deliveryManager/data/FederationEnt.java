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
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/** This class represents joined federations
 * 
 *
 */
//TODO: Parametrizzare la query anche per gli altri metodi
@Entity
@NamedQueries(value={
@NamedQuery(name=FederationEnt.FIND_ALL,query="from FederationEnt"),
@NamedQuery(name=FederationEnt.FIND_GOSSIP,query="from FederationEnt where method = 'gossip'"),
@NamedQuery(name=FederationEnt.FIND_PUBSUB,query="from FederationEnt where method = 'ps'"),
@NamedQuery(name=FederationEnt.FIND_PUBSUBREP,query="from FederationEnt where method = 'psr'")
})
public class FederationEnt {
	
	public static final String FIND_ALL="find_all";
	public static final String FIND_GOSSIP="find_gossip";
	public static final String FIND_PUBSUB="find_pubsub";
	public static final String FIND_PUBSUBREP="find_pubsubrep";
	
	@Id
	private String id;
	
	private String name;
	
	//TODO: verificare che gli altri partecipanti alla sottoscrizione si desottoscrivano
	@Temporal(TemporalType.TIMESTAMP)
	private Date lease;
	
	private boolean ownership;
	
	private String method;

	//TODO: Check: added cascade for removal
	@OneToMany(fetch=FetchType.LAZY, mappedBy="federation", cascade={CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.REMOVE})
	private Collection<FederatedElement> elements;

	//TODO: Check: added cascade for removal
	@OneToMany(fetch=FetchType.LAZY, mappedBy="federation", cascade={CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.REMOVE})
	private Collection<FederatedPromotion> promotions;

	@OneToOne(optional=true)
	private FederationExtraInfo extraInfo;
	
	FederationEnt() { /* emtpy constructor */ }

	public FederationEnt(String id, String name, Date lease, boolean ownership, String proxyName, Collection<FederatedElement> elements, FederationExtraInfo extra) {
		super();
		this.id = id;
		this.name = name;
		this.lease = lease;
		this.ownership = ownership;
		this.method = proxyName;
		this.elements = elements;
		this.extraInfo = extra;
		this.elements = new ArrayList<FederatedElement>();
		this.promotions = new ArrayList<FederatedPromotion>();
	}

	public Collection<FederatedElement> getElements() {
		return elements;
	}

	public void setElements(Collection<FederatedElement> elements) {
		this.elements = elements;
	}

	public void addElement(FederatedElement element) {
		this.elements.add(element);
	}

	public Collection<FederatedPromotion> getPromotions() {
		return promotions;
	}
	
	public void setPromotions(Collection<FederatedPromotion> promotions) {
		this.promotions = promotions;
	}
	
	public void addPromotion(FederatedPromotion promotion) {
		this.promotions.add(promotion);
	}
	
	public FederationExtraInfo getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(FederationExtraInfo extra) {
		this.extraInfo = extra;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getLease() {
		return lease;
	}

	public void setLease(Date lease) {
		this.lease = lease;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isOwnership() {
		return ownership;
	}

	public void setOwnership(boolean ownership) {
		this.ownership = ownership;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((id == null) ? 0 : id.hashCode());
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
		final FederationEnt other = (FederationEnt) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Federation " + this.name;
	}
}
