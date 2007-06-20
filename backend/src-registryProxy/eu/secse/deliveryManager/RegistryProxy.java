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
import java.util.Vector;

import javax.annotation.EJB;
import javax.annotation.PostConstruct;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.ejb.Stateless;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import eu.secse.deliveryManager.exceptions.CredentialsNotValidException;
import eu.secse.deliveryManager.exceptions.FacetNotAddedException;
import eu.secse.deliveryManager.exceptions.FacetNotRemovedException;
import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.exceptions.ServiceNotUndeployedException;
import eu.secse.deliveryManager.model.DFacetSpecificationSchema;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.EFacetSpecificationSchema;
import eu.secse.deliveryManager.model.EFacetSpecificationXML;
import eu.secse.deliveryManager.registry.IRegistryProxy;
import eu.secse.deliveryManager.utils.Xml2Dom;

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

	private static final String SERVICE_PROVIDER_XPATH = "/LanguageSpecificSpecification/FacetSpecificationData/CommerceSpec/BusinessEntity/BusinessName";
	private static final String COMMERCE_TYPE_NAME = "Commerce";
	private XPathExpression service_provider_xpath;
	
	@EJB IConfiguration config;

	private SeCSERegistry registry;
	private String registryId;

	private URL registryUrl;
	private String registryUsername; 
	private String registryPassword; 
	private String registryName;

	private long deliveryManagerID; 
	
	
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
		
		try {
			// In order to replace the way the delivery manager contact the 
			// registry, modify the next lines:
			SeCSERegistryServiceLocator srsl = new SeCSERegistryServiceLocator();
			registry = srsl.getSeCSERegistry(registryUrl);
			// END modify
			
			// log all registry invocations
			registry = new LoggingRegistry(registry);
			
			// register delivery manager
			try {
				this.deliveryManagerID = registry.authenticate(new Credentials(this.registryPassword, this.registryUsername));
			} catch (Throwable e) {
				try {
					log.info("Registering the delivery manager into the registry: name=" + this.registryName + " username=" + this.registryUsername + " password=" + this.registryPassword);
					registry.registerServiceProvider(new ServiceProviderDescription(this.registryName, -1), new Credentials(this.registryPassword, this.registryUsername));
				} catch (Exception e1) {
					log.fatal("Cannot register the service provider of the delivery manager: " + e1.getMessage() + " due to: " + e1.getCause());
				}
			}

			// get the registry unique id
			registryId = registry.getRegistryId();
			
			service_provider_xpath = XPathFactory.newInstance().newXPath().compile(SERVICE_PROVIDER_XPATH);
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

	public void deployService(DService service) throws RemoteException {
		log.info("deployService(" + service + ")");

		Service regSrv = null;
		Collection<String> previousFacets;
		try {
			regSrv = registry.getService(service.getServiceID());
		} catch (Throwable exc) {
		}

		if(regSrv == null) {
			log.debug("The registry doesn't have the service id " + service.getServiceID() + "; publishing that service in the registry");

			// serviceId = <registryID>.<internalID>
			// ids = {registryID, serviceID}
			String[] ids = service.getServiceID().split("\\.");
			
			regSrv = new Service();
			regSrv.setRegistryId(ids[0]);
			regSrv.setId(ids[1]);
			regSrv.setName(service.getName());
			regSrv.setAllowAdditionalInformation(service.isAllowAdditionalInformation());
			regSrv.setPreviousVersionId(service.getPreviousVersionId());
			regSrv.setVersion(service.getVersion());
			regSrv.setTimestamp(service.getTimestamp());
			regSrv.setISOTimestamp(service.getIsoTimestamp());

			registry.deployService(regSrv, this.getCredentials(), true);
			
			previousFacets = new Vector<String>(0);
		} else {
			log.debug("The registry already has the service id " + service.getServiceID());

			// Getting all XSDs of the service
			FacetSpecificationXSD[] arrayFacets = registry.getFacetSpecificationXSDs(service.getServiceID(), false);

			previousFacets = new Vector<String>(arrayFacets.length);
			for (FacetSpecificationXSD fsxsd : arrayFacets)
				previousFacets.add(fsxsd.getFacetSpecificationXSDId());
		}


		for (EFacetSpecificationSchema f : service.getSpecType()) {
			log.debug("Considering the received facet specification with id " + f.getSchemaID());

			// check if the schema is present
			if(previousFacets.remove(f.getSchemaID())) {
				log.debug("The facet schema " + f.getSchemaID() + " is already present in the registry");

				// check also its XML
				EFacetSpecificationXML eXml = f.getFacetSpecificationXML();
				FacetSpecificationXML xml = null;
				try {
					xml = registry.getFacetSpecificationXMLByFacetSpecificationXSDId(f.getSchemaID());

					// devo rimuovere la facet presente nel registro?
					if(xml != null && (eXml == null || !xml.getFacetSpecificationXMLId().equals(eXml.getXmlID()))) {
						log.debug("The facet XML id " + f.getSchemaID() + " is present in the registry, but it will be removed.");
						registry.removeFacetSpecificationXML(xml.getFacetSpecificationXMLId(), getCredentials());
					}
				} catch (Throwable e) { // XML non trovato...
				}
				
				// devo pubblicare l'xml?
				if(eXml != null  && (xml == null || !xml.getFacetSpecificationXMLId().equals(eXml.getXmlID()))) {
					try {
						log.debug("The facet XML id " + f.getSchemaID() + " will be inserted in the registry.");
						
						FacetSpecificationXML fsXml = new FacetSpecificationXML();
						fsXml.setDocument(eXml.getDocument());
						fsXml.setFacetSpecificationXMLId(eXml.getXmlID());
						fsXml.setFacetTypeName(f.getTypeName());
						fsXml.setName(eXml.getName());
						fsXml.setISOTimestamp(eXml.getIsoTimestamp());
						fsXml.setTimestamp(eXml.getTimestamp());

						registry.addFacetSpecificationXML(service.getServiceID(), f.getSchemaID(), fsXml, getCredentials(), ENABLE_VALIDATION, true);
					} catch (Throwable e) { // XML non trovato...
					}
				}
			} else {
				log.debug("The facet schema " + f.getSchemaID() + " is not present in the registry");

				FacetSpecificationXSD fsXSD = new FacetSpecificationXSD();
				fsXSD.setAdditionalInformation(false);
				fsXSD.setDocument(f.getDocument());
				fsXSD.setFacetSpecificationXSDId(f.getSchemaID());
				fsXSD.setName(f.getName());
				fsXSD.setServiceId(service.getServiceID());
				fsXSD.setTimestamp(f.getTimestamp());
				fsXSD.setISOTimestamp(f.getIsoTimestamp());
				registry.addFacetSpecificationXSD(service.getServiceID(), fsXSD, getCredentials(), false, f.getTypeName(), true);
				
				// deploya la facet XML nel registro
				EFacetSpecificationXML eXml = f.getFacetSpecificationXML();

				// devo pubblicare l'xml?
				if(eXml != null) {
					FacetSpecificationXML fsXml = new FacetSpecificationXML();
					fsXml.setDocument(eXml.getDocument());
					fsXml.setFacetSpecificationXMLId(eXml.getXmlID());
					fsXml.setFacetTypeName(f.getTypeName());
					fsXml.setName(eXml.getName());
					fsXml.setISOTimestamp(eXml.getIsoTimestamp());
					fsXml.setTimestamp(eXml.getTimestamp());

					registry.addFacetSpecificationXML(service.getServiceID(), f.getSchemaID(), fsXml, getCredentials(), ENABLE_VALIDATION, true);
				}
			}
		}
		
		for (String xsdID : previousFacets) {
			log.debug("Removing the facet schema " + xsdID);
			try {
				registry.removeFacetSpecification(xsdID, getCredentials());
			} catch (Throwable e) {
				log.warn("Cannot remove the facet schema id " + xsdID + ": " + e.getMessage() + " due to: " + e.getCause());
			}
		}
	}

	public DFacetSpecificationSchema getAdditionalInformationFacet(String facetSpecificationSchemaID) throws RemoteException, NotFoundException {
		FacetSpecificationXSD xsd = registry.getFacetSpecificationXSDById(facetSpecificationSchemaID);
		
		EFacetSpecificationXML exml = null;
		try {
			FacetSpecificationXML xml = registry.getFacetSpecificationXMLByFacetSpecificationXSDId(facetSpecificationSchemaID);
			exml = new EFacetSpecificationXML(xml.getFacetSpecificationXMLId(), xml.getName(), xml.getDocument(), xml.getTimestamp(), xml.getISOTimestamp());
		} catch (Throwable e) {
		}

		String typeName = registry.getFacetType(facetSpecificationSchemaID);
		DFacetSpecificationSchema ret = new DFacetSpecificationSchema(xsd.getFacetSpecificationXSDId(), xsd.getName(), xsd.getDocument(), typeName, exml, xsd.getServiceId(), null, xsd.getTimestamp(), xsd.getISOTimestamp());
		
		return ret;
	}

	public Collection<DFacetSpecificationSchema> getAdditionalInformationFacets(String serviceID) throws RemoteException, NotFoundException {
		Collection<DFacetSpecificationSchema> ret = new ArrayList<DFacetSpecificationSchema>();
		
		FacetSpecificationXSD[] facetSpecificationXSDs = registry.getFacetSpecificationXSDs(serviceID, true);
		if(facetSpecificationXSDs == null) facetSpecificationXSDs=new FacetSpecificationXSD[0];
		
		for (FacetSpecificationXSD xsd : facetSpecificationXSDs) {
			if(xsd != null && xsd.isAdditionalInformation()) {
				
				EFacetSpecificationXML exml = null;
				try {
					FacetSpecificationXML xml = registry.getFacetSpecificationXMLByFacetSpecificationXSDId(xsd.getFacetSpecificationXSDId());
					exml = new EFacetSpecificationXML(xml.getFacetSpecificationXMLId(), xml.getName(), xml.getDocument(), xml.getTimestamp(), xml.getISOTimestamp());
				} catch (Throwable e1) {
					// XML non trovato...

				}
				
				String typeName = registry.getFacetType(xsd.getFacetSpecificationXSDId());
				DFacetSpecificationSchema e = new DFacetSpecificationSchema(xsd.getFacetSpecificationXSDId(), xsd.getName(), xsd.getDocument(), typeName, exml, xsd.getServiceId(), null, xsd.getTimestamp(), xsd.getISOTimestamp());
				ret.add(e);
			}
		}
		
		return ret;
	}

	public long getDeliveryManagerId() throws CredentialsNotValidException, RemoteException {
		return this.deliveryManagerID;
	}

	public DService getService(String serviceID) throws RemoteException, NotFoundException {
		Service srv = registry.getService(serviceID);
		DService ret = new DService(serviceID, srv.getName(), srv.getVersion(), srv.getPreviousVersionId(), srv.isAllowAdditionalInformation(), null, srv.getTimestamp(), srv.getISOTimestamp());

		for (FacetSpecificationXSD xsd : registry.getFacetSpecificationXSDs(serviceID, false)) {
			if(xsd == null) continue;
			
			EFacetSpecificationXML exml = null;
			try {
				FacetSpecificationXML xml = registry.getFacetSpecificationXMLByFacetSpecificationXSDId(xsd.getFacetSpecificationXSDId());
				exml = new EFacetSpecificationXML(xml.getFacetSpecificationXMLId(), xml.getName(), xml.getDocument(), xml.getTimestamp(), xml.getISOTimestamp());
			} catch (Throwable e1) {
				// XML non trovato...

			}
			
			String typeName = registry.getFacetType(xsd.getFacetSpecificationXSDId());
			DFacetSpecificationSchema e = new DFacetSpecificationSchema(xsd.getFacetSpecificationXSDId(), xsd.getName(), xsd.getDocument(), typeName, exml, xsd.getServiceId(), null, xsd.getTimestamp(), xsd.getISOTimestamp());
		
			ret.addSpecType(e);
		}
				
		return ret;
	}

	public void removeFacetSpecification(String facetTypeId) throws RemoteException, FacetNotRemovedException {
		registry.removeFacetSpecification(facetTypeId, getCredentials());		
	}

	public void storeFacetSpecification(DFacetSpecificationSchema facet) throws RemoteException, FacetNotAddedException, CredentialsNotValidException {
		log.debug("storeFacetSpecification(" + facet + ")");
		
		EFacetSpecificationXML receivedXml = facet.getFacetSpecificationXML();
		String receivedXmlID = (receivedXml!=null?receivedXml.getXmlID():null);

		// check if the schema is present
		FacetSpecificationXSD f;
		try {
			f = registry.getFacetSpecificationXSDById(facet.getSchemaID());
		} catch (Throwable e) {
			f = null;
		}

		if(f != null) {
			// The schema is present
			log.debug("The facet schema " + facet.getSchemaID() + " is already in the registry");

			// check also its XML
			String registryXmlID;
			try {
				registryXmlID = registry.getFacetSpecificationXMLIdByFacetSpecificationXSDId(facet.getSchemaID());
			} catch (Throwable e) {
				registryXmlID = null;
			}

			// devo rimuovere quello che c'e' nel registro?
			if(registryXmlID != null && !registryXmlID.equals(receivedXmlID)) {
				log.debug("Removing the facet xml id " + registryXmlID + " from the registry");
				registry.removeFacetSpecificationXML(registryXmlID, getCredentials());
			}

			// devo aggiungere la nuova facet nel registro?
			if(receivedXmlID != null && !receivedXmlID.equals(registryXmlID)) {
				log.debug("Adding the facet xml id " + receivedXmlID + " from the registry");
				FacetSpecificationXML fsXML = new FacetSpecificationXML();
				fsXML.setDocument(receivedXml.getDocument());
				fsXML.setFacetSpecificationXMLId(receivedXml.getXmlID());
				fsXML.setFacetTypeName(facet.getTypeName());
				fsXML.setName(receivedXml.getName());
				fsXML.setTimestamp(receivedXml.getTimestamp());
				fsXML.setISOTimestamp(receivedXml.getIsoTimestamp());

				registry.addFacetSpecificationXML(facet.getServiceID(), facet.getSchemaID(), fsXML , getCredentials(), ENABLE_VALIDATION, true);
			}
		} else {
			log.debug("The facet schema " + facet.getSchemaID() + " is not in the registry");

			// deploya la facet nel registro
			try {
				FacetSpecificationXSD fsXSD = new FacetSpecificationXSD();
				fsXSD.setAdditionalInformation(true);
				fsXSD.setDocument(facet.getDocument());
				fsXSD.setFacetSpecificationXSDId(facet.getSchemaID());
				fsXSD.setServiceId(facet.getServiceID());
				fsXSD.setName(facet.getName());
				fsXSD.setTimestamp(facet.getTimestamp());
				fsXSD.setISOTimestamp(facet.getIsoTimestamp());
				
				registry.addFacetSpecificationXSD(facet.getServiceID(), fsXSD, getCredentials(), true, facet.getTypeName(), true);
			} catch (Throwable e) {
				log.warn("Cannot store the new received xsd: " + e.getMessage() + " due to: " + e.getCause());
			}

			if(receivedXml != null) {
				try {
					FacetSpecificationXML fsXML = new FacetSpecificationXML();
					fsXML.setName(receivedXml.getName());
					fsXML.setDocument(receivedXml.getDocument());
					fsXML.setFacetTypeName(facet.getTypeName());
					fsXML.setFacetSpecificationXMLId(receivedXml.getXmlID());
					fsXML.setTimestamp(receivedXml.getTimestamp());
					fsXML.setISOTimestamp(receivedXml.getIsoTimestamp());
					registry.addFacetSpecificationXML(facet.getServiceID(), facet.getSchemaID(), fsXML , getCredentials(), ENABLE_VALIDATION, true);
				} catch (Throwable e) {
					log.warn("Cannot store the new received xsd: " + e.getMessage() + " due to: " + e.getCause());
				}
			}
		}
	}

	public void undeployService(String serviceId) throws RemoteException, ServiceNotUndeployedException, CredentialsNotValidException {
		registry.undeployService(serviceId, getCredentials());
	}
	
	public String getRegistryId() {
		return this.registryId;
	}
	
	public long getServiceProviderId(String arg0) throws NotFoundException, RemoteException {
		Service serv = registry.getService(arg0);
		return serv.getProviderId();
	}
	
	public long getFacetSchemaProviderId(String arg0) throws RemoteException {
		FacetSpecificationXSD facetSchema = registry.getFacetSpecificationXSDById(arg0);
		return facetSchema.getProviderId();
	}

	public String getServiceName(String serviceId) throws NotFoundException {
		try {
			Service srv = registry.getService(serviceId);
			return srv.getName();
		} catch (Throwable e) {
			throw new NotFoundException("The service " + serviceId + " is not found.");
		}
	}
	
	public String getServiceProvider(String serviceId) throws NotFoundException {
		try {
			for (FacetSpecificationXML xml : registry.getFacetSpecificationXMLsByServiceId(serviceId, false)) {
				if (COMMERCE_TYPE_NAME.equalsIgnoreCase(xml.getFacetTypeName())) {
					try {
						Document dom = Xml2Dom.xmlToDom(xml.getDocument());
						String serviceProvider = (String) service_provider_xpath.evaluate(dom, XPathConstants.STRING);
						if(serviceProvider != null && serviceProvider.length() > 0) return serviceProvider;
					} catch (Throwable e) {
						log.debug("exception thrown while retriving the service provider: " + e.getMessage() + " due to:" + e.getCause());
					}
				}
			}
		} catch (Throwable e) {
			log.warn("exception thrown while retriving the service provider: " + e.getMessage() + " due to:" + e.getCause());
		}

		throw new NotFoundException("Error while retriving the serviceProvider of the service " + serviceId + ": the service doesn't exist or it has not a commerce facet.");
	}

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
