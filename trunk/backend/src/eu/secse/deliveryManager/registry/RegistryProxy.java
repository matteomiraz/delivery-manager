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
import it.s2.registry.FacetSpecificationXML;
import it.s2.registry.FacetSpecificationXSD;
import it.s2.registry.SeCSERegistry;
import it.s2.registry.SeCSERegistryServiceLocator;
import it.s2.registry.Service;
import it.s2.registry.ServiceProviderDescription;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.secse.deliveryManager.exceptions.CredentialsNotValidException;
import eu.secse.deliveryManager.exceptions.FacetNotAddedException;
import eu.secse.deliveryManager.exceptions.FacetNotRemovedException;
import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.exceptions.ServiceNotUndeployedException;
import eu.secse.deliveryManager.logger.IPerformanceLogger;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.FacetAddInfo;
import eu.secse.deliveryManager.model.FacetSpec;
import eu.secse.deliveryManager.model.FacetSpecXML;
import eu.secse.deliveryManager.utils.IConfiguration;

/**
* In order to create a SeCSE registry proxy, you should create another
* class that implements the SeCSERegistry interface, and modify the 
* following init method.
* @author matteo
*/
@Stateless
public class RegistryProxy implements IRegistryProxy {

	private static final boolean ENABLE_VALIDATION = false;
	private static final Log log = LogFactory.getLog(IRegistryProxy.class);

	@EJB IConfiguration config;
	
	private SeCSERegistry registry;
	private String registryId;

	private URL registryUrl;
	private String registryUsername; 
	private String registryPassword; 
	private String registryName;

	private long deliveryManagerID; 
	
	@EJB private IPerformanceLogger pLogger; 
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#init()
	 */
	@PostConstruct
	@PostActivate
	public void init() {
		try {
			String str = config.getString("RegistryProxy.url");
			this.registryUrl = new URL(str);
		} catch (Throwable e) {
			try {
				this.registryUrl = new URL("http://localhost:8080/SeCSERegistry/services/SeCSERegistry");
			} catch (MalformedURLException e1) {
				// this never happen
			}
		}
		
		registryUsername = config.getString("RegistryProxy.username");
		registryPassword = config.getString("RegistryProxy.password");
		registryName = config.getString("RegistryProxy.name");
		
		String loggingRegistryString = config.getString("RegistryProxy.logging");
		boolean loggingRegistry = (loggingRegistryString!=null && new Boolean(loggingRegistryString));
		
		try {
			// In order to replace the way the delivery manager contact the 
			// registry, modify the next lines:
			SeCSERegistryServiceLocator srsl = new SeCSERegistryServiceLocator();
			registry = srsl.getSeCSERegistry(registryUrl);
			// END modify
			
			// log all registry invocations
			if(loggingRegistry) registry = new LoggingRegistry(registry);
			
			// register delivery manager
			try {
				this.deliveryManagerID = registry.authenticate(new Credentials(this.registryPassword, this.registryUsername));
			} catch (Throwable e) {
				try {
					log.info("Registering the delivery manager into the registry: name=" + this.registryName + " username=" + this.registryUsername + " password=" + this.registryPassword);
					registry.registerServiceProvider(new ServiceProviderDescription(this.registryName, -1), new Credentials(this.registryPassword, this.registryUsername));
					this.deliveryManagerID = registry.authenticate(new Credentials(this.registryPassword, this.registryUsername));
				} catch (Exception e1) {
					log.fatal("Cannot register the service provider of the delivery manager: " + e1.getMessage() + " due to: " + e1.getCause());
				}
			}

			// get the registry unique id
			registryId = registry.getRegistryId();
		} catch (Exception e) {
	        e.printStackTrace ();
	    }
	}
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#passivate()
	 */
	@PrePassivate
	public void passivate() {
		this.registry = null;
	}

