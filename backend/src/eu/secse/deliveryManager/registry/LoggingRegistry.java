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

import it.s2.registry.Credentials;
import it.s2.registry.CredentialsNotValidException1;
import it.s2.registry.FacetSpecificationNotRemovedException1;
import it.s2.registry.FacetSpecificationXML;
import it.s2.registry.FacetSpecificationXMLNotAddedException1;
import it.s2.registry.FacetSpecificationXMLNotRemovedException1;
import it.s2.registry.FacetSpecificationXSD;
import it.s2.registry.FacetSpecificationXSDNotAddedException1;
import it.s2.registry.LoginAlreadyExistingException1;
import it.s2.registry.QueryErrorException1;
import it.s2.registry.QueryNotAllowedException1;
import it.s2.registry.SeCSERegistry;
import it.s2.registry.Service;
import it.s2.registry.ServiceNotDeployedException1;
import it.s2.registry.ServiceNotUndeployedException1;
import it.s2.registry.ServiceProviderDescription;
import it.s2.registry.ServiceProviderNotFoundException1;
import it.s2.registry.ServiceProviderNotRegisteredException1;
import it.s2.registry.ServiceProviderNotUnregisteredException1;

import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LoggingRegistry implements SeCSERegistry {
	private SeCSERegistry realRegistry;
	
	private static final Log log = LogFactory.getLog(LoggingRegistry.class);

	public LoggingRegistry(SeCSERegistry registry) {
		this.realRegistry = registry;
	}

	public String addFacetSpecificationXML(String serviceId, String facetSpecificationXSDId, FacetSpecificationXML fsXML, Credentials credentials, boolean enableValidation, boolean forceId) throws RemoteException, FacetSpecificationXMLNotAddedException1 {
		try {
			log.debug("Calling addFacetSpecificationXML(" + serviceId + ", " + facetSpecificationXSDId + ", " + Utils.convert(fsXML) + ", " + Utils.convert(credentials) + ", " + enableValidation + ", " + forceId + ")");
			String ret = realRegistry.addFacetSpecificationXML(serviceId, facetSpecificationXSDId, fsXML, credentials, enableValidation, forceId);
			log.debug("Called addFacetSpecificationXML: " + ret);
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling addFacetSpecificationXML: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			if (e instanceof FacetSpecificationXMLNotAddedException1) throw (FacetSpecificationXMLNotAddedException1) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public String addFacetSpecificationXSD(String serviceId, FacetSpecificationXSD fsXSD, Credentials credentials, boolean additionalInformation, String facetType, boolean forceId) throws RemoteException, CredentialsNotValidException1, FacetSpecificationXSDNotAddedException1 {
		try {
			log.debug("Calling addFacetSpecificationXSD(" + serviceId + ", " + Utils.convert(fsXSD) + ", " + Utils.convert(credentials) + ", " + additionalInformation + ", " + facetType + ", " + forceId + ")");
			String ret = realRegistry.addFacetSpecificationXSD(serviceId, fsXSD, credentials, additionalInformation, facetType, forceId);
			log.debug("Called addFacetSpecificationXSD: " + ret);
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling addFacetSpecificationXSD: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			if (e instanceof CredentialsNotValidException1) throw (CredentialsNotValidException1) e;
			if (e instanceof FacetSpecificationXSDNotAddedException1) throw (FacetSpecificationXSDNotAddedException1) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public String addWellKnownFacetSpecificationXML(String serviceId, String wellKnownFacetSpecificationXSD, FacetSpecificationXML fsXML, Credentials credentials, boolean enableValidation, boolean forceId, String forcedXSDId, boolean additionalInformation) throws RemoteException, FacetSpecificationXMLNotAddedException1 {
		try {
			log.debug("Calling addWellKnownFacetSpecificationXML(serviceId, wellKnownFacetSpecificationXSD, fsXML, credentials, enableValidation, forceId, forcedXSDId, additionalInformation)");
			String ret = realRegistry.addWellKnownFacetSpecificationXML(serviceId, wellKnownFacetSpecificationXSD, fsXML, credentials, enableValidation, forceId, forcedXSDId, additionalInformation);
			log.debug("Called addWellKnownFacetSpecificationXML: " + ret);
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling addWellKnownFacetSpecificationXML: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			if (e instanceof FacetSpecificationXMLNotAddedException1 ) throw (FacetSpecificationXMLNotAddedException1) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public long authenticate(Credentials credentials) throws RemoteException, CredentialsNotValidException1 {
		try {
			log.debug("Calling authenticate(" + Utils.convert(credentials) + ")");
			long ret = realRegistry.authenticate(credentials);
			log.debug("Called authenticate: " + ret);
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling authenticate: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			if (e instanceof CredentialsNotValidException1) throw (CredentialsNotValidException1) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public String deployService(Service sd, Credentials credentials, boolean forceId) throws RemoteException, CredentialsNotValidException1, ServiceNotDeployedException1 {
		try {
			log.debug("Calling deployService(" + Utils.convert(sd) + ", " + Utils.convert(credentials) + ", " + forceId + ")");
			String ret = realRegistry.deployService(sd, credentials, forceId);
			log.debug("Called deployService: " + ret);
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling deployService: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			if (e instanceof CredentialsNotValidException1) throw (CredentialsNotValidException1) e;
			if (e instanceof ServiceNotDeployedException1) throw (ServiceNotDeployedException1) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public String[] getAdditionalInformation(long providerId) throws RemoteException {
		try {
			log.debug("Calling getAdditionalInformation(" + providerId + ")");
			String[] ret = realRegistry.getAdditionalInformation(providerId);
			log.debug("Called getAdditionalInformation: " + Utils.convert(ret));
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling getAdditionalInformation: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public FacetSpecificationXSD[] getAllFacetSpecificationXSDs(String[] serviceIds, boolean includeAdditionalInformation) throws RemoteException {
		try {
			log.debug("Calling getAllFacetSpecificationXSDs(" + Utils.convert(serviceIds) + ", " + includeAdditionalInformation + ")");
			FacetSpecificationXSD[] ret = realRegistry.getAllFacetSpecificationXSDs(serviceIds, includeAdditionalInformation);
			log.debug("Called getAllFacetSpecificationXSDs: " + Utils.convert(ret));
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling getAllFacetSpecificationXSDs: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public String[] getAllFacetTypes(String[] serviceIds) throws RemoteException {
		try {
			log.debug("Calling getAllFacetTypes(" + Utils.convert(serviceIds) + ")");
			String[] ret = realRegistry.getAllFacetTypes(serviceIds);
			log.debug("Called getAllFacetTypes: " + Utils.convert(ret));
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling getAllFacetTypes: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public String[] getAllServiceIds() throws RemoteException {
		try {
			log.debug("Calling getAllServiceIds()");
			String[] ret = realRegistry.getAllServiceIds();
			log.debug("Called getAllServiceIds: " + Utils.convert(ret));
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling getAllServiceIds: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public FacetSpecificationXSD[] getCommonFacetSpecificationXSDs(String[] serviceIds, boolean includeAdditionalInformation) throws RemoteException {
		try {
			log.debug("Calling getCommonFacetSpecificationXSDs(" + Utils.convert(serviceIds) + ", " + includeAdditionalInformation + ")");
			FacetSpecificationXSD[] ret = realRegistry.getCommonFacetSpecificationXSDs(serviceIds, includeAdditionalInformation);
			log.debug("Called getCommonFacetSpecificationXSDs: " + Utils.convert(ret));
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling getCommonFacetSpecificationXSDs: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public String[] getCommonFacetTypes(String[] serviceIds) throws RemoteException {
		try {
			log.debug("Calling getCommonFacetTypes(" + Utils.convert(serviceIds) + ")");
			String[] ret = realRegistry.getCommonFacetTypes(serviceIds);
			log.debug("Called getCommonFacetTypes: " + Utils.convert(ret));
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling getCommonFacetTypes: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public FacetSpecificationXML getFacetSpecificationXMLByFacetSpecificationXSDId(String facetSpecificationXSDId) throws RemoteException {
		try {
			log.debug("Calling getFacetSpecificationXMLByFacetSpecificationXSDId(" + facetSpecificationXSDId + ")");
			FacetSpecificationXML ret = realRegistry.getFacetSpecificationXMLByFacetSpecificationXSDId(facetSpecificationXSDId);
			log.debug("Called getFacetSpecificationXMLByFacetSpecificationXSDId: " + Utils.convert(ret));
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling getFacetSpecificationXMLByFacetSpecificationXSDId: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public FacetSpecificationXML getFacetSpecificationXMLById(String facetSpecificationXMLId) throws RemoteException {
		try {
			log.debug("Calling getFacetSpecificationXMLById(" + facetSpecificationXMLId + ")");
			FacetSpecificationXML ret = realRegistry.getFacetSpecificationXMLById(facetSpecificationXMLId);
			log.debug("Called getFacetSpecificationXMLById: " + Utils.convert(ret));
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling getFacetSpecificationXMLById: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public FacetSpecificationXML[] getFacetSpecificationXMLByName(String serviceId, String XMLNameRegExp) throws RemoteException {
		try {
			log.debug("Calling getFacetSpecificationXMLByName(" + serviceId + ", " + XMLNameRegExp + ")");
			FacetSpecificationXML[] ret = realRegistry.getFacetSpecificationXMLByName(serviceId, XMLNameRegExp);
			log.debug("Called getFacetSpecificationXMLByName: " + Utils.convert(ret));
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling getFacetSpecificationXMLByName: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public String getFacetSpecificationXMLIdByFacetSpecificationXSDId(String facetSpecificationXSDId) throws RemoteException {
		try {
			log.debug("Calling getFacetSpecificationXMLIdByFacetSpecificationXSDId(" + facetSpecificationXSDId + ")");
			String ret = realRegistry.getFacetSpecificationXMLIdByFacetSpecificationXSDId(facetSpecificationXSDId);
			log.debug("Called getFacetSpecificationXMLIdByFacetSpecificationXSDId: " + ret);
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling getFacetSpecificationXMLIdByFacetSpecificationXSDId: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public String[] getFacetSpecificationXMLIdsByServiceId(String serviceId, boolean includeAdditionalInformation) throws RemoteException {
		try {
			log.debug("Calling getFacetSpecificationXMLIdsByServiceId(" + serviceId + ", " + includeAdditionalInformation + ")");
			String[] ret = realRegistry.getFacetSpecificationXMLIdsByServiceId(serviceId, includeAdditionalInformation);
			log.debug("Called getFacetSpecificationXMLIdsByServiceId: " + Utils.convert(ret));
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling getFacetSpecificationXMLIdsByServiceId: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public FacetSpecificationXML[] getFacetSpecificationXMLsByServiceId(String serviceId, boolean includeAdditionalInformation) throws RemoteException {
		try {
			log.debug("Calling getFacetSpecificationXMLsByServiceId(" + serviceId + ", " + includeAdditionalInformation + ")");
			FacetSpecificationXML[] ret = realRegistry.getFacetSpecificationXMLsByServiceId(serviceId, includeAdditionalInformation);
			log.debug("Called getFacetSpecificationXMLsByServiceId: " + Utils.convert(ret));
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling getFacetSpecificationXMLsByServiceId: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public FacetSpecificationXSD getFacetSpecificationXSDById(String facetSpecificationXSDId) throws RemoteException {
		try {
			log.debug("Calling getFacetSpecificationXSDById(" + facetSpecificationXSDId + ")");
			FacetSpecificationXSD ret = realRegistry.getFacetSpecificationXSDById(facetSpecificationXSDId);
			log.debug("Called getFacetSpecificationXSDById: " + Utils.convert(ret));
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling getFacetSpecificationXSDById: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public FacetSpecificationXSD[] getFacetSpecificationXSDByName(String serviceId, String XSDNameRegExp) throws RemoteException {
		try {
			log.debug("Calling getFacetSpecificationXSDByName(" + serviceId + ", " + XSDNameRegExp + ")");
			FacetSpecificationXSD[] ret = realRegistry.getFacetSpecificationXSDByName(serviceId, XSDNameRegExp);
			log.debug("Called getFacetSpecificationXSDByName: " + Utils.convert(ret));
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling getFacetSpecificationXSDByName: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public String[] getFacetSpecificationXSDIdsByServiceId(String serviceId) throws RemoteException {
		try {
			log.debug("Calling getFacetSpecificationXSDIdsByServiceId(" + serviceId + ")");
			String[] ret = realRegistry.getFacetSpecificationXSDIdsByServiceId(serviceId);
			log.debug("Called getFacetSpecificationXSDIdsByServiceId: " + Utils.convert(ret));
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling getFacetSpecificationXSDIdsByServiceId: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public FacetSpecificationXSD[] getFacetSpecificationXSDs(String serviceId, boolean includeAdditionalInformation) throws RemoteException {
		try {
			log.debug("Calling getFacetSpecificationXSDs(" + serviceId + ", " + includeAdditionalInformation + ")");
			FacetSpecificationXSD[] ret = realRegistry.getFacetSpecificationXSDs(serviceId, includeAdditionalInformation);
			log.debug("Called getFacetSpecificationXSDs: " + Utils.convert(ret));
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling getFacetSpecificationXSDs: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}


	public String getFacetType(String facetSpecificationXSDId) throws RemoteException {
		try {
			log.debug("Calling getFacetType(" + facetSpecificationXSDId + ")");
			String ret = realRegistry.getFacetType(facetSpecificationXSDId);
			log.debug("Called getFacetType: " + ret);
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling getFacetType: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public String[] getFacetTypeNamesByServiceId(String serviceId) throws RemoteException {
		try {
			log.debug("Calling getFacetTypeNamesByServiceId(" + serviceId + ")");
			String[] ret = realRegistry.getFacetTypeNamesByServiceId(serviceId);
			log.debug("Called getFacetTypeNamesByServiceId: " + Utils.convert(ret));
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling getFacetTypeNamesByServiceId: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public String getRegistryId() throws RemoteException {
		try {
			log.debug("Calling getRegistryId()");
			String ret = realRegistry.getRegistryId();
			log.debug("Called getRegistryId: " + ret);
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling getRegistryId: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public Service getService(String serviceId) throws RemoteException {
		try {
			log.debug("Calling getService(" + serviceId + ")");
			Service ret = realRegistry.getService(serviceId);
			log.debug("Called getService: " + Utils.convert(ret));
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling getService: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public String[] getServiceIdsByFacetSpecifications(FacetSpecificationXSD[] fsXSD) throws RemoteException {
		try {
			log.debug("Calling getServiceIdsByFacetSpecifications(" + Utils.convert(fsXSD) + ")");
			String[] ret = realRegistry.getServiceIdsByFacetSpecifications(fsXSD);
			log.debug("Called getServiceIdsByFacetSpecifications: " + Utils.convert(ret));
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling getServiceIdsByFacetSpecifications: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public String[] getServiceIdsByFacetTypeName(String facetTypeNameRegExp) throws RemoteException {
		try {
			log.debug("Calling getServiceIdsByFacetTypeName(" + facetTypeNameRegExp + ")");
			String[] ret = realRegistry.getServiceIdsByFacetTypeName(facetTypeNameRegExp);
			log.debug("Called getServiceIdsByFacetTypeName: " + Utils.convert(ret));
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling getServiceIdsByFacetTypeName: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public String[] getServiceIdsByName(String serviceNameRegExp) throws RemoteException {
		try {
			log.debug("Calling getServiceIdsByName(" + serviceNameRegExp + ")");
			String[] ret = realRegistry.getServiceIdsByName(serviceNameRegExp);
			log.debug("Called getServiceIdsByName: " + Utils.convert(ret));
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling getServiceIdsByName: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public String[] getServiceIdsByProvider(long providerId) throws RemoteException {
		try {
			log.debug("Calling getServiceIdsByProvider(" + providerId + ")");
			String[] ret = realRegistry.getServiceIdsByProvider(providerId);
			log.debug("Called getServiceIdsByProvider: " + Utils.convert(ret));
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling getServiceIdsByProvider: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public ServiceProviderDescription getServiceProviderDescriptionById(long id) throws RemoteException, ServiceProviderNotFoundException1 {
		try {
			log.debug("Calling getServiceProviderDescriptionById(" + id + ")");
			ServiceProviderDescription ret = realRegistry.getServiceProviderDescriptionById(id);
			log.debug("Called Calling getServiceProviderDescriptionById: " + Utils.convert(ret));
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling Calling getServiceProviderDescriptionById: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			if (e instanceof ServiceProviderNotFoundException1) throw (ServiceProviderNotFoundException1) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public long[] getServiceProviderDescriptionByName(String nameRegExp) throws RemoteException, ServiceProviderNotFoundException1 {
		try {
			log.debug("Calling getServiceProviderDescriptionByName(" + nameRegExp + ")");
			long[] ret = realRegistry.getServiceProviderDescriptionByName(nameRegExp);
			log.debug("Called getServiceProviderDescriptionByName: " + Utils.convert(ret));
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling getServiceProviderDescriptionByName: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			if (e instanceof ServiceProviderNotFoundException1) throw (ServiceProviderNotFoundException1) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public boolean hasFacetSpecificationXSD(String serviceId, FacetSpecificationXSD fsXSD) throws RemoteException {
		try {
			log.debug("Calling hasFacetSpecificationXSD(" + serviceId + ", " + Utils.convert(fsXSD) + ")");
			boolean ret = realRegistry.hasFacetSpecificationXSD(serviceId, fsXSD);
			log.debug("Called hasFacetSpecificationXSD: " + ret);
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling hasFacetSpecificationXSD: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public boolean hasFacetType(String serviceId, String facetTypeName) throws RemoteException {
		try {
			log.debug("Calling hasFacetType(" + serviceId + ", " + facetTypeName + ")");
			boolean ret = realRegistry.hasFacetType(serviceId, facetTypeName);
			log.debug("Called hasFacetType: " + ret);
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling hasFacetType: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public String[] listWellKnownFacetSpecificationXSDs() throws RemoteException {
		try {
			log.debug("Calling listWellKnownFacetSpecificationXSDs()");
			String[] ret = realRegistry.listWellKnownFacetSpecificationXSDs();
			log.debug("Called listWellKnownFacetSpecificationXSDs: " + Utils.convert(ret));
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling listWellKnownFacetSpecificationXSDs: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public String[] listWellKnownFacetTypes() throws RemoteException {
		try {
			log.debug("Calling listWellKnownFacetTypes()");
			String[] ret = realRegistry.listWellKnownFacetTypes();
			log.debug("Called listWellKnownFacetTypes: " + Utils.convert(ret));
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling listWellKnownFacetTypes: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public String[] query(String query) throws RemoteException, QueryErrorException1, QueryNotAllowedException1 {
		try {
			log.debug("Calling query(" + query + ")");
			String[] ret = realRegistry.query(query);
			log.debug("Called query: " + Utils.convert(ret));
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling query: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			if (e instanceof QueryErrorException1) throw (QueryErrorException1) e;
			if (e instanceof QueryNotAllowedException1) throw (QueryNotAllowedException1) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public long registerServiceProvider(ServiceProviderDescription spd, Credentials credentials) throws RemoteException, ServiceProviderNotRegisteredException1, LoginAlreadyExistingException1 {
		try {
			log.debug("Calling registerServiceProvider(" + Utils.convert(spd) + ", " + Utils.convert(credentials) + ")");
			long ret = realRegistry.registerServiceProvider(spd, credentials);
			log.debug("Called registerServiceProvider: " + ret);
			return ret;
		} catch (Throwable e) {
			log.debug("Exception while calling registerServiceProvider: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			if (e instanceof ServiceProviderNotRegisteredException1) throw (ServiceProviderNotRegisteredException1) e;
			if (e instanceof LoginAlreadyExistingException1) throw (LoginAlreadyExistingException1) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public void removeFacetSpecification(String facetSpecificationXSDId, Credentials credentials) throws RemoteException, FacetSpecificationNotRemovedException1 {
		try {
			log.debug("Calling removeFacetSpecification(" + facetSpecificationXSDId + ", " + Utils.convert(credentials) + ")");
			realRegistry.removeFacetSpecification(facetSpecificationXSDId, credentials);
			log.debug("Called removeFacetSpecification.");
		} catch (Throwable e) {
			log.debug("Exception while calling removeFacetSpecification: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			if (e instanceof FacetSpecificationNotRemovedException1) throw (FacetSpecificationNotRemovedException1) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public void removeFacetSpecificationXML(String facetSpecificationXMLId, Credentials credentials) throws RemoteException, CredentialsNotValidException1, FacetSpecificationXMLNotRemovedException1 {
		try {
			log.debug("Calling removeFacetSpecificationXML(" + facetSpecificationXMLId + ", " + Utils.convert(credentials) + ")");
			realRegistry.removeFacetSpecificationXML(facetSpecificationXMLId, credentials);
			log.debug("Called removeFacetSpecificationXML.");
		} catch (Throwable e) {
			log.debug("Exception while calling removeFacetSpecificationXML: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			if (e instanceof FacetSpecificationXMLNotRemovedException1) throw (FacetSpecificationXMLNotRemovedException1) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public void removeFacetSpecificationXMLByFacetSpecificationXSDId(String facetSpecificationXSDId, Credentials credentials) throws RemoteException, CredentialsNotValidException1, FacetSpecificationXMLNotRemovedException1 {
		try {
			log.debug("Calling removeFacetSpecificationXMLByFacetSpecificationXSDId(" + facetSpecificationXSDId + ", " + Utils.convert(credentials) + ")");
			realRegistry.removeFacetSpecificationXMLByFacetSpecificationXSDId(facetSpecificationXSDId, credentials);
			log.debug("Called removeFacetSpecificationXMLByFacetSpecificationXSDId.");
		} catch (Throwable e) {
			log.debug("Exception while calling removeFacetSpecificationXMLByFacetSpecificationXSDId: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			if (e instanceof CredentialsNotValidException1) throw (CredentialsNotValidException1) e;
			if (e instanceof FacetSpecificationXMLNotRemovedException1) throw (FacetSpecificationXMLNotRemovedException1) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public void undeployService(String serviceId, Credentials credentials) throws RemoteException, ServiceNotUndeployedException1, CredentialsNotValidException1 {
		try {
			log.debug("Calling undeployService(" + serviceId + ", " + Utils.convert(credentials) + ")");
			realRegistry.undeployService(serviceId, credentials);
			log.debug("Called undeployService.");
		} catch (Throwable e) {
			log.debug("Exception while calling undeployService: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			if (e instanceof ServiceNotUndeployedException1) throw (ServiceNotUndeployedException1) e;
			if (e instanceof CredentialsNotValidException1) throw (CredentialsNotValidException1) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}

	public void unregisterServiceProvider(Credentials credentials) throws RemoteException, ServiceProviderNotUnregisteredException1 {
		try {
			log.debug("Calling unregisterServiceProvider(" + Utils.convert(credentials) + ")");
			realRegistry.unregisterServiceProvider(credentials);
			log.debug("Called unregisterServiceProvider.");
		} catch (Throwable e) {
			log.debug("Exception while calling unregisterServiceProvider: " + e.getMessage());
			if (e instanceof RemoteException) throw (RemoteException) e;
			if (e instanceof ServiceProviderNotUnregisteredException1) throw (ServiceProviderNotUnregisteredException1) e;
			throw new RemoteException("Exception in the registry", e);
		}
	}
}