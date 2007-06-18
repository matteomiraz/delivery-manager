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


package eu.secse.deliveryManager.notify;

import eu.secse.deliveryManager.notify.data.NotificationInterest;

public class Interest {
	
	private int id;
	
	private String providerName;
	private String serviceId;
	private String baseServiceId;
	private String serviceNameRegex;
	
	private boolean notifyService;
	private boolean notifyFacetSpecificationSchema;
	private boolean notifyFacetSpecificationXml;
	
	public Interest() { }

	public Interest(NotificationInterest interest) {
		this.id = interest.getId();
		
		this.providerName = interest.getProviderName();
		this.serviceId = interest.getServiceId();
		this.baseServiceId = interest.getBaseServiceId();
		this.serviceNameRegex = interest.getServiceNameRegex();
		
		this.notifyService = interest.isNotifyService();
		this.notifyFacetSpecificationSchema = interest.isNotifyFacetSpecificationSchema();
		this.notifyFacetSpecificationXml = interest.isNotifyFacetSpecificationXml();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isNotifyFacetSpecificationSchema() {
		return notifyFacetSpecificationSchema;
	}

	public void setNotifyFacetSpecificationSchema(
			boolean notifyFacetSpecificationSchema) {
		this.notifyFacetSpecificationSchema = notifyFacetSpecificationSchema;
	}

	public boolean isNotifyFacetSpecificationXml() {
		return notifyFacetSpecificationXml;
	}

	public void setNotifyFacetSpecificationXml(boolean notifyFacetSpecificationXml) {
		this.notifyFacetSpecificationXml = notifyFacetSpecificationXml;
	}

	public boolean isNotifyService() {
		return notifyService;
	}

	public void setNotifyService(boolean notifyService) {
		this.notifyService = notifyService;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceNameRegex() {
		return serviceNameRegex;
	}

	public void setServiceNameRegex(String serviceNameRegex) {
		this.serviceNameRegex = serviceNameRegex;
	}

	public String getBaseServiceId() {
		return baseServiceId;
	}
	
	public void setBaseServiceId(String baseServiceId) {
		this.baseServiceId = baseServiceId;
	}
}
