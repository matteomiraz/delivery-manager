package eu.secse.deliveryManager.registry;

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

import dire.registry.IRegistry;
import dire.registry.ws.FacetWS;
import dire.registry.ws.ServiceWS;
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

	private static final Log log = LogFactory.getLog(IRegistryProxy.class);

	@EJB IConfiguration config;
	@EJB IRegistry registry;
	
	private String registryId;

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
		registryUsername = config.getString("RegistryProxy.username");
		registryPassword = config.getString("RegistryProxy.password");
		registryName = config.getString("RegistryProxy.name");

		try {
			// register delivery manager
			try {
				this.deliveryManagerID = registry.authenticate(this.registryUsername, this.registryPassword);
			} catch (Throwable e) {
				try {
					log.info("Registering the delivery manager into the registry: name=" + this.registryName + " username=" + this.registryUsername + " password=" + this.registryPassword);
					this.deliveryManagerID = registry.registerServiceProvider(this.registryUsername, this.registryPassword, this.registryName);
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

	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#deployService(eu.secse.deliveryManager.model.DService)
	 */
	public void storeService(DService srv) throws RemoteException {		log.info("deployService(" + srv + ")");
		
		if(pLogger.isEnabled()) pLogger.log(System.currentTimeMillis(), "REGISTRY_ADD_SERVICE", srv.getServiceID());
		
		ServiceWS regSrv = null;
		try {
			regSrv = registry.getService(srv.getServiceID());
		} catch (Throwable exc) {
		}

		if(regSrv == null) {
			log.debug("The registry doesn't have the service id " + srv.getServiceID() + "; publishing that service in the registry");

                        log.debug("Service "+srv+" stored in the registry");
			registry.deployService(this.registryUsername, this.registryPassword, srv.getServiceID(), srv.getName(), srv.isAllowAdditionalInformation(), srv.getVersion(), srv.getPreviousVersionId(), srv.getTimestamp());
			
		} else {
			log.warn("The service " + srv.getServiceID() + " is already present in the registry.");
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#getService(java.lang.String)
	 */
	public DService getService(String serviceID) throws RemoteException, NotFoundException {
		ServiceWS srv = registry.getService(serviceID);
		if (srv==null) throw new NotFoundException("Service with id " + serviceID + " not in registry");
		DService ret = new DService(serviceID, srv.getName(), srv.getVersion(), srv.getPreviousVersionId(), srv.isAllowAdditionalInformation(), srv.getTimestamp(), srv.getTimestamp(), null);
		return ret;
	}
	
	public boolean isServiceLocallyCreated(String serviceId) {
		try {
			ServiceWS serv = registry.getService(serviceId);
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

		registry.undeployService(this.registryUsername, this.registryPassword, serviceId);
	}

	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#storeFacet(eu.secse.deliveryManager.model.DFacetSpecificationSchema)
	 */
	public void storeFacet(FacetAddInfo facet) throws RemoteException, FacetNotAddedException, CredentialsNotValidException {
		if(pLogger.isEnabled()) pLogger.log(System.currentTimeMillis(), "REGISTRY_ADD_FACET_ADDINFO", facet.getSchemaID() + "@SRV:" + facet.getServiceID());
		storeFacet(facet, facet.getServiceID());
	}

	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#storeFacet(eu.secse.deliveryManager.model.EFacetSpecificationSchema, java.lang.String)
	 */
	public void storeFacet(FacetSpec facet, String serviceId) throws RemoteException, FacetNotAddedException, CredentialsNotValidException {
		log.debug("storeFacetSpecification(" + facet + ", " + serviceId + ")");
		
		// check if the schema is present
		FacetWS f;
		try {
			f = registry.getFacetXsdById(facet.getSchemaID());
		} catch (Throwable e) {
			f = null;
		}
	
		// se la facet non è presente, la deploya nel registro
		if(f == null) {
			log.debug("The facet schema " + facet.getSchemaID() + " is not in the registry");
	
			try {
				FacetWS fsXSD = new FacetWS();
				fsXSD.setDocument(facet.getDocument());
				fsXSD.setId(facet.getSchemaID());
				fsXSD.setName(facet.getName());
				fsXSD.setTimestamp(facet.getTimestamp());
				
				registry.storeFacetXSD(this.registryUsername, this.registryPassword, serviceId, fsXSD, facet instanceof FacetAddInfo);
			} catch (Throwable e) {
				log.warn("Cannot store the new received xsd: " + e.getMessage() + " due to: " + e.getCause());
			}
		}
	}
	
	public void storeFacetXml(String serviceId, String facetId, FacetSpecXML xml, String facetType) {
		try {
			FacetWS fsXML = new FacetWS();
			fsXML.setName(xml.getName());
			fsXML.setDocument(xml.getDocument());
			fsXML.setTimestamp(xml.getTimestamp());
			registry.storeFacetXML(this.registryUsername, this.registryPassword, serviceId, facetId, fsXML);
		} catch (Throwable e) {
			log.warn("Cannot store the new received xsd: " + e.getMessage() + " due to: " + e.getCause());
		}		
	}
	
	public void removeFacetXml(String facetXmlID) {
		try {
			log.debug("Removing the facet xml id " + facetXmlID + " from the registry");
			registry.removeFacetXml(this.registryUsername, this.registryPassword, facetXmlID);
		} catch (Throwable e) {
			log.warn("Cannot remove facet xml with id " + facetXmlID + ": " + e.getMessage() + " due to: " + e.getCause());
		}		
	}
	
	public boolean isFacetLocallyCreated(String facetId) {
		try {
			FacetWS facet = registry.getFacetXsdById(facetId);
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
		FacetWS xsd = registry.getFacetXsdById(facetSchemaId);
		
		if (xsd==null) throw new NotFoundException("Could not find facet schema " + facetSchemaId);
		FacetSpecXML exml = null;
		
		try {
			FacetWS xml = registry.getFacetXmlBySchemaId(facetSchemaId);
			if (xml==null) {
				log.debug("Could not get xml for schema " + facetSchemaId + ". Maybe no xml is present");				
			} else {
				exml = new FacetSpecXML(xml.getId(), xml.getName(), xml.getDocument(), xml.getTimestamp(), xml.getTimestamp());
			}
		} catch (Throwable e) {
			log.warn("Caught throwable " + e.getMessage());
		}

		FacetSpec ret;
		if(registry.isAdditionalInformation(xsd.getId())) {
			ret = new FacetAddInfo(xsd.getId(), xsd.getName(), xsd.getDocument(), xsd.getTypeName(), exml, registry.getServiceIdByFacetId(xsd.getId()), null, xsd.getTimestamp(), xsd.getTimestamp());
			log.debug("creating new FacetAddInfo");
		}
		else {
			ret = new FacetSpec(xsd.getId(), xsd.getName(), xsd.getDocument(), xsd.getTypeName(), exml, xsd.getTimestamp(), xsd.getTimestamp());
			log.debug("creating new FacetSpec");
		}
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#getSpecificationFacets(java.lang.String)
	 */
	public Collection<FacetSpec> getSpecificationFacets(String serviceID) throws RemoteException, NotFoundException {
		Collection<FacetSpec> ret = new ArrayList<FacetSpec>();
		
		for (FacetWS xsd : registry.getFacetSpecXSDs(serviceID)) {
			if(xsd == null) continue;
			
			FacetSpecXML exml = null;
			try {
				FacetWS xml = registry.getFacetXmlBySchemaId(xsd.getId());
				exml = new FacetSpecXML(xml.getId(), xml.getName(), xml.getDocument(), xml.getTimestamp(), xml.getTimestamp());
			} catch (Throwable e1) {
				// XML non trovato...

			}
			
			FacetSpec e = new FacetSpec(xsd.getId(), xsd.getName(), xsd.getDocument(), xsd.getTypeName(), exml, xsd.getTimestamp(), xsd.getTimestamp());
		
			ret.add(e);
		}
		
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#getAdditionalInformationFacets(java.lang.String)
	 */
	public Collection<FacetAddInfo> getAdditionalInformationFacets(String serviceID) throws RemoteException, NotFoundException {
		Collection<FacetAddInfo> ret = new ArrayList<FacetAddInfo>();
		
		Collection<FacetWS> facetSpecificationXSDs = registry.getFacetAddInfoXSDs(serviceID);
		if(facetSpecificationXSDs == null) facetSpecificationXSDs=new ArrayList<FacetWS>(0);
		
		for (FacetWS xsd : facetSpecificationXSDs) {
			if(xsd != null) {
				
				FacetSpecXML exml = null;
				try {
					FacetWS xml = registry.getFacetXmlBySchemaId(xsd.getId());
					exml = new FacetSpecXML(xml.getId(), xml.getName(), xml.getDocument(), xml.getTimestamp(), xml.getTimestamp());
				} catch (Throwable e1) {
					// XML non trovato...

				}
				
				FacetAddInfo e = new FacetAddInfo(xsd.getId(), xsd.getName(), xsd.getDocument(), xsd.getTypeName(), exml, serviceID, null, xsd.getTimestamp(), xsd.getTimestamp());
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

		registry.removeFacetXSD(this.registryUsername, this.registryPassword, facetTypeId);
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
		ServiceWS serv = registry.getService(arg0);
		return serv.getProviderId();
	}
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#getFacetSchemaProviderId(java.lang.String)
	 */
	public long getFacetSchemaProviderId(String arg0) throws RemoteException {
		FacetWS facetSchema = registry.getFacetXsdById(arg0);
		return facetSchema.getProviderId();
	}

	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#getServiceName(java.lang.String)
	 */
	public String getServiceName(String serviceId) throws NotFoundException {
		try {
			ServiceWS srv = registry.getService(serviceId);
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
			ServiceWS srv = registry.getService(newServiceId);
			
			while(srv != null && !baseServiceId.equals(srv.getPreviousVersionId())) {
				srv = registry.getService(srv.getPreviousVersionId());
			}
			
			return srv != null && baseServiceId.equals(srv.getPreviousVersionId());
		} catch (Throwable e) {
			return false;
		}
	}
}
