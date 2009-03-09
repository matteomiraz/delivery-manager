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

package eu.secse.deliveryManager.core;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Vector;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.secse.deliveryManager.data.ElementEnt;
import eu.secse.deliveryManager.data.ElementEntPK;
import eu.secse.deliveryManager.data.ElementExtraInfo;
import eu.secse.deliveryManager.data.FacetEnt;
import eu.secse.deliveryManager.data.FacetXmlEnt;
import eu.secse.deliveryManager.data.FederatedFacet;
import eu.secse.deliveryManager.data.FederatedPromotion;
import eu.secse.deliveryManager.data.FederatedService;
import eu.secse.deliveryManager.data.FederatedXml;
import eu.secse.deliveryManager.data.FederationEnt;
import eu.secse.deliveryManager.data.ServiceEnt;
import eu.secse.deliveryManager.exceptions.CredentialsNotValidException;
import eu.secse.deliveryManager.exceptions.FacetNotAddedException;
import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.FacetAddInfo;
import eu.secse.deliveryManager.model.FacetSpec;
import eu.secse.deliveryManager.model.FacetSpecXML;
import eu.secse.deliveryManager.registry.IRegistryProxy;

/**
 * Bean di sessione per la gestione del modello del delivery manager: si preoccupa di gestire le istanze
 * delle classi eu.secse.deliveryManager.data.*.
 * <b>Nota</b>: le operazioni di effettuate da questo bean sono idempotenti.
 */
@Stateless
@TransactionAttribute(value=TransactionAttributeType.REQUIRED)
public class ModelManagerBean implements ModelManager {

	private static final Log log = LogFactory.getLog(ModelManager.class);

	private InitialContext ctx;

	@PersistenceContext(unitName="deliveryManager")
	protected EntityManager em;

	@EJB protected IRegistryProxy registry;

	@EJB protected IFederationCoordinator fedCoord;

	/**
	 * 	Segnala la presenza di elementi, che sono già presenti nel registro locale
	 */
	public ServiceEnt add(String serviceId) {
		log.debug("ModelManagerBean.add(" + serviceId + ")");

		if (serviceId==null) {
			log.warn("Service id null, ignoring");
			return null;
		}

		ServiceEnt srv = em.find(ServiceEnt.class, new ElementEntPK(serviceId, ServiceEnt.SERVICE_ENT));
		if(srv != null)
			return srv;

		try {
			DService dsrv = registry.getService(serviceId);
			srv = new ServiceEnt(dsrv.getServiceID(), registry.isServiceLocallyCreated(serviceId), dsrv.isAllowAdditionalInformation());
			em.persist(srv);			
			em.flush();
			postInsert(srv);

			return srv;
		} catch (RemoteException e) {
			log.debug("Exception raised while talking with the registry...");
		} catch (NotFoundException e) {
			log.warn("The service " + serviceId + " is not present in the local registry!");
		}
		return null;
	}


	/**
	 * Segnala la presenza nel registro di una facet.
	 * @return la facetEnt corrispondente. Se non esiste nel registro, restituisce null.
	 */
	public FacetEnt add(String serviceId, String facetId) {
		log.debug("ModelManagerBean.add(" + serviceId + ", " + facetId + ")");

		if (serviceId == null || facetId==null ) {
			log.warn("Service id or facet id null, ignoring");
			return null;
		}

		ServiceEnt srv = add(serviceId);
		if(srv == null) {
			log.debug("The related service with id (" + serviceId + ") doesn't exist!");
			return null;
		}

		FacetEnt facet = em.find(FacetEnt.class, new ElementEntPK(facetId, FacetEnt.FACET_ENT));
		if(facet != null) 
			return facet;

		try {
			FacetSpec efacet = registry.getFacet(facetId);

			boolean addInfo = efacet instanceof FacetAddInfo;
			boolean original = registry.isFacetLocallyCreated(facetId);

			Date timestamp;
			if(original) {
				timestamp = new Date();
			} else {
				log.debug("");
				timestamp = new Date(0);
			}

			facet = new FacetEnt(facetId, srv, addInfo, original, timestamp);

			em.persist(facet);
			em.flush();
			postInsert(facet);

			return facet;				
		} catch (RemoteException e) {
			log.debug("Exception raised while talking with the registry...");
		} catch (NotFoundException e) {
			log.warn("The facet (schema) " + facetId + " is not present in the local registry!");
		}

		return null;
	}

	/**
	 * Segnala la presenza di elementi, che sono già presenti nel registro locale
	 */
	public FacetXmlEnt add(String serviceId, String facetId, String facetXmlId) {
		log.debug("ModelManagerBean.add(" + serviceId + ", " + facetId + ", " + facetXmlId + ")");

		if (serviceId == null || facetId==null || facetXmlId==null) {
			log.warn("Service id, facet id or xml id null, ignoring");
			return null;
		}

		FacetEnt facet = add(serviceId, facetId);
		if(facet == null) return null;

		FacetXmlEnt facetXml = em.find(FacetXmlEnt.class, new ElementEntPK(facetXmlId, FacetXmlEnt.FACET_XML_ENT));
		if(facetXml != null)
			return facetXml;

		try {
			FacetSpecXML efacetXml = registry.getFacet(facetId).getFacetSpecificationXML();

			facetXml = new FacetXmlEnt(facet, efacetXml.getXmlID());

			em.persist(facetXml);
//			Aggiorna il timestamp della facet
			facet.setTimestamp(new Date());

			em.flush();

			postInsert(facetXml);

			return facetXml;				
		} catch (RemoteException e) {
			log.debug("Exception raised while talking with the registry...");
		} catch (NotFoundException e) {
			log.warn("The facet (schema) " + facetId + " is not present in the local registry!");
		}

		return null;
	}

