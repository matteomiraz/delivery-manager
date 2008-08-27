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

package eu.secse.deliveryManager.federations.pubsubscribe.core;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.mx.util.MBeanProxyExt;
import org.jboss.mx.util.MBeanServerLocator;

import eu.secse.deliveryManager.core.ModelManager;
import eu.secse.deliveryManager.data.ElementEnt;
import eu.secse.deliveryManager.data.FacetEnt;
import eu.secse.deliveryManager.data.FacetXmlEnt;
import eu.secse.deliveryManager.data.FederatedElement;
import eu.secse.deliveryManager.data.FederatedFacet;
import eu.secse.deliveryManager.data.FederatedPromotion;
import eu.secse.deliveryManager.data.FederatedService;
import eu.secse.deliveryManager.data.FederationEnt;
import eu.secse.deliveryManager.data.FederationExtraInfo;
import eu.secse.deliveryManager.data.ServiceEnt;
import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.federations.pubsubscribe.data.PubSubFedElemExtraInfo;
import eu.secse.deliveryManager.federations.pubsubscribe.data.PubSubFederationExtraInfo;
import eu.secse.deliveryManager.federations.pubsubscribe.data.PubSubPromotionExtraInfo;
import eu.secse.deliveryManager.interest.InterestFederation;
import eu.secse.deliveryManager.model.DFederationPlainMessage;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.FacetAddInfo;
import eu.secse.deliveryManager.model.FacetSpec;
import eu.secse.deliveryManager.model.FacetSpecXML;
import eu.secse.deliveryManager.reds.InterestEnvelope;
import eu.secse.deliveryManager.registry.IRegistryProxy;
import eu.secse.deliveryManager.timeout.ILeaseManager;
import eu.secse.deliveryManager.timeout.LeaseExtraInfo;


@Stateless
public class PubSubProxy implements IPubSubProxy{

	private static final Log log = LogFactory.getLog(IPubSubProxy.class);

	@PersistenceContext(unitName="deliveryManager")
	private EntityManager em;

	@EJB private IRegistryProxy registry;
	
	@EJB private ILeaseManager iLease;
	
	@EJB private ModelManager modelManager;

	public void addFacetAddInfo(FederatedPromotion prom, FacetAddInfo facetAddInfo) {
		// publishes the  DFederationMessage with FacetAddInfo
		send(prom, facetAddInfo);
		
//		set initial renew
		PubSubPromotionExtraInfo promExtraInfo = new PubSubPromotionExtraInfo();
		promExtraInfo.setTimeout(iLease.getInitialRenew());
		prom.setExtraInfo(promExtraInfo);
		promExtraInfo.setProm(prom);
		em.persist(promExtraInfo);
		em.flush();
	}

	public void addFacetSpec(FederatedPromotion prom, FacetSpec facetSpecification) {
	
		DService service = modelManager.getServiceData(((ServiceEnt)prom.getElement()).getElemPK().getId());
//		
		this.addService(prom, service);

	}

	public void addFacetXml(FederatedPromotion prom, FacetSpecXML xml, Date dmTimestamp, String facetSchemaId) {
		if (prom.getElement() instanceof FacetEnt) {
			FacetEnt facetEnt = (FacetEnt)prom.getElement();
			FacetAddInfo facetAddInfo = modelManager.getFacetAdditionalData(facetEnt.getService().getElemPK().getId(),facetSchemaId);
			this.addFacetAddInfo(prom, facetAddInfo);
		}
		if (prom.getElement() instanceof ServiceEnt) {
			ServiceEnt serviceEnt = (ServiceEnt)prom.getElement();
			FacetSpec facetSpec = modelManager.getFacetSpecificationData(serviceEnt.getElemPK().getId(), facetSchemaId);
			this.addFacetSpec(prom, facetSpec);
			
		}
	}

	public void addService(FederatedPromotion prom, DService serv) {
		
		//send service
		send(prom, serv);
		
		//set initial renew
		PubSubPromotionExtraInfo promExtraInfo = new PubSubPromotionExtraInfo();
		promExtraInfo.setTimeout(iLease.getInitialRenew());
		promExtraInfo.setProm(prom);
		prom.setExtraInfo(promExtraInfo);
		em.persist(promExtraInfo);
		em.flush();
	}

	public void delete(FederatedPromotion prom, ElementEnt elementoCancellato) {
		if(elementoCancellato instanceof ServiceEnt){
			log.info("ServiceEnt "+ ((ServiceEnt)elementoCancellato).getElemPK().getId() + " deleted");
		}
		if(elementoCancellato instanceof FacetEnt){
			log.info("FacetEnt "+ ((FacetEnt)elementoCancellato).getElemPK().getId() + " deleted");
		}
		if(elementoCancellato instanceof FacetXmlEnt){
			log.info("FacetXmlEnt "+ ((FacetXmlEnt)elementoCancellato).getElemPK().getId() + " deleted");
		}
		}

	public void dismissFederation(FederationEnt federation) {
		leave(federation);

	}

	public Map<String, String> getFederationCreationOptions(String federationid) {
		return new HashMap<String,String>();
	}

