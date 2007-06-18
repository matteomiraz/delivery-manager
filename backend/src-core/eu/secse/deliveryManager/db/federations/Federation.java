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


package eu.secse.deliveryManager.db.federations;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@Entity
@NamedQueries({
	@NamedQuery(name="federationGetAll", query="FROM Federation")
})
public class Federation {
	@Id
	private String federationName;

	private long interestId;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="federation", cascade=CascadeType.ALL)
	private Collection<FederatedElement> elements;

	Federation() { /* emtpy constructor */ }
	
	public Federation(String federationName) {
		this.federationName = federationName;
	}

	public void setFederationName(String federationName) {
		this.federationName = federationName;
	}
	
	public String getFederationName() {
		return federationName;
	}
	
	@SuppressWarnings("unchecked")
	public static Collection<Federation> getAllFederations(EntityManager em) {
		return em.createNamedQuery("federationGetAll").getResultList();
	}

	public Collection<FederatedElement> getElements() {
		return elements;
	}
	
	public long getInterestId() {
		return interestId;
	}
	
	public void setInterestId(long interestId) {
		this.interestId = interestId;
	}
}