	/**
	 * Segnala la ricezione di un servizio (vengono ignorate le facet attaccate!) che vanno inseriti nel registro locale
	 */
        @TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ServiceEnt add(DService service) {

		if (service==null) {
			log.warn("DService is null, ignoring");
			return null;
		}		

		ServiceEnt srv = em.find(ServiceEnt.class, new ElementEntPK(service.getServiceID(), ServiceEnt.SERVICE_ENT));
		if(srv != null) 
			return srv;

		try {
			registry.storeService(service);
			
			srv = new ServiceEnt(service.getServiceID(), false, service.isAllowAdditionalInformation());
			em.persist(srv);
			em.flush();
			
			postInsert(srv);

			return srv;
		} catch (RemoteException e) {
			log.debug("Exception raised while talking with the registry... ");
                        log.debug(e.getMessage());
                        e.printStackTrace();
			return null;
		}
	}

	/*
	 * Segnala la ricezione di facet schema & xml che vanno inseriti nel registro locale
	 */
	public FacetEnt add(FacetAddInfo dfacet) {
		if (dfacet==null) {
			log.debug("Facet is null, ignoring");
			return null;			
		}

		return add(dfacet, lookup(dfacet.getServiceID()));
	}

	/**
	 * Segnala la ricezione di facet schema & xml che vanno inseriti nel registro locale
	 */
	public FacetEnt add(FacetSpec dfacet, ServiceEnt service) {
		if (dfacet==null || service == null) {
			log.warn("Facet or service is null, ignoring");
			return null;
		}

		// Cache dei valori utili
		FacetSpecXML eXml = dfacet.getFacetSpecificationXML();

		// Cerca la facet
		FacetEnt facet = em.find(FacetEnt.class, new ElementEntPK(dfacet.getSchemaID(), FacetEnt.FACET_ENT));

		// Se la facet è già stata ricevuta
		if(facet != null) {

			// controlla se la versione della nuova facet è più recente di quella conosciuta
			if(facet.getTimestamp().before(dfacet.getDmTimestamp()) || facet.getTimestamp().equals(dfacet.getDmTimestamp())) {
				// la facet ricevuta è più recente 

				if(dfacet.getFacetSpecificationXML() != null) // la facet ricevuta ha una facetXml associata
					this.add(dfacet.getFacetSpecificationXML(), facet, dfacet.getTypeName(), dfacet.getDmTimestamp());
				else { // la facet ricevuta non ha una facetXml associata

					if(facet.getXml() != null)  
						this.changeFacetXml(facet.getXml(), dfacet.getDmTimestamp());
					else 
						facet.setTimestamp(dfacet.getDmTimestamp());
				}
			}

			return facet;
		}

		try {

			facet = new FacetEnt(dfacet.getSchemaID(), service, dfacet instanceof FacetAddInfo, false, new Date(0));
			registry.storeFacet(dfacet, service.getElemPK().getId());
			facet.setTimestamp(dfacet.getDmTimestamp());
			em.persist(facet);

			FacetXmlEnt facetXml = null;
			if(eXml != null) {
				facetXml = new FacetXmlEnt(facet, eXml.getXmlID());
				registry.storeFacetXml(service.getElemPK().getId(), facet.getElemPK().getId(), eXml, dfacet.getTypeName());
				em.persist(facetXml);
				facet.setXml(facetXml); // TODO: verificare se da problemi di persistenza
				em.flush();
			}

			postInsert(facet);
			if(facetXml != null) postInsert(facetXml);

			return facet;
		} catch (RemoteException e) {
			log.debug("Exception raised while talking with the registry...");
			return null;
		} catch (FacetNotAddedException e) {
			log.warn("Impossible to add the received facet in the registry.");
			return null;
		} catch (CredentialsNotValidException e) {
			log.warn("Invalid credentials to store the facet in the registry.");
			return null;
		}
	}

	public FacetXmlEnt add(FacetSpecXML xml, FacetEnt facet, String facetType, Date facetTimestamp) {
		if (facet ==null || facetType == null || facetTimestamp == null) {
			log.debug("Facet, facet type or timestamp null, ignoring");
			return null;
		}

		if(xml == null) changeFacetXml(facet.getXml(), facetTimestamp);

		FacetXmlEnt fXml = facet.getXml();

		if(fXml != null) { 
			if(facet.getTimestamp().before(facetTimestamp)) {
				// verifica se c'è già un xml associato
				if(fXml.getElemPK().getId().equals(xml.getXmlID())) {
					log.debug("Detected temporal hole!");
					facet.setTimestamp(facetTimestamp);
				} else {
					log.debug("Delete Facet XML detected!!!");
					this.changeFacetXml(fXml, facetTimestamp);
					fXml = this.add(xml, facet, facetType, facetTimestamp);
					// non serve aggiornare il timestamp in quanto verrà aggiunto un nuovo xml!
				}
			}
			return fXml;
		}

		if(facet.getTimestamp().before(facetTimestamp) || facet.getTimestamp().equals(facetTimestamp)) {
			fXml = new FacetXmlEnt(facet, xml.getXmlID());
			registry.storeFacetXml(facet.getService().getElemPK().getId(), facet.getElemPK().getId(), xml, facetType);
			facet.setTimestamp(facetTimestamp);
			em.persist(fXml);
			em.flush();
			postInsert(fXml);
		} else {
			log.debug("discarding the received xml: received timestamp (" + facetTimestamp + ") < last known timestamp (" + facet.getTimestamp() + ").");
		}

		return fXml;

	}

	/**
	 * Segnala la ricezione da una federazione di elementi che vanno inseriti nel registro locale
	 */
	public FederatedService add(FederationEnt federation, DService service) {
		log.debug("Adding service " + service + " in federation " + federation );
		if (service==null || federation == null) {
			log.warn("Service or federation is null, ignoring");
			return null;
		}

		FederatedService fe = lookup(service.getServiceID(), federation);
		if(fe != null) return fe;

		this.add(service);		
		fe = retrieve(service.getServiceID(),federation, true);

		return fe;
	}