	@SuppressWarnings("unchecked")
	public void initialize() {
	
		// Called when booting the server. Must implement resubscription actions , listener intialization, and so on
		//Listener initialization
       
		
		// Lookup federations joined with the gossip method
		Query q=em.createNamedQuery(FederationEnt.FIND_PUBSUB);
		Collection<FederationEnt> pubsub_federations=q.getResultList();
		//Resubscribe
		for (FederationEnt fed:pubsub_federations) {				
			restartSubscribe(fed.getId());			
		}

	}



	public void join(FederationEnt federation, Map<String, String> options) {
		log.debug("Joining federation " + federation.getId()); 
		
		InterestFederation interestFederation = new InterestFederation(federation.getId());
		InterestEnvelope federationFilter = new InterestEnvelope(interestFederation, registry.getRegistryId());
		
		
		/*	Create the federation data, this data containes the filter on which delivery manager
		 must subscribe */
	
		FederationExtraInfo info=federation.getExtraInfo();
		if (info == null) {
			log.debug("Creating Extra information for the federation with id " + federation.getId());
			PubSubFederationExtraInfo fedExtraInfo = new PubSubFederationExtraInfo(federationFilter);			
			
			federation.setExtraInfo(fedExtraInfo);			
			fedExtraInfo.setFederation(federation); 
			em.persist(fedExtraInfo);
			em.flush();
		} else {
			log.debug("Federation info already existing");
		}
//		lookup federation coordination manager and refresh
		MBeanServer server = MBeanServerLocator.locate();

		try {
			IPubSubProxyMBean pubSubMBean =(IPubSubProxyMBean)MBeanProxyExt.create(IPubSubProxyMBean.class, "DeliveryManager:service=pubSubFederationProxy", server);			
			pubSubMBean.subscribe(federationFilter);					
		} catch (MalformedObjectNameException e) {			
			log.error(e.getMessage());	      		    
		}	
	}

	public void leave(FederationEnt federation) {
		MBeanServer server = MBeanServerLocator.locate();
		try {
			IPubSubProxyMBean pubSubMBean =(IPubSubProxyMBean)MBeanProxyExt.create(IPubSubProxyMBean.class, "DeliveryManager:service=pubSubFederationProxy", server);			
			pubSubMBean.unsubscribe(((PubSubFederationExtraInfo)federation.getExtraInfo()).getFederationFilter());					
		} catch (MalformedObjectNameException e) {			
			log.error(e.getMessage());	      		    
		}	  

	}

	public void stop() {
		/* Nothing to do */
	}
	
	public void send(FederatedPromotion fedPromotion, FacetAddInfo facetAddInfo){
		PubSubPromotionExtraInfo promExtraInfo = new PubSubPromotionExtraInfo();
		promExtraInfo.setTimeout(iLease.getStandardRenew());
		promExtraInfo.setProm(fedPromotion);
		fedPromotion.setExtraInfo(promExtraInfo);
		em.persist(promExtraInfo);
		em.flush();
		
		facetAddInfo.setInfo(iLease.getLease(fedPromotion.getElement()));
		
		// lookup federation coordination manager and refresh
		MBeanServer server = MBeanServerLocator.locate();
		try {
			IPubSubProxyMBean pubSubMBean =(IPubSubProxyMBean)MBeanProxyExt.create(IPubSubProxyMBean.class, "DeliveryManager:service=pubSubFederationProxy", server);			
			pubSubMBean.publish(new DFederationPlainMessage(fedPromotion.getFederation().getId(), facetAddInfo));
			
		} catch (MalformedObjectNameException e) {			
			log.error(e.getMessage());	      		    
		}	
		
	}
	
	public void send(FederatedPromotion fedPromotion, DService dService) {
		
		//set standard renew
		PubSubPromotionExtraInfo promExtraInfo = new PubSubPromotionExtraInfo();
		promExtraInfo.setTimeout(iLease.getStandardRenew());
		promExtraInfo.setProm(fedPromotion);
		fedPromotion.setExtraInfo(promExtraInfo);
		em.persist(promExtraInfo);
		em.flush();
		
		dService.setInfo(iLease.getLease(fedPromotion.getElement()));
		
//		lookup federation coordination manager and refresh
		MBeanServer server = MBeanServerLocator.locate();

		try {
			IPubSubProxyMBean pubSubMBean =(IPubSubProxyMBean)MBeanProxyExt.create(IPubSubProxyMBean.class, "DeliveryManager:service=pubSubFederationProxy", server);			
			pubSubMBean.publish(new DFederationPlainMessage(fedPromotion.getFederation().getId(), dService));
			if(fedPromotion.isShareAll()){
//				add additional facets
				log.info("Sending Additional Facet for service "+dService.getServiceID());
				Collection<FacetAddInfo> facetAddInfo = modelManager.getFacetAdditionalInfo(dService.getServiceID());
				if(facetAddInfo!=null){
				for (FacetAddInfo s : facetAddInfo) {
					s.setInfo(iLease.getLease(fedPromotion.getElement()));
					pubSubMBean.publish(new DFederationPlainMessage(fedPromotion.getFederation().getId(), s));
				}
				}
			}
		} catch (MalformedObjectNameException e) {			
			log.error(e.getMessage());	      		    
		}	
	}
	
