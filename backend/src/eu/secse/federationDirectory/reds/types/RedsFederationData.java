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

package eu.secse.federationDirectory.reds.types;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;


public class RedsFederationData implements Serializable {
	private static final long serialVersionUID = -3206666082294386253L;


	private String id;
	

	private String name;
	
	private String method; 
		
	
	private Collection<RedsFederationProperty> properties;
	

	private Date leaseExpiration;

	public RedsFederationData() {
		
	}
	
	public RedsFederationData(String uid) {
		this.id=uid;		
	} 
	
	public Date getLeaseExpiration() {
		return leaseExpiration;
	}

	public void setLeaseExpiration(Date leaseExpiration) {
		this.leaseExpiration = leaseExpiration;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<RedsFederationProperty> getProperties() {
		return properties;
	}

	public void setProperties(
			Collection<RedsFederationProperty> properties) {
		this.properties = properties;
	}
	
	public RedsUniqueID getId() {
		return new RedsUniqueID(id);
	}

	public void setId(RedsUniqueID id) {
		this.id = id.getId();
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
}