	/**add
	 * Segnala la ricezione da una federazione di elementi che vanno inseriti nel registro locale
	 */
	public FederatedFacet add(FederationEnt federation, FacetAddInfo dfacet) {
		log.debug("Adding facet " + dfacet + " in federation " + federation);
		if (federation == null || dfacet == null) {
			log.warn("Federation or facet null, ignoring");
			return null;
		}
		ServiceEnt srv = em.find(ServiceEnt.class, new ElementEntPK( dfacet.getServiceID(), ServiceEnt.SERVICE_ENT));
		return add(federation, dfacet, srv);
	}

	/**
	 * Segnala la ricezione da una federazione di elementi che vanno inseriti nel registro locale
	 */
	public FederatedFacet add(FederationEnt federation, FacetSpec efacet, ServiceEnt srv) {
		if(srv == null) {
			log.warn("Aborting facet insertion: the service of the facet " + efacet.getSchemaID() + " is unknown");
			return null;
		}

		FederatedFacet fe = lookup(srv.getElemPK().getId(), efacet.getSchemaID(), federation);
		if(fe != null) {
			// check the timestamp
			if(fe.getFacet().getTimestamp().before(efacet.getDmTimestamp())) {
				this.add(efacet, srv);
			}

			return fe;
		}

		// È stata ricevuta una nuova promozione

		FacetEnt facet = this.add(efacet, srv);

		FederatedService fserv=lookup(srv.getElemPK().getId(),federation);
		if (fserv==null) {
			log.error("Could not lookup federated service " + srv.getElemPK().getId() + ". Aborting facet add");
			return null;
		}

		fe = retrieve(srv.getElemPK().getId(),facet.getElemPK().getId(),federation, true);		
		return fe;
	}
	
	public FederatedXml add(FederationEnt federation, FacetSpecXML xml, FacetEnt facet, String facetType, Date facetTimestamp) {
		log.debug("Adding xml " + xml + " to facet " + facet + " of type " + facetType + " with timestamp " + facetTimestamp +" in federation " + federation);		
		if(facet == null) {
			log.warn("Cannot insert the received xml facet: the facet schema is unknown");
			return null;
		}
		if (xml == null || facet == null || federation ==null || facetType==null || facetTimestamp == null ) {
			log.warn("Null parameter passed, ignoring");
			return null;
		}


		FederatedXml fe = lookup(facet.getService().getElemPK().getId(), facet.getElemPK().getId(), xml.getXmlID(), federation);
		if(fe != null) {
			// check the timestamp
			if(fe.getFacet().getFacet().getTimestamp().before(facetTimestamp)) {
				this.add(xml, facet, facetType, facetTimestamp);
			}

			return fe;
		}

		this.add(xml, facet, facetType, facetTimestamp);

		fe = retrieve(facet.getService().getElemPK().getId(),facet.getElemPK().getId(),xml.getXmlID(),federation);					
		return fe;
	}

	public void delete(ServiceEnt service) {
		log.debug("Deleting service " + service);
		if (service==null) {
			log.warn("Service is null, ignoring ");
			return;
		}
		deleteElement(service);
	}

	public void delete(FacetEnt facet) {
		log.debug("Deleting facet " +facet );
		if (facet==null) {
			log.warn("Facet is null, ignoring");
			return;
		}		
		deleteElement(facet);
	}
	
	public void delete(FacetXmlEnt facetXml){
		log.debug("Deleting facet " +facetXml );
		if (facetXml==null) {
			log.warn("Facet xml is null, ignoring");
			return;
		}		
		deleteElement(facetXml);
	}

	public void changeFacetXml(FacetXmlEnt facetXml, Date timestamp) {
		log.debug("Removing xml " + facetXml + " with timestamp " +  timestamp);
		if (timestamp==null || facetXml == null) {
			log.warn("Timestamp or facet is null, ignoring ");
			return;
		}
		if(facetXml.getFacet().getTimestamp().before(timestamp) || facetXml.getFacet().getTimestamp().equals(timestamp)) {
			deleteElement(facetXml);
			facetXml.getFacet().setTimestamp(timestamp);
		} else 
			log.debug("discarding the deletion of xml(" + facetXml.getElemPK().getId() + "): deletion timestamp (" + timestamp + ") < last known timestamp (" + facetXml.getFacet().getTimestamp() + ").");
	}

	public void delete(FederatedService fedService) {
		log.debug("Removing federated service " + fedService);
		if (fedService==null) {
			return;
		}

		ServiceEnt e = fedService.getService();

		//Cascaded:
//		Collection<FederatedFacet> facets=fedService.getFacets();

//		fedService.getFacets().clear();		
//		for (FederatedFacet fac:facets) {			
//		FederatedXml xml=fac.getXml();
//		if (xml!=null) {
//		fac.setXml(null);								
//		em.remove(xml);
//		}
//		em.remove(fac);			
//		}

		em.remove(fedService);
		em.flush();
		em.refresh(e);
		deleteElement(e);

	}

	public void delete(FederatedFacet fedFacet) {
		log.debug("Removing federated facet " + fedFacet);
		if (fedFacet == null) {
			log.warn("Federated facet is null, ignoring");
			return;
		}

		FacetEnt e = fedFacet.getFacet();

//		Cascaded:
//		//Remove xml first
//		FederatedXml xml=fedFacet.getXml();
//		if (xml!=null) {
//		fedFacet.setXml(null);
//		em.remove(xml);
//		}		

		em.remove(fedFacet);
		em.flush();
		em.refresh(e);
		deleteElement(e);	


	}

