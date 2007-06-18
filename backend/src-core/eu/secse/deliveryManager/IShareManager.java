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

import javax.ejb.Remote;

import eu.secse.deliveryManager.exceptions.NotFoundException;

@Remote
public interface IShareManager {

	/**
	 * This operation deliveries the service identified by the specified id, and
	 * all its specification facets type and instance, to the other interested
	 * registries. It is required that the service is originally deployed in the
	 * local subsystem (you cannot share an information received from another
	 * subsystem).
	 */
	void shareServiceSpecifications(String serviceId) throws NotFoundException;

	/**
	 * This operation deliveries the service identified by the specified id, and
	 * all its additional information facets type and instance, to the other
	 * interested registries.
	 */
	void shareAllServiceAdditionalInformations(String serviceId) throws NotFoundException;

	/**
	 * This operation requires a service id and one of its facet types, which
	 * must be related to a service additional information. As result, the
	 * delivery manager shares the related facet instances with the interested
	 * registries. This method allows a finer-grade selection of the facets you
	 * want to share.
	 */
	void shareServiceAdditionalInformation(String serviceId, String facetSchemaId) throws NotFoundException;
}
