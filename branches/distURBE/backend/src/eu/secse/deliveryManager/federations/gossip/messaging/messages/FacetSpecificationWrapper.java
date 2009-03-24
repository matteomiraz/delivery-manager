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

package eu.secse.deliveryManager.federations.gossip.messaging.messages;

import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.model.FacetSpec;

/** Wrapper for transporting specification facets
 * 
 * @author ercasta
 *
 */
public class FacetSpecificationWrapper implements Deliverable {
		
	private static final long serialVersionUID = 4648996216371844605L;

	private String serviceId;
	
	private FacetSpec spec;

	public FacetSpecificationWrapper(FacetSpec info, String serviceid) {
		super();
		spec = info;
		serviceId = serviceid;
	}

	public FacetSpec getSpec() {
		return spec;
	}

	public void setSpec(FacetSpec spec) {
		this.spec = spec;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	
	public String getType() {
		return "GOSSIP";
	}
}