	public void delete(FederatedXml fedXml) {
		log.debug("Removing federated xml " + fedXml);
		if (fedXml == null) {
			log.warn("Federated Xml is null, ignoring ");
			return;
		}
		FacetXmlEnt e = fedXml.getXml();

		//unlink owning side 
		fedXml.getFacet().setXml(null);

		em.remove(fedXml);
		em.flush();
		em.refresh(e);
		deleteElement(e);
	}

	private void deleteElement(ElementEnt e) {
		int aei = e.getAliveExtraInfo(em);
		int afp = e.getAliveFederatedElements();

		log.info("The " + e.toString() + " has " + aei + " direct promotions and " + afp + " federation promotions.");
		
		//L'elemento non ha più niente che lo vincoli
		if(aei == 0 && afp == 0) {
			log.info("Removing the " + e.toString());

			if(e instanceof ServiceEnt) {
				preDelete((ServiceEnt) e);

				try {
					registry.removeService(e.getElemPK().getId());
					em.remove(e);
				} catch (Throwable exc) {
					log.warn("Exception raised from the registry: " + e.toString());
				}
			} else if(e instanceof FacetEnt) {
				preDelete((FacetEnt) e);

				try {
					registry.removeFacetSpecification(e.getElemPK().getId());

					em.remove(e);
				} catch (Throwable exc) {
					log.warn("Exception raised from the registry: " + e.toString());
				}
			} else if(e instanceof FacetXmlEnt) {
				preDelete((FacetXmlEnt) e);

				try {
					registry.removeFacetXml(e.getElemPK().getId());

					this.em.remove(e);
				} catch (Throwable exc) {
					log.warn("Exception raised from the registry: " + e.toString());
				}

			} else log.warn("Unknown type: " + e.getClass().getCanonicalName());			
		}

	}

	public ServiceEnt lookup(String serviceId) {
		ServiceEnt srv = em.find(ServiceEnt.class, new ElementEntPK(serviceId,ServiceEnt.SERVICE_ENT));
		if(srv != null) return srv;

		return null;
	}

	public FacetEnt lookup(String serviceId, String facetId) {
		FacetEnt facet = em.find(FacetEnt.class, new ElementEntPK(facetId, FacetEnt.FACET_ENT));
		if(facet != null) {
			if(!facet.getService().getElemPK().getId().equals(serviceId))
				log.debug("The stored serviceID doesn't match with the one used");

			return facet;
		}

		return null;
	}

	public FacetXmlEnt lookup(String serviceId, String facetId, String facetXmlId) {
		FacetXmlEnt facetXml = em.find(FacetXmlEnt.class, new ElementEntPK(facetXmlId, FacetXmlEnt.FACET_XML_ENT));
		if(facetXml != null) {
			if(!facetXml.getFacet().getService().getElemPK().getId().equals(serviceId))
				log.debug("The stored serviceID doesn't match with the one used");

			if(!facetXml.getFacet().getElemPK().getId().equals(facetId))
				log.debug("The stored facet ID doesn't match with the one used");

			return facetXml;
		}

		return null;
	}

	public FederatedService lookup(String serviceId, FederationEnt federation) {
		ServiceEnt srv = lookup(serviceId);
		if(srv == null) return null;

		FederatedService fe = FederatedService.search(em, srv, federation);
		return fe;
	}

	public FederatedFacet lookup(String serviceId, String facetId, FederationEnt federation) {
		FacetEnt facet = lookup(serviceId, facetId);
		if(facet == null) return null;

		FederatedFacet fe = FederatedFacet.search(em, facet, federation);
		return fe;
	}

	public FederatedXml lookup(String serviceId, String facetId, String facetXmlId, FederationEnt federation) {
		FacetXmlEnt facetXml = lookup(serviceId, facetId, facetXmlId);
		if(facetXml == null) return null;

		FederatedXml fe = FederatedXml.search(em, facetXml, federation);
		return fe;
	}

	public FederatedPromotion lookup(ElementEnt element, FederationEnt federation) {
		FederatedPromotion fedProm = FederatedPromotion.search(em, element, federation);
		return fedProm;
	}


	private void preDelete(ServiceEnt srv) {
		// notifica la cancellazione del servizio su tutte le federazioni interessate
		for(FederatedPromotion prom : srv.getFederatedPromotion()) {
			fedCoord.getProxy(prom.getFederation().getMethod()).delete(prom, srv);
			em.remove(prom);
		}

		for(ElementExtraInfo ei : srv.getExtraInfo().values()) {
			if(ei.isSharing()) getGenericProxy(ei).delete(srv);
		}

//		Cascaded:		
//		for (FacetEnt fent:srv.getFacets()) {			
//		em.remove(fent);
//		}
	}

	private void preDelete(FacetEnt facet) {
		if(facet.isAddInfo()) {
			// controlla se la facet è stata promossa esplicitamente
			for(FederatedPromotion prom: facet.getFederatedPromotion()) {
				fedCoord.getProxy(prom.getFederation().getMethod()).delete(prom, facet);
				em.remove(prom);
				em.flush();
			}

			// controlla se il servizio ha qualche promozione "shareAll"... in questo caso notifica pure lì 
			for(FederatedPromotion serviceProm: facet.getService().getFederatedPromotion())
				if(serviceProm.isShareAll()) fedCoord.getProxy(serviceProm.getFederation().getMethod()).delete(serviceProm, facet);


			for(ElementExtraInfo ei : facet.getService().getExtraInfo().values()) {
				if(ei.isShareAll()) getGenericProxy(ei).delete(facet);
			}

			for(ElementExtraInfo ei : facet.getExtraInfo().values()) {
				if(ei.isSharing()) getGenericProxy(ei).delete(facet);
			}

		} else {
			// la facet è di specifica => notifica nelle promozioni dei servizi!
			for(FederatedPromotion prom: facet.getService().getFederatedPromotion()) {
				if(!facet.isAddInfo() || prom.isShareAll())
					fedCoord.getProxy(prom.getFederation().getMethod()).delete(prom, facet);
			}

			for(ElementExtraInfo ei : facet.getService().getExtraInfo().values()) {
				if(ei.isSharing()) getGenericProxy(ei).delete(facet);
			}			
		}
	}

