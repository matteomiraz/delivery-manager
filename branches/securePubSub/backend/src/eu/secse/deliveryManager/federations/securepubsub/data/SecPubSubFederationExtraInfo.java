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
import java.security.Key;
import java.security.cert.X509Certificate;
import java.util.HashMap;

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
        
        @Lob
        private X509Certificate federationCertificate;
	
	public SecPubSubFederationExtraInfo() {
		super();	    
                keys = new HashMap();
                lastKeyVersion = 0;
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

	public void addKey(EntityManager em, Key key, long version) {
		if(version > this.lastKeyVersion) this.lastKeyVersion = version;
		
		SecPubSubKey secPubSubKey = new SecPubSubKey(version, key);
		em.persist(secPubSubKey);
		
		keys.put(version, secPubSubKey);
	}
	
	public Key getLastKey() {
		return keys.get(this.lastKeyVersion).getKey();
	}

	public Key getKey(long version) {
		return keys.get(version).getKey();
	}
        
        public void setCertificate(X509Certificate federationCertificate){
            this.federationCertificate = federationCertificate;
        }
        
        public X509Certificate getCertificate(){
            return federationCertificate;
        }
        
        public long getLastKeyVersion(){
            return lastKeyVersion;
        }

	@SuppressWarnings("unchecked")
	public static Collection<SecPubSubFederationExtraInfo> getAll(EntityManager em){
		return em.createQuery("FROM PubSubFederationExtraInfo").getResultList();
	}
}
