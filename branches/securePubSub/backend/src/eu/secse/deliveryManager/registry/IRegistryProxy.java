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

package eu.secse.deliveryManager.registry;

import java.rmi.RemoteException;
import java.util.Collection;

import eu.secse.deliveryManager.exceptions.CredentialsNotValidException;
import eu.secse.deliveryManager.exceptions.FacetNotAddedException;
import eu.secse.deliveryManager.exceptions.FacetNotRemovedException;
import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.exceptions.ServiceNotUndeployedException;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.FacetAddInfo;
import eu.secse.deliveryManager.model.FacetSpec;
import eu.secse.deliveryManager.model.FacetSpecXML;
import javax.ejb.Local;

@Local
public interface IRegistryProxy {

	/**
	 * Salva il servizio nel registro. Nota: NON salva anche le facet di specifica
	 * @param srv il servizio
	 */
	public abstract void storeService(DService srv) throws RemoteException;

	/**
	 * Recupera il servizio dal registro (senza facet)
	 * @param serviceID l'id del servizio
	 * @return il servizio
	 * @throws NotFoundException se il servizio con l'id dato non è stato trovato
	 */
	public abstract DService getService(String serviceID) throws RemoteException, NotFoundException;

	
	public abstract FacetSpec getFacet(String facetSchemaId) throws RemoteException, NotFoundException;

	public abstract Collection<FacetSpec> getSpecificationFacets(String serviceID) throws RemoteException, NotFoundException;

	public abstract Collection<FacetAddInfo> getAdditionalInformationFacets(
			String serviceID) throws RemoteException, NotFoundException;

	public abstract long getDeliveryManagerId()
			throws CredentialsNotValidException, RemoteException;

	public abstract void removeFacetSpecification(String facetTypeId)
			throws RemoteException, FacetNotRemovedException;

	public abstract void storeFacet(FacetAddInfo facet)
			throws RemoteException, FacetNotAddedException,
			CredentialsNotValidException;

	public abstract void storeFacet(FacetSpec facet,
			String serviceId) throws RemoteException, FacetNotAddedException,
			CredentialsNotValidException;

	public abstract void removeService(String serviceId)
			throws RemoteException, ServiceNotUndeployedException,
			CredentialsNotValidException;

	public abstract String getRegistryId();

	public abstract long getServiceProviderId(String arg0)
			throws NotFoundException, RemoteException;

	public abstract long getFacetSchemaProviderId(String arg0)
			throws RemoteException;

	public abstract String getServiceName(String serviceId)
			throws NotFoundException;

	public abstract boolean isANextVersionOfSpecifiedService(
			String newServiceId, String baseServiceId);

	public boolean isServiceLocallyCreated(String serviceId);
	public boolean isFacetLocallyCreated(String facetId);
	
	public void storeFacetXml(String serviceId, String facetId, FacetSpecXML xml, String facetType);
	public void removeFacetXml(String facetXmlID);
}