	private void preDelete(FacetXmlEnt xml) {
		FacetEnt facet = xml.getFacet();

		for(FederatedPromotion prom: facet.getFederatedPromotion()) {
			fedCoord.getProxy(prom.getFederation().getMethod()).delete(prom, xml);
		}

		for(FederatedPromotion prom: facet.getService().getFederatedPromotion()) {
			if(!facet.isAddInfo() || prom.isShareAll())
				fedCoord.getProxy(prom.getFederation().getMethod()).delete(prom, xml);
		}

		for(ElementExtraInfo ei : facet.getExtraInfo().values()) {
			if(ei.isShareAll()) getGenericProxy(ei).delete(xml);
		}

		for(ElementExtraInfo ei : facet.getService().getExtraInfo().values()) {
			if((!facet.isAddInfo() && ei.isSharing()) || ei.isShareAll()) getGenericProxy(ei).delete(xml);
		}
	}

	public void postInsert(ServiceEnt srv) {
		// nothing to do here!
	}

	public void postInsert(FacetEnt facet) {
		log.info("Executing postInsert for "+ facet.toString());
		ServiceEnt srv = facet.getService();
		FacetSpec facetSpec;

		// recupera la facet dal registro
		try {
			facetSpec = registry.getFacet(facet.getElemPK().getId());
		} catch (RemoteException e) {
			log.debug("Exception raised while talking with the registry: " + e.toString());
			return;
		} catch (NotFoundException e) {
			log.debug("Exception raised while talking with the registry: " + e.toString());
			return;
		}

		log.info("Size of the promotion "+srv.getFederatedPromotion().size()+ "facet.isAddInfo "+facet.isAddInfo() );
		// se il servizio è promosso in una federazione
		if(srv.getFederatedPromotion().size() > 0) {

			if(!facet.isAddInfo()) {
				// spedisce se la facet se è di specifica
				for (FederatedPromotion p : srv.getFederatedPromotion()) {
					fedCoord.getProxy(p.getFederation().getMethod()).addFacetSpec(p, facetSpec);
				}
			} else {
				// spedisce la facet se la promozione è totale
				for (FederatedPromotion p : srv.getFederatedPromotion()) {
					if(p.isShareAll()){
						fedCoord.getProxy(p.getFederation().getMethod()).addFacetSpec(p, facetSpec);
						log.debug("Sending facet "+facet.toString() +" because the promotion is total");
					}
				}
			}
		}

		// verifica se la facet è associata ad un servizio condiviso
		for (ElementExtraInfo e : srv.getExtraInfo().values()) {
			// se si condivide tutto il servizio o se la facet è di specifica 
			if(e.isShareAll() || (e.isSharing() && !facet.isAddInfo())) {
				getGenericProxy(e).add(facetSpec, facet);
			}
		}
	}

	public GenericProxy getGenericProxy(ElementExtraInfo e) {
		try {
			if(ctx == null) ctx = new InitialContext();
			GenericProxy genericProxy = ((GenericProxy)ctx.lookup(e.getNotifier()));
			return genericProxy;
		} catch (NamingException exc) {
			log.error("Warning: cannot retrieve the federation proxy of type \"" + e.getNotifier() + "\":" + exc.getMessage());
			return null;
		}
	}

	public void postInsert(FacetXmlEnt xml) {
		FacetEnt facet = xml.getFacet();
		FacetSpecXML dxml;

		try {
			// This should be refactored ;)
			dxml = registry.getFacet(xml.getFacet().getElemPK().getId()).getFacetSpecificationXML();
		} catch (RemoteException e) {
			log.debug("Exception raised while talking with the registry: " + e.toString());
			return;
		} catch (NotFoundException e) {
			log.debug("Exception raised while talking with the registry: " + e.toString());
			return;
		}

		// La facet è aggiuntiva ed è stata promossa a parte rispetto al servizio
		for(FederatedPromotion prom: facet.getFederatedPromotion()) {
			fedCoord.getProxy(prom.getFederation().getMethod()).addFacetXml(prom, dxml, xml.getFacet().getTimestamp(), xml.getFacet().getElemPK().getId());
		}

		// la facet è di specifica oppure il servizio condivide tutte le facet
		// In questi caso non c'è la promozione della facet
		for(FederatedPromotion prom: facet.getService().getFederatedPromotion()) {
			if(!facet.isAddInfo() || prom.isShareAll())
				fedCoord.getProxy(prom.getFederation().getMethod()).addFacetXml(prom, dxml, xml.getFacet().getTimestamp(), xml.getFacet().getElemPK().getId());
		}

		for(ElementExtraInfo ei : facet.getExtraInfo().values()) {
			if(ei.isShareAll()) getGenericProxy(ei).add(dxml, xml);
		}

		for(ElementExtraInfo ei : facet.getService().getExtraInfo().values()) {
			if((!facet.isAddInfo() && ei.isSharing()) || ei.isShareAll()) getGenericProxy(ei).add(dxml, xml);
		}
	}

	public FederatedService add(FederationEnt federation, String serviceid) {
		//This method is called only if the service is already in the registry.		
		ServiceEnt sent=em.find(ServiceEnt.class, new ElementEntPK(serviceid, ServiceEnt.SERVICE_ENT));
		if (sent==null) {
			//error
			log.debug("Service with id  " +serviceid + "should be in registry but it is not");
			log.error("Could not lookup service entity with id " + serviceid);
			return null;
		}

		FederatedService service=FederatedService.search(em,sent,federation);
		if (service==null) {
			//create;
			service = new FederatedService(federation,sent,null); //extra info will be added by caller
			em.persist(service);
			em.flush();
		} 
		return service;
	}

