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


package eu.secse.deliveryManager.registry;

//TODO: non ritornare null ma lanciare una notfoundexc

import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.Local;

import eu.secse.deliveryManager.exceptions.CredentialsNotValidException;
import eu.secse.deliveryManager.exceptions.FacetNotAddedException;
import eu.secse.deliveryManager.exceptions.FacetNotRemovedException;
import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.exceptions.ServiceNotDeployedException;
import eu.secse.deliveryManager.exceptions.ServiceNotUndeployedException;
import eu.secse.deliveryManager.model.DFacetSpecificationSchema;
import eu.secse.deliveryManager.model.DService;

/**
 * Proxy to the SeCSE Registry
 * @author matteo
 */
@Local
public interface IRegistryProxy {

	/**
	 * Deploy the service and all its (not additional information) facetspecification{schema|XML}.
	 * This metod checks also if there are new facets attached to the service or if some of the previous facets are
	 * removed from the service.
	 * @param service
	 * @throws RemoteException
	 * @throws CredentialsNotValidException
	 * @throws ServiceNotDeployedException
	 */
	public abstract void deployService(DService service) throws RemoteException, CredentialsNotValidException, ServiceNotDeployedException;

	/**
	 * Undeploy the specified service, removing also all its facets from the registry
	 * @param serviceId
	 * @throws RemoteException
	 * @throws ServiceNotUndeployedException
	 * @throws CredentialsNotValidException
	 */
	public abstract void undeployService(String serviceId) throws RemoteException, ServiceNotUndeployedException, CredentialsNotValidException;

	/**
	 * Get the service with all its specification facets (not additional information)
	 * @param serviceID
	 * @return
	 * @throws RemoteException
	 * @throws NotFoundException
	 */
	public abstract DService getService(String serviceID) throws RemoteException, NotFoundException;

	/**
	 * Store the additional information facet specification in the registry.
	 * After this invocation, it is guaranteed that in the registry is present exactly the <code>facet<code>,
	 * so check if one or both are already present in the registry, and if the xml is removed from the facet.  
	 * @param facet
	 * @throws RemoteException
	 * @throws FacetSpecificationNotAddedException
	 * @throws CredentialsNotValidException
	 */
	public abstract void storeFacetSpecification(DFacetSpecificationSchema facet) throws RemoteException, FacetNotAddedException, CredentialsNotValidException;

	/**
	 * Remove a facet (additional information) from the registry
	 * @param facetTypeId
	 * @throws RemoteException
	 * @throws FacetNotRemovedException
	 */
	public abstract void removeFacetSpecification(String facetTypeId) throws RemoteException, FacetNotRemovedException;

	/**
	 * Get the set of additional information facets of a given service
	 * @param serviceID
	 * @return
	 * @throws RemoteException
	 * @throws NotFoundException
	 */
	public abstract Collection<DFacetSpecificationSchema> getAdditionalInformationFacets(String serviceID) throws RemoteException, NotFoundException;

	/**
	 * Get a particular additional information facet from the registry
	 * @param serviceID
	 * @return
	 * @throws RemoteException
	 * @throws NotFoundException
	 */
	public abstract DFacetSpecificationSchema getAdditionalInformationFacet(String facetSpecificationSchemaID) throws RemoteException, NotFoundException;

	
	long getServiceProviderId(String serviceID) throws NotFoundException, RemoteException;
	long getFacetSchemaProviderId(String facetSchemaId) throws RemoteException;
	
	/**
	 * Get the registry id of the delivery manager
	 * @param username
	 * @param password
	 * @return
	 * @throws CredentialsNotValidException
	 * @throws RemoteException
	 */
	long getDeliveryManagerId() throws CredentialsNotValidException, RemoteException;
	
	/**
	 * Get the registry unique id
	 * @return the registry unique id
	 */
	public String getRegistryId();
	
	/**
	 * Get the name of a specified service
	 * @param serviceId the id of the service
	 * @return the name of the service
	 * @throws NotFoundException the service is not found
	 */
	public String getServiceName(String serviceId) throws NotFoundException;
	
	/**
	 * Get the provider of a specified service
	 * @param serviceId the id of the service
	 * @return the provider of the service
	 * @throws NotFoundException the service is not found
	 */
	public String getServiceProvider(String serviceId) throws NotFoundException;
	
	/**
	 * Check if a service (identified by newServiceId) is a next version
	 * of a well-known service (identified by baseServiceId).
	 * @param newServiceId the new service id
	 * @param baseServiceId the base service id
	 * @return true if baseService is a previous version of newService 
	 */
	public boolean isANextVersionOfSpecifiedService(String newServiceId, String baseServiceId);
}