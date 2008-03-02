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

package eu.secse.deliveryManager.federations.pubSubRep.reds;

import eu.secse.deliveryManager.model.Deliverable;

/**
 * 
 * @author matteo
 */
public class PSRDeletion implements Deliverable {
	
	private static final long serialVersionUID = -5601154714597022652L;

	private String serviceId;
	private String facetId;

	public PSRDeletion() { }

	public PSRDeletion(String serviceId) {
		super();
		this.serviceId = serviceId;
		this.facetId = null;
	}

	public PSRDeletion(String facetId, String serviceId) {
		super();
		this.facetId = facetId;
		this.serviceId = serviceId;
	}
	
	public String getServiceId() {
		return serviceId;
	}
	
	public String getFacetId() {
		return facetId;
	}

	public boolean isService() {
		return facetId == null;
	}
	
	public String getType() {
		return "PSR-DELETION";
	}
}