	public FederatedFacet add(FederationEnt federation, String serviceid, String facetId, boolean addInfo) {
		log.debug("Adding facet " + facetId + "(additional:" + addInfo + "for service " + serviceid + " in federation " + federation);
		if (serviceid == null || facetId == null || federation == null) {
			log.warn("Service, facet or federation is null, ignoring");
			return null;
		}

		FacetEnt facent=em.find(FacetEnt.class, new ElementEntPK(facetId, FacetEnt.FACET_ENT));
		if (facent==null) {
//			error
			log.debug("Facet with id  " +facetId + "should be in registry but it is not");
			log.error("Could not lookup facet entity with id " + facetId);
			return null;
		}

		FederatedFacet fedfacet=FederatedFacet.search(em,facent,federation);
		FederatedService fserv=lookup(serviceid,federation);
		if (fserv==null) {
			log.error("Could not lookup federated service " + serviceid + ". Aborting facet add");
			return null;
		}

		if (fedfacet==null) {
			//create
			fedfacet=new FederatedFacet(fserv,federation,facent,null); //extra info will be added by caller
			fserv.getFacets().add(fedfacet);
			em.persist(fedfacet);		
			em.flush();
		}
		return fedfacet;
	}

	public FederatedXml add(FederationEnt federation, String serviceid, String facetId, String facetXmlId) {
		log.debug("Adding xml " + facetXmlId +  " for facet " + facetId + " for service " + serviceid + " in federation " + federation);
		if (facetXmlId == null || facetId == null || serviceid == null || federation == null) {
			log.warn("xml, facet, service or federation null, ignoring");
			return null;
		}

		FacetXmlEnt xmlent=em.find(FacetXmlEnt.class,new ElementEntPK(facetXmlId, FacetXmlEnt.FACET_XML_ENT));
		if (xmlent==null) {
			//error
			log.debug("Facet xml with id  " +facetXmlId + "should be in registry but it is not");
			log.error("Could not lookup facet xml entity with id " + facetXmlId);
			return null;
		}

		FederatedFacet ffacet=retrieve(serviceid,facetId,federation, true);
		if (ffacet==null) {
			log.error("Could not lookup facet " + facetId + ", aborting insertion of xml " + facetXmlId);
			return null;
		}

		FederatedXml fedxml=FederatedXml.search(em,xmlent,federation);		
		if (fedxml==null) {
			//create
			fedxml=new FederatedXml(federation,xmlent,null);  //extra info will be added by caller
			fedxml.setFacet(ffacet);
			ffacet.setXml(fedxml);

			em.persist(fedxml);
			em.flush();
		}

		return fedxml;
	}

	public DService getServiceData(String serviceid) {
		log.debug("Getting service data for service " + serviceid);
		if (serviceid==null) {
			log.warn("Service id is null, ignoring");
			return null;
		}

		//x crearli usare la add() (controllare se ci sono nel registro e poi creare gli ent)
		try {
			DService service=registry.getService(serviceid);			
			ServiceEnt servent=em.find(ServiceEnt.class,new ElementEntPK(serviceid, ServiceEnt.SERVICE_ENT));
			if (servent==null) {
				//create
				servent=new ServiceEnt(serviceid,registry.isServiceLocallyCreated(serviceid),service.isAllowAdditionalInformation());
				em.persist(servent);
				em.flush();
				postInsert(servent);
			}
			Collection<FacetSpec> fspec=registry.getSpecificationFacets(serviceid);
			if (fspec!=null) {
				for (FacetSpec f: fspec) {					
					String facetid=f.getSchemaID();
					service.addSpecType(f);
					FacetEnt facetent=em.find(FacetEnt.class,new ElementEntPK(facetid, FacetEnt.FACET_ENT));
					if (facetent == null) {
						facetent = new FacetEnt(facetid,servent,false,registry.isFacetLocallyCreated(facetid),Calendar.getInstance().getTime());						
						em.persist(facetent);						
						postInsert(facetent);
					}
					//Set the dm timestamp
					f.setDmTimestamp(facetent.getTimestamp());
					//XML
					FacetSpecXML facetxml=f.getFacetSpecificationXML();
					if (facetxml!=null) {
						String facetxmlid=facetxml.getXmlID();
						FacetXmlEnt xmlent=em.find(FacetXmlEnt.class,new ElementEntPK(facetxmlid, FacetXmlEnt.FACET_XML_ENT));
						if (xmlent==null) {
							xmlent=new FacetXmlEnt(facetent,facetxmlid);							
							em.persist(xmlent);							
							facetent.setXml(xmlent);
							postInsert(xmlent);
						}
					}
				}
			}

			return service;
		} catch (RemoteException e) {
			log.debug("Unable to communicate with registry");
		} catch (NotFoundException e) {
			log.debug("Service not found");			
		}
		return null;

	}

	public FacetSpec getFacetSpecificationData(String serviceid, String facetschemaid) {
		log.debug("Getting facet specification data for facet " + facetschemaid + " for service " + serviceid);
		if (facetschemaid==null || serviceid==null) {
			log.warn("Facet or serviceid is null, ignoring");
			return null;
		}
		//creare sia facetEnt che facetXmlEnt
		try {
			FacetSpec spec=registry.getFacet(facetschemaid);
			FacetEnt ent=em.find(FacetEnt.class,new ElementEntPK(facetschemaid, FacetEnt.FACET_ENT));
			if (ent==null) {
				//Check if service exists, otherwise return null
				ServiceEnt servent=add(serviceid);				
				if (servent==null) return null;		

				//create facet and xml
				ent=new FacetEnt(facetschemaid,servent,spec instanceof FacetAddInfo ? true : false,registry.isFacetLocallyCreated(facetschemaid),new Date());				

				ent.setService(servent);
				em.persist(ent);

				//"Always link both sides...."				
				servent.addFacet(ent);
				em.flush();

				postInsert(ent);

				createFacetXMLEnt(spec,ent);				
			}

			//Set deliverymanager timestamp
			spec.setDmTimestamp(ent.getTimestamp());

			return spec;
		} catch (RemoteException e) {
			log.debug("Unable to communicate with registry");
		} catch (NotFoundException e) {
			log.debug("Facet not found");		
		}			

		return null;
	}

