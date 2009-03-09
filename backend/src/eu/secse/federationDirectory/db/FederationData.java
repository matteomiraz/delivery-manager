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

package eu.secse.federationDirectory.db;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

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

@Entity
@Table(name="FederationData")
@NamedQueries(value={
		@NamedQuery(name=FederationData.getall,query="from FederationData"),
		@NamedQuery(name=FederationData.expired, query="from FederationData where lease < current_timestamp()" ),
		@NamedQuery(name=FederationData.like, query="from FederationData where name like :name")
		}
)
public class FederationData implements Serializable{
	private static final long serialVersionUID = -2366324247976962355L;

	public static final String getall="DIRECTORY_GETALL";
	public static final String expired="DIRECTORY_EXPIRED";
	public static final String like="DIRECTORY_LIKE";
	
	@Id
	private String id;
	
	@Column(name="name")
	private String name;
	
	private String method; 
		
	@OneToMany(cascade=CascadeType.ALL)
	private Collection<FederationProperty> properties;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="lease")
	private Date leaseExpiration;

	public FederationData() {
		
	}
	
	public FederationData(String uid) {
		this.id=uid;		
	} 
	
	public long getLeaseExpiration() {
		return leaseExpiration.getTime();
	}

	public void setLeaseExpiration(long leaseExpiration) {
		GregorianCalendar gc=new GregorianCalendar();
		gc.setTimeInMillis(leaseExpiration);		
		this.leaseExpiration = gc.getTime();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<FederationProperty> getProperties() {
		return properties;
	}

	public void setProperties(
			Collection<FederationProperty> properties) {
		this.properties = properties;
	}
	
	public UniqueID getId() {
		return new UniqueID(id);
	}

	public void setId(UniqueID id) {
		this.id = id.getId();
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	
}
