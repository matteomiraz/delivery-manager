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


package eu.secse.deliveryManager.integration;

import javax.jws.WebMethod;
import javax.jws.WebParam;


public interface IUpdateWS {

	@WebMethod
	public void addFacetSpecificationSchema(@WebParam(name="serviceId") String serviceId, @WebParam(name="schemaId") String schemaId, @WebParam(name="isAdditionalInformation") boolean isAdditionalInformation, @WebParam(name="providerId") long providerId);

	@WebMethod
	public void addFacetSpecificationXML(@WebParam(name="serviceId") String serviceId, @WebParam(name="schemaId") String schemaId, @WebParam(name="xmlId") String xmlId, @WebParam(name="isAdditionalInformation") boolean isAdditionalInformation, @WebParam(name="providerId") long providerId);

	@WebMethod
	public void addService(@WebParam(name="serviceId") String serviceId, @WebParam(name="providerId") long providerId);

	@WebMethod
	public void deleteFacetFacetSpecificationXML(@WebParam(name="serviceId") String serviceId, @WebParam(name="schemaId") String schemaId, @WebParam(name="xmlId") String xmlId, @WebParam(name="wasAdditionalInformation") boolean wasAdditionalInformation, @WebParam(name="providerId") long providerId);

	@WebMethod
	public void deleteFacetSpecificationSchema(@WebParam(name="serviceId") String serviceId, @WebParam(name="schemaId") String schemaId, @WebParam(name="wasAdditionalInformation") boolean wasAdditionalInformation, @WebParam(name="providerId") long providerId);

	@WebMethod
	public void deleteService(@WebParam(name="serviceId") String serviceId, @WebParam(name="providerId") long providerId);
}