	public FacetAddInfo getFacetAdditionalData(String serviceid, String facetschemaid) {
		log.debug("Getting facet specification data for additional facet " + facetschemaid + " for service " + serviceid);
		if (facetschemaid==null || serviceid==null) {
			log.warn("Facet or serviceid is null, ignoring");
			return null;
		}
		//creare sia facetEnt che facetXmlEnt
		try {
			FacetSpec spec=registry.getFacet(facetschemaid);
			if (!(spec instanceof FacetAddInfo)) {
				log.debug("Looking for Facet Additional info but registry returned specification facet");
				return null;
			}
			FacetEnt ent=em.find(FacetEnt.class,new ElementEntPK(facetschemaid, FacetEnt.FACET_ENT));
			if (ent==null) {
				//Check if service exists, otherwise return null
				ServiceEnt servent=add(serviceid);				
				if (servent==null) return null;		

				//create facet and xml
				ent=new FacetEnt(facetschemaid,servent,spec instanceof FacetAddInfo ? true : false,registry.isFacetLocallyCreated(facetschemaid),new Date());				

				ent.setService(servent);
				em.persist(ent);

				//"Always link both sides...."				
				servent.addFacet(ent);
				em.flush();

				postInsert(ent);

				createFacetXMLEnt(spec, ent);										
			}

			//Set deliverymanager timestamp
			spec.setDmTimestamp(ent.getTimestamp());

			return (FacetAddInfo)spec;
		} catch (RemoteException e) {
			log.debug("Unable to communicate with registry");
		} catch (NotFoundException e) {
			log.debug("Facet not found");		
		}			

		return null;
	}

	private void createFacetXMLEnt(FacetSpec spec, FacetEnt ent) {
		log.debug("Creating facet xml for facet " + ent + " using facet specification data "+ spec);
		if (spec==null || ent ==null) {
			log.warn("Facet data or ent is null, ignoring");
			return;
		}
		FacetSpecXML facet_xml=spec.getFacetSpecificationXML();
		if (facet_xml!=null)  {
			//create Xml ent	
			//dubbio: devo controllare se c'e' gia'?
			FacetXmlEnt xmlent= new FacetXmlEnt(ent,facet_xml.getXmlID());
			// "Always link both sides...."
			ent.setXml(xmlent);
			xmlent.setFacet(ent);
			em.persist(xmlent);
			em.flush();
			postInsert(xmlent);
		}
	}

	public FacetSpecXML getFacetXmlData(String serviceid, String facetschemaid) {
		log.debug("Getting facet xml data for service " + serviceid + " and facetschemaid " + facetschemaid);
		if (serviceid==null || facetschemaid==null) {
			log.warn("Serviceid or facetschemaid is null, ignoring");
			return null;
		}
		try {
			FacetSpec spec=registry.getFacet(facetschemaid);
			if (spec==null) return null;
			if (spec.getFacetSpecificationXML()==null) return null;

			FacetEnt facetent=em.find(FacetEnt.class,new ElementEntPK(facetschemaid, FacetEnt.FACET_ENT));
			if (facetent==null) return null;

			FacetXmlEnt xmlent=em.find(FacetXmlEnt.class,new ElementEntPK(spec.getFacetSpecificationXML().getXmlID(),FacetXmlEnt.FACET_XML_ENT));
			if (xmlent==null) {
				//do not create: creation is only allowed by updating the timestamp
				log.warn("FacetXmlEnt does not exist, but should be here");
				return null;
			}

			return spec.getFacetSpecificationXML();

		} catch (RemoteException e) {
			log.debug("Unable to communicate with registry");
		} catch (NotFoundException e) {
			log.debug("Facet not found");		
		}
		return null;
	}


	public boolean isPromotedWithFacets(String serviceid, String federationid) {		
		FederatedPromotion fp=getPromotion(serviceid,federationid);
		if (fp!=null && fp.isShareAll()) {		
			return true;
		}

		return false;
	}


	public boolean isPromoted(String serviceid, String federationid) {
		FederatedPromotion fp=getPromotion(serviceid,federationid);
		if (fp!=null) {		
			return true;
		}

		return false;
	}

	public boolean isPromotedWithoutFacets(String serviceid, String federationid) {
		FederatedPromotion fp=getPromotion(serviceid,federationid);
		if (fp!=null && !(fp.isShareAll())) {
			return true;
		}
		return false;
	}

	public boolean isFacetPromoted(String serviceid, String facetid, boolean addInfo, FederationEnt fed) {		
		FacetEnt fent=em.find(FacetEnt.class, new ElementEntPK(facetid, FacetEnt.FACET_ENT));
		if (fent==null) return false;

		FederatedPromotion fedprom=FederatedPromotion.search(em,fent,fed);
		if (fedprom!=null) return true;

		//check if service is promoted with all facets
		ServiceEnt sent=em.find(ServiceEnt.class,new ElementEntPK(serviceid, ServiceEnt.SERVICE_ENT));
		if (sent==null) return false;

		fedprom=FederatedPromotion.search(em,sent,fed);
		if (fedprom!=null && (fedprom.isShareAll() || !addInfo))
			return true;

		return false;
	}

