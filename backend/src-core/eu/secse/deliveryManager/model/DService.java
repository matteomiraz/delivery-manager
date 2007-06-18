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


package eu.secse.deliveryManager.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class DService implements Deliverable {

	private static final long serialVersionUID = -5997442809017106152L;

	/** service uniqueID */
	private String serviceID;
	
	/** service name */
	private String name;

	/** versione attuale */
	private String version;
	/** id della versione precedente */
	private String previousVersionId;
	
	/** allows anyone to attach additional information to this service */
	private boolean allowAdditionalInformation;
		
	private String timestamp;
	private String isoTimestamp;
	
	/** lease time */
	private Date lease;
	
	/** The collection of all specification facet types */
	private Collection<EFacetSpecificationSchema> specType;
	
	public DService(String serviceId, String name, String version, String previousVersionId, boolean allowAdditionalInformation, Date lease, String timestamp, String isoTimestamp) {
		this.serviceID = serviceId;
		this.name = name;
		this.version = version;
		this.previousVersionId = previousVersionId;
		this.lease = lease;
		this.allowAdditionalInformation = allowAdditionalInformation;
		this.specType = new ArrayList<EFacetSpecificationSchema>();
		this.timestamp = timestamp;
		this.isoTimestamp = isoTimestamp;
	}

	public void addSpecType(EFacetSpecificationSchema e) {
		this.specType.add(e);
	}
	
	/**
	 * @return the allowAdditionalInformation
	 */
	public boolean isAllowAdditionalInformation() {
		return allowAdditionalInformation;
	}

	/**
	 * @return the lease
	 */
	public Date getLease() {
		return lease;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the previousVersionId
	 */
	public String getPreviousVersionId() {
		return previousVersionId;
	}

	/**
	 * @return the serviceID
	 */
	public String getServiceID() {
		return serviceID;
	}

	/**
	 * @return the specType
	 */
	public Collection<EFacetSpecificationSchema> getSpecType() {
		return specType;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	
	public String getIsoTimestamp() {
		return isoTimestamp;
	}

	@Override
	public String toString() {
		return "{SRV id:" + this.serviceID + ", name:" + this.name + " specType:" + this.specType + "}";
	}

	public void setLease(Date lease) {
		this.lease = lease;
	}
}
