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

@Local
public interface IUpdate {

	void addService(String serviceId, long providerId);

	void deleteService(String serviceId, long providerId);

	void addFacetSpecificationSchema(String serviceId, String schemaId, boolean additionalInformation, long providerId);

	void deleteFacetSpecificationSchema(String serviceId, String schemaId, boolean additionalInformation, long providerId);

	void addFacetSpecificationXML(String serviceId, String schemaId, String xmlId, boolean additionalInformation, long providerId);

	void deleteFacetFacetSpecificationXML(String serviceId, String schemaId, String xmlId, boolean additionalInformation, long providerId);
}