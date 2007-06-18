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


package eu.secse.deliveryManager;

import javax.ejb.Local;

import eu.secse.deliveryManager.model.DFacetSpecificationSchema;
import eu.secse.deliveryManager.model.DService;


@Local
public interface IModelManager {
	
	/**
	 * Store a service received from a remote registry
	 * This method renew the lease of the received elements if those are known by the delivery manager
	 * @param srv the service received
	 */
	public void storeService(DService srv);

	/**
	 * Store a facet specification xml (wrapped by its facet specification schema) received
	 * from a remote registry.
	 * This method renew the lease of the received elements if those are known by the delivery manager
	 * @param ft the facet instance received
	 */
	public void storeFacet(DFacetSpecificationSchema ft);
}