	/*
	 * Process the message that contains the promotion of a service without its additional facets
	 * PRECONDITIOND:
	 * FederationEnt exists && FederatedPromotion doesn't exist
	 * 
	 * (non-Javadoc)
	 * 
	 * @see eu.secse.deliveryManager.federations.pubsubscribe.core.IPubSubProxy#received(java.lang.String, eu.secse.deliveryManager.model.DService)
	 */
	public void received(String federationId, DService federatedService) {
		
		//controls if it can find FederationEnt
		FederationEnt fed = this.em.find(FederationEnt.class, federationId);
		if(fed == null) {
			log.warn("Not in the federation " + federationId);
			return;
		}
		
		//controls if it can't find FederatedPromotion
		
		if(!modelManager.isPromoted(federatedService.getServiceID(), federationId)){
			FederatedService fedService = modelManager.add(fed, federatedService);
			addExtraInfo(fedService, (Date)federatedService.getInfo());
			
			
			Collection<FacetSpec> facetSpecs = federatedService.getSpecType();
			if(facetSpecs!=null){
				for(FacetSpec f: facetSpecs){
					ServiceEnt se = null;
					try{ 
						se = ServiceEnt.searchByID(em, federatedService.getServiceID());
					}catch(NotFoundException e){
						log.error("ServiceEnt "+federatedService.getServiceID()+" not added");
					}
					FederatedFacet fedFacet = modelManager.add(fed, f, se);
					addExtraInfo(fedFacet, (Date) federatedService.getInfo());
				}
		}
			
		} else log.warn("The service "+federatedService.getServiceID()+ " has been promoted by this FederationProxy");
		 
		
		
		
}
	/*
	 * Process the message that contains the promotion of a service without its additional facets
	 * PRECONDITIOND:
	 * FederationEnt exists && FederatedPromotion must not exist
	 * 
	 * (non-Javadoc)
	 * 
	 * @see eu.secse.deliveryManager.federations.pubsubscribe.core.IPubSubProxy#received(java.lang.String, eu.secse.deliveryManager.model.DService)
	 */
	public void received(String federationId, FacetAddInfo federatedFacetSpec) {
		//controls if it can find FederationEnt
		FederationEnt fed = this.em.find(FederationEnt.class, federationId);
		if(fed == null) {
			log.warn("Not in the federation " + federationId);
			return;
		}
		//Controls if it can find FederatedService
		FederatedService fedService = modelManager.lookup(federatedFacetSpec.getServiceID(), fed);
		if(fedService!= null){
			 FederatedFacet fedFacet = modelManager.add(fed, federatedFacetSpec);
			 addExtraInfo(fedFacet, (Date)federatedFacetSpec.getInfo());
		}else{
			log.warn("Cannot find FederatedService of the service "+ federatedFacetSpec.getServiceID()+" in the Fedearation "+federationId);
		}

	}
	
	private void restartSubscribe(String federationId){
		InterestEnvelope federationFilter = new InterestEnvelope(new InterestFederation(federationId), registry.getRegistryId());
		//lookup federation coordination manager and refresh
		MBeanServer server = MBeanServerLocator.locate();

		try {
			IPubSubProxyMBean pubSubMBean =(IPubSubProxyMBean)MBeanProxyExt.create(IPubSubProxyMBean.class, "DeliveryManager:service=pubSubFederationProxy", server);			
			pubSubMBean.subscribe(federationFilter);					
		} catch (MalformedObjectNameException e) {			
			log.error(e.getMessage());	      		    
		}	  

	}
	
	
	private void addExtraInfo(FederatedElement fedElem, Date lease) {
		PubSubFedElemExtraInfo fedElemExtraInfo = (PubSubFedElemExtraInfo)fedElem.getExtraInfo();
		if(fedElemExtraInfo == null) {
			fedElemExtraInfo = new PubSubFedElemExtraInfo();
			fedElemExtraInfo.setTimeout(lease);
			fedElem.setExtraInfo(fedElemExtraInfo);
			fedElemExtraInfo.setElem(fedElem);
			em.persist(fedElemExtraInfo);
		}else{
			if(fedElemExtraInfo.getTimeout().before(lease))
				fedElemExtraInfo.setTimeout(lease);
		}

		LeaseExtraInfo leaseExtraInfo = (LeaseExtraInfo) fedElem.getElement().getExtraInfo().get(LeaseExtraInfo.INFO_TYPE);
		if(leaseExtraInfo == null) {
			leaseExtraInfo = new LeaseExtraInfo(fedElem.getElement(), lease);
			fedElem.getElement().getExtraInfo().put(LeaseExtraInfo.INFO_TYPE, leaseExtraInfo);
			em.persist(leaseExtraInfo);
			em.flush();
		} else{
			leaseExtraInfo.setLease(lease);
			em.flush();
		}

		em.flush();
	}
}