	private FederatedPromotion getPromotion(String serviceid,String federationid) {
		//Search service;
		ServiceEnt sent=em.find(ServiceEnt.class,new ElementEntPK(serviceid, ServiceEnt.SERVICE_ENT));
		if (sent==null) return null;

		FederationEnt fedent=em.find(FederationEnt.class,federationid);		
		if (fedent==null) return null;

		FederatedPromotion fp=FederatedPromotion.search(em, sent,fedent);
		return fp;
	}

	public Collection<FacetAddInfo> getFacetAdditionalInfo(String serviceid) {
		log.debug("Getting facet additional info for service " +serviceid );
		if (serviceid == null) {
			log.warn("Service is null, ignoring");
			return new Vector<FacetAddInfo>();
		}

		try {
			Collection<FacetAddInfo> addinfo=registry.getAdditionalInformationFacets(serviceid);
			//create info in the db
			if (addinfo!=null) {
				for (FacetAddInfo addfac: addinfo) {
					FacetEnt ent=em.find(FacetEnt.class,new ElementEntPK(addfac.getSchemaID(), FacetEnt.FACET_ENT));
					if (ent==null) {
						//create

						ServiceEnt serv=add(serviceid);
						String facetid=addfac.getSchemaID();
						ent=new FacetEnt(facetid,serv,true,registry.isFacetLocallyCreated(facetid),new Date());
						em.persist(ent);
						em.flush();
					}

					//set the Dm timestamp
					addfac.setDmTimestamp(ent.getTimestamp());
				}
				return addinfo;
			}

		} catch (RemoteException e) {
			log.debug("Unable to communicate with registry");
		} catch (NotFoundException e) {
			log.debug("Service with id" + serviceid + " not found");		
		}

		return new Vector<FacetAddInfo>();
	}

	/* Implementato da Antonio */
	public FederatedService retrieve(String serviceId, FederationEnt federation, boolean received) {
		FederatedService fserv=lookup(serviceId,federation);

		if (fserv==null) {
			ServiceEnt ent=add(serviceId);
			if (ent == null) {
				log.error("Could not create service ent");
				return null;				
			}
			fserv=new FederatedService(federation,ent,null);
			fserv.setReceived(received);
			em.persist(fserv);
			em.flush();
			log.debug("Created new federated service " + serviceId + " in federation " + federation.getId());
		} 
		return fserv;
	}

	/* Implementato da Antonio */
	public FederatedFacet retrieve(String serviceId, String facetId, FederationEnt federation, boolean received) {
		FederatedFacet ffacet=lookup(serviceId,facetId,federation);
		if (ffacet==null) {
			//create
			FacetEnt fent=add(serviceId,facetId);
			if (fent==null) {
				log.error("Could not create facet ent");
				return null;	
			}
			FederatedService fedserv=retrieve(serviceId,federation,received);
			if (fedserv==null) {
				log.error("Could not lookup federated service, aborting");
				return null;
			}

			ffacet = new FederatedFacet(fedserv,federation,fent,null);
			em.persist(ffacet);
			em.flush();
			log.debug("Created new federated facet " + facetId + " in federation " + federation.getId());
		}
		return ffacet;
	}

	public FederatedXml retrieve(String serviceId, String facetId, String facetXmlId, FederationEnt federation) {
		FederatedXml fxml=lookup(serviceId,facetId,facetXmlId,federation);
		if (fxml==null) {
			//create
			FacetXmlEnt xent=add(serviceId,facetId,facetXmlId);
			if (xent==null) {
				log.error("Could not create xml ent");
				return null;	
			}
			fxml=new FederatedXml(federation,xent,null);
			FederatedFacet fedfacet=retrieve(serviceId,facetId,federation, true);
			fxml.setFacet(fedfacet);
			em.persist(fxml);
			em.flush();
			log.debug("Created new federated xml " + facetXmlId + " in federation " + federation.getId());
		}
		return fxml;
	}

	public void deleteFromPlugin(ElementEnt elementEnt){
		
			//remove federated service
			int fp = elementEnt.getFederatedPromotion().size();
			int aei = elementEnt.getAliveExtraInfo(em);
			int afp = elementEnt.getAliveFederatedElements();
			if(fp==0 && aei==0 && afp==0){
				if(!elementEnt.isOwnership()) try{
					registry.removeService(elementEnt.getElemPK().getId());
				}catch (Exception e) {
					e.printStackTrace();
				}
				em.remove(elementEnt);
			}
		
	}
	
	public void deleteFromUpdate(ServiceEnt servEnt){
		log.debug("Deleting service " + servEnt.toString());
		if (servEnt==null) {
			log.warn("Service is null, ignoring ");
			return;
		}
		preDelete(servEnt);

		try {
			em.remove(servEnt);
			em.flush();
		} catch (Throwable exc) {
			log.warn("Exception raised from the registry: " + servEnt.toString());
		}
	}
	
	public void deleteFromUpdate(FacetEnt facetEnt){
		log.debug("Deleting facet " +facetEnt.toString() );
		if (facetEnt==null) {
			log.warn("Facet is null, ignoring");
			return;
		}	
		preDelete(facetEnt);

		try {
			em.remove(facetEnt);
			em.flush();
		} catch (Throwable exc) {
			log.warn("Exception raised from the registry: " + facetEnt.toString());
		}
	}
	
	public void deleteFromUpdate(FacetXmlEnt facetXmlEnt){
		log.debug("Deleting facet " +facetXmlEnt.toString() );
		
		if (facetXmlEnt ==null) {
			log.warn("Facet xml is null, ignoring");
			return;
		}	
		preDelete(facetXmlEnt);

		try {
			facetXmlEnt.getFacet().setXml(null);
			em.remove(facetXmlEnt);
			em.flush();
		} catch (Throwable exc) {
			log.warn("Exception raised from the registry: " + facetXmlEnt.toString());
		}
	}
}
