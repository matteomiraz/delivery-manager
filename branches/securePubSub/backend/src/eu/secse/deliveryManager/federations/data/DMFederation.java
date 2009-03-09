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

package eu.secse.deliveryManager.federations.data;

import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/** This entity is used as a cache for existing Federations, fetched from the directory or directly inserted
 * from the deliverymanager. Owned federations are never deleted from this cache, because they have
 * to be periodically renewed.
 * 
 * @author ercasta
 *
 */
@Entity
@Table(name="DMFederation")
@NamedQueries(value={
		@NamedQuery(name=DMFederation.getall,query="from DMFederation"),
		@NamedQuery(name=DMFederation.expired, query="from DMFederation where lease < current_timestamp()"  ),
		@NamedQuery(name=DMFederation.like, query="from DMFederation where name like :name"),
		@NamedQuery(name=DMFederation.owned, query="from DMFederation where ownership = true"),
		@NamedQuery(name=DMFederation.acquired, query="from DMFederation where ownership = false"),
		@NamedQuery(name=DMFederation.torenew, query="from DMFederation where renew < current_timestamp() and ownership = true"),
		@NamedQuery(name=DMFederation.byname, query="from DMFederation where :name = name "),
		@NamedQuery(name=DMFederation.byUID, query = "from DMFederation where id = :id")
		}
)
        
public class DMFederation {

	public static final String getall="GETALL";
	public static final String expired="EXPIRED";
	public static final String like="LIKE";
	public static final String owned="OWNED";
	public static final String byUID = "UID";
	//Acquired federations are federations which are not owned 
	public static final String acquired="ACQUIRED";
	public static final String torenew="RENEW";
	public static final String byname="get_existing_federation_by_name";
	
	
	//Represents how many hours before the actual expiration a federation is considered  "expiring" 
	public static final int hours_before_expiring=5;
	
	@Id
	private String id;
	
	@Column(name="name")
	private String name;
	
	private String method; 
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date lease;
	
	//If the federation is owned, indicates the moment when the delivery manger should renew the lease
	@Temporal(TemporalType.TIMESTAMP)
	private Date renew;
	
	@OneToMany(cascade=CascadeType.ALL)
	private Collection<DMFederationProperty> properties;
		
	private boolean ownership;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
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

	public Collection<DMFederationProperty> getProperties() {
		return properties;
	}

	public void setProperties(
			Collection<DMFederationProperty> properties) {
		this.properties = properties;
	}

	public Date getLease() {
		return lease;
	}

	public void setLease(Date lease) {
		this.lease = lease;
	}

	public Date getRenew() {
		return renew;
	}

	public void setRenew(Date renew) {
		this.renew = renew;
	}
	
	public boolean equals(Object o1) {
		if (o1==null || o1.getClass()!=getClass()) {
			return false;
		}
		DMFederation fed=(DMFederation)o1;
		if (id.equals(fed.id)) return true;
		return false;
	}
}