	private Credentials getCredentials() {
		Credentials ret = new Credentials();
		
		ret.setLogin(this.registryUsername);
		ret.setPassword(this.registryPassword);

		return ret;
	}

	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#deployService(eu.secse.deliveryManager.model.DService)
	 */
	public void storeService(DService srv) throws RemoteException {
		log.info("deployService(" + srv + ")");
		
		if(pLogger.isEnabled()) pLogger.log(System.currentTimeMillis(), "REGISTRY_ADD_SERVICE", srv.getServiceID());
		
		Service regSrv = null;
		try {
			regSrv = registry.getService(srv.getServiceID());
		} catch (Throwable exc) {
		}

		if(regSrv == null) {
			log.debug("The registry doesn't have the service id " + srv.getServiceID() + "; publishing that service in the registry");

			// serviceId = <registryID>.<internalID>
			// ids = {registryID, serviceID}
			String[] ids = srv.getServiceID().split("\\.");
			
			regSrv = new Service();
			regSrv.setRegistryId(ids[0]);
			regSrv.setId(ids[1]);
			regSrv.setName(srv.getName());
			regSrv.setAllowAdditionalInformation(srv.isAllowAdditionalInformation());
			regSrv.setPreviousVersionId(srv.getPreviousVersionId());
			regSrv.setVersion(srv.getVersion());
			regSrv.setTimestamp(srv.getTimestamp());
			regSrv.setISOTimestamp(srv.getIsoTimestamp());

			registry.deployService(regSrv, this.getCredentials(), true);
			
		} else {
			log.warn("The service " + srv.getServiceID() + " is already present in the registry.");
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#getService(java.lang.String)
	 */
	public DService getService(String serviceID) throws RemoteException, NotFoundException {
		Service srv = registry.getService(serviceID);
		if (srv==null) throw new NotFoundException("Service with id " + serviceID + " not in registry");
		DService ret = new DService(serviceID, srv.getName(), srv.getVersion(), srv.getPreviousVersionId(), srv.isAllowAdditionalInformation(), srv.getTimestamp(), srv.getISOTimestamp(), null);
		return ret;
	}
	
	public boolean isServiceLocallyCreated(String serviceId) {
		try {
			Service serv = registry.getService(serviceId);
			return serv.getProviderId() != deliveryManagerID;
		} catch (RemoteException e) {
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#undeployService(java.lang.String)
	 */
	public void removeService(String serviceId) throws RemoteException, ServiceNotUndeployedException, CredentialsNotValidException {
		if(pLogger.isEnabled()) pLogger.log(System.currentTimeMillis(), "REGISTRY_DEL_SERVICE", serviceId);

		registry.undeployService(serviceId, getCredentials());
	}

	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#storeFacet(eu.secse.deliveryManager.model.DFacetSpecificationSchema)
	 */
	public void storeFacet(FacetAddInfo facet) throws RemoteException, FacetNotAddedException, CredentialsNotValidException {
		if(pLogger.isEnabled()) pLogger.log(System.currentTimeMillis(), "REGISTRY_ADD_FACET_ADDINFO", facet.getSchemaID());
		storeFacet(facet, facet.getServiceID());
	}

	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#storeFacet(eu.secse.deliveryManager.model.EFacetSpecificationSchema, java.lang.String)
	 */
	public void storeFacet(FacetSpec facet, String serviceId) throws RemoteException, FacetNotAddedException, CredentialsNotValidException {
		log.debug("storeFacetSpecification(" + facet + ", " + serviceId + ")");
		
		// check if the schema is present
		FacetSpecificationXSD f;
		try {
			f = registry.getFacetSpecificationXSDById(facet.getSchemaID());
		} catch (Throwable e) {
			f = null;
		}
	
		// se la facet non è presente, la deploya nel registro
		if(f == null) {
			log.debug("The facet schema " + facet.getSchemaID() + " is not in the registry");
	
			try {
				FacetSpecificationXSD fsXSD = new FacetSpecificationXSD();
				//fsXSD.setAdditionalInformation(true); 
				fsXSD.setAdditionalInformation(facet instanceof FacetAddInfo);
				fsXSD.setDocument(facet.getDocument());
				fsXSD.setFacetSpecificationXSDId(facet.getSchemaID());
				fsXSD.setServiceId(serviceId);
				fsXSD.setName(facet.getName());
				fsXSD.setTimestamp(facet.getTimestamp());
				fsXSD.setISOTimestamp(facet.getIsoTimestamp());
				
				//registry.addFacetSpecificationXSD(serviceId, fsXSD, getCredentials(), true, facet.getTypeName(), true); 
				registry.addFacetSpecificationXSD(serviceId, fsXSD, getCredentials(), facet instanceof FacetAddInfo, facet.getTypeName(), true);
			} catch (Throwable e) {
				log.warn("Cannot store the new received xsd: " + e.getMessage() + " due to: " + e.getCause());
			}
		}
	}
	
	public void storeFacetXml(String serviceId, String facetId, FacetSpecXML xml, String facetType) {
		try {
			FacetSpecificationXML fsXML = new FacetSpecificationXML();
			fsXML.setName(xml.getName());
			fsXML.setDocument(xml.getDocument());
			fsXML.setFacetTypeName(facetType);
			fsXML.setFacetSpecificationXMLId(xml.getXmlID());
			fsXML.setTimestamp(xml.getTimestamp());
			fsXML.setISOTimestamp(xml.getIsoTimestamp());
			registry.addFacetSpecificationXML(serviceId, facetId, fsXML , getCredentials(), ENABLE_VALIDATION, true);
		} catch (Throwable e) {
			log.warn("Cannot store the new received xsd: " + e.getMessage() + " due to: " + e.getCause());
		}		
	}
	
	public void removeFacetXml(String facetXmlID) {
		try {
			log.debug("Removing the facet xml id " + facetXmlID + " from the registry");
			registry.removeFacetSpecificationXML(facetXmlID, getCredentials());
		} catch (Throwable e) {
			log.warn("Cannot remove facet xml with id " + facetXmlID + ": " + e.getMessage() + " due to: " + e.getCause());
		}		
	}
	
	public boolean isFacetLocallyCreated(String facetId) {
		try {
			FacetSpecificationXSD facet = registry.getFacetSpecificationXSDById(facetId);
			return facet.getProviderId() != deliveryManagerID;
		} catch (Throwable e) {
			log.warn("Cannot access to facet schema with id " + facetId + ": " + e.getMessage() + " due to: " + e.getCause());
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#getFacet(java.lang.String)
	 */
	public FacetSpec getFacet(String facetSchemaId) throws RemoteException, NotFoundException {
		FacetSpecificationXSD xsd = registry.getFacetSpecificationXSDById(facetSchemaId);
		
		if (xsd==null) throw new NotFoundException("Could not find facet schema " + facetSchemaId);
		FacetSpecXML exml = null;
		
		try {
			FacetSpecificationXML xml = registry.getFacetSpecificationXMLByFacetSpecificationXSDId(facetSchemaId);
			if (xml==null) {
				log.debug("Could not get xml for schema " + facetSchemaId + ". Maybe no xml is present");				
			} else {
				exml = new FacetSpecXML(xml.getFacetSpecificationXMLId(), xml.getName(), xml.getDocument(), xml.getTimestamp(), xml.getISOTimestamp());
			}
		} catch (Throwable e) {
			log.warn("Caught throwable " + e.getMessage());
		}

		String typeName = registry.getFacetType(facetSchemaId);
		
		FacetSpec ret;
		if(xsd.isAdditionalInformation()) {
			ret = new FacetAddInfo(xsd.getFacetSpecificationXSDId(), xsd.getName(), xsd.getDocument(), typeName, exml, xsd.getServiceId(), null, xsd.getTimestamp(), xsd.getISOTimestamp());
			log.debug("creating new FacetAddInfo");
		}
		else {
			ret = new FacetSpec(xsd.getFacetSpecificationXSDId(), xsd.getName(), xsd.getDocument(), typeName, exml, xsd.getTimestamp(), xsd.getISOTimestamp());
			log.debug("creating new FacetSpec");
		}
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#getSpecificationFacets(java.lang.String)
	 */
	public Collection<FacetSpec> getSpecificationFacets(String serviceID) throws RemoteException, NotFoundException {
		Collection<FacetSpec> ret = new ArrayList<FacetSpec>();
		
		for (FacetSpecificationXSD xsd : registry.getFacetSpecificationXSDs(serviceID, false)) {
			if(xsd == null) continue;
			
			FacetSpecXML exml = null;
			try {
				FacetSpecificationXML xml = registry.getFacetSpecificationXMLByFacetSpecificationXSDId(xsd.getFacetSpecificationXSDId());
				exml = new FacetSpecXML(xml.getFacetSpecificationXMLId(), xml.getName(), xml.getDocument(), xml.getTimestamp(), xml.getISOTimestamp());
			} catch (Throwable e1) {
				// XML non trovato...

			}
			
			String typeName = registry.getFacetType(xsd.getFacetSpecificationXSDId());
			FacetSpec e = new FacetSpec(xsd.getFacetSpecificationXSDId(), xsd.getName(), xsd.getDocument(), typeName, exml, xsd.getTimestamp(), xsd.getISOTimestamp());
		
			ret.add(e);
		}
		
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#getAdditionalInformationFacets(java.lang.String)
	 */
	public Collection<FacetAddInfo> getAdditionalInformationFacets(String serviceID) throws RemoteException, NotFoundException {
		Collection<FacetAddInfo> ret = new ArrayList<FacetAddInfo>();
		
		FacetSpecificationXSD[] facetSpecificationXSDs = registry.getFacetSpecificationXSDs(serviceID, true);
		if(facetSpecificationXSDs == null) facetSpecificationXSDs=new FacetSpecificationXSD[0];
		
		for (FacetSpecificationXSD xsd : facetSpecificationXSDs) {
			if(xsd != null && xsd.isAdditionalInformation()) {
				
				FacetSpecXML exml = null;
				try {
					FacetSpecificationXML xml = registry.getFacetSpecificationXMLByFacetSpecificationXSDId(xsd.getFacetSpecificationXSDId());
					exml = new FacetSpecXML(xml.getFacetSpecificationXMLId(), xml.getName(), xml.getDocument(), xml.getTimestamp(), xml.getISOTimestamp());
				} catch (Throwable e1) {
					// XML non trovato...

				}
				
				String typeName = registry.getFacetType(xsd.getFacetSpecificationXSDId());
				FacetAddInfo e = new FacetAddInfo(xsd.getFacetSpecificationXSDId(), xsd.getName(), xsd.getDocument(), typeName, exml, xsd.getServiceId(), null, xsd.getTimestamp(), xsd.getISOTimestamp());
				ret.add(e);
			}
		}
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#getDeliveryManagerId()
	 */
	public long getDeliveryManagerId() throws CredentialsNotValidException, RemoteException {
		return this.deliveryManagerID;
	}

	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#removeFacetSpecification(java.lang.String)
	 */
	public void removeFacetSpecification(String facetTypeId) throws RemoteException, FacetNotRemovedException {
		if(pLogger.isEnabled()) pLogger.log(System.currentTimeMillis(), "REGISTRY_DEL_FACET", facetTypeId);

		registry.removeFacetSpecification(facetTypeId, getCredentials());		
	}

	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#getRegistryId()
	 */
	public String getRegistryId() {
		return this.registryId;
	}
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#getServiceProviderId(java.lang.String)
	 */
	public long getServiceProviderId(String arg0) throws NotFoundException, RemoteException {
		Service serv = registry.getService(arg0);
		return serv.getProviderId();
	}
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#getFacetSchemaProviderId(java.lang.String)
	 */
	public long getFacetSchemaProviderId(String arg0) throws RemoteException {
		FacetSpecificationXSD facetSchema = registry.getFacetSpecificationXSDById(arg0);
		return facetSchema.getProviderId();
	}

	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#getServiceName(java.lang.String)
	 */
	public String getServiceName(String serviceId) throws NotFoundException {
		try {
			Service srv = registry.getService(serviceId);
			return srv.getName();
		} catch (Throwable e) {
			throw new NotFoundException("The service " + serviceId + " is not found.");
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#isANextVersionOfSpecifiedService(java.lang.String, java.lang.String)
	 */
	public boolean isANextVersionOfSpecifiedService(String newServiceId, String baseServiceId) {
		try {
			Service srv = registry.getService(newServiceId);
			
			while(srv != null && !baseServiceId.equals(srv.getPreviousVersionId())) {
				srv = registry.getService(srv.getPreviousVersionId());
			}
			
			return srv != null && baseServiceId.equals(srv.getPreviousVersionId());
		} catch (Throwable e) {
			return false;
		}
	}
}
