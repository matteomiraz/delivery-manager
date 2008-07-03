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

package eu.secse.deliveryManager.federations.securepubsub.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Lob;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

import eu.secse.deliveryManager.data.FederationExtraInfo;
import eu.secse.deliveryManager.reds.InterestEnvelope;

/**
 * This entity has to be inserted in delivery-directory/META-INF/persistence.xml
 * @author lili
 * @author matteo
 */
@Entity
public class SecPubSubFederationExtraInfo extends FederationExtraInfo{
	
	@Lob
	private InterestEnvelope federationFilter;

	@OneToMany(cascade=CascadeType.ALL)
	@MapKey(name="version")
	private Map<Long, SecPubSubKey> keys;
	
	private long lastKeyVersion;
	
	public SecPubSubFederationExtraInfo() {
		super();		
	}

	public SecPubSubFederationExtraInfo(InterestEnvelope federationFilter) {
		this.federationFilter = federationFilter;
	}

	public InterestEnvelope getFederationFilter() {
		return federationFilter;
	}

	public void setFederationFilter(InterestEnvelope federationFilter) {
		this.federationFilter = federationFilter;
	}

	public void addKey(EntityManager em, Serializable key, long version) {
		if(version > this.lastKeyVersion) this.lastKeyVersion = version;
		
		SecPubSubKey secPubSubKey = new SecPubSubKey(version, key);
		em.persist(secPubSubKey);
		
		keys.put(version, secPubSubKey);
	}
	
	public Serializable getLastKey() {
		return keys.get(this.lastKeyVersion).getKey();
	}

	public Serializable getKey(long version) {
		return keys.get(version).getKey();
	}

	@SuppressWarnings("unchecked")
	public static Collection<SecPubSubFederationExtraInfo> getAll(EntityManager em){
		return em.createQuery("FROM PubSubFederationExtraInfo").getResultList();
	}
}
