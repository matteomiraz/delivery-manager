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

package eu.secse.deliveryManager.federations.pubSubRep.core;

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

import polimi.reds.MessageID;
import eu.secse.deliveryManager.core.ModelManager;
import eu.secse.deliveryManager.data.ElementEnt;
import eu.secse.deliveryManager.data.FacetEnt;
import eu.secse.deliveryManager.data.FacetXmlEnt;
import eu.secse.deliveryManager.data.FederatedFacet;
import eu.secse.deliveryManager.data.FederatedPromotion;
import eu.secse.deliveryManager.data.FederatedService;
import eu.secse.deliveryManager.data.FederationEnt;
import eu.secse.deliveryManager.data.FederationExtraInfo;
import eu.secse.deliveryManager.data.ServiceEnt;
import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.federations.pubSubRep.data.PubSubReplyFederationExtraInfo;
import eu.secse.deliveryManager.federations.pubSubRep.reds.PSRDeletion;
import eu.secse.deliveryManager.federations.pubSubRep.reds.PSRQuery;
import eu.secse.deliveryManager.federations.pubSubRep.reds.PSRResponse;
import eu.secse.deliveryManager.interest.InterestFederation;
import eu.secse.deliveryManager.model.DFederation;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.model.FacetAddInfo;
import eu.secse.deliveryManager.model.FacetSpec;
import eu.secse.deliveryManager.model.FacetSpecXML;
import eu.secse.deliveryManager.reds.Envelope;
import eu.secse.deliveryManager.reds.InterestEnvelope;
import eu.secse.deliveryManager.registry.IRegistryProxy;

@Stateless
public class PsrProxy implements IPsrProxy{

	private static final Log log = LogFactory.getLog(IPsrProxy.class);

	@PersistenceContext(unitName="deliveryManager")
	private EntityManager em;

	@EJB private IRegistryProxy registry;
	@EJB private ModelManager modelManager;
	
	public void addFacetAddInfo(FederatedPromotion prom, FacetAddInfo facetAddInfo) {
		// publishes the  DFederationMessage with FacetAddInfo
		send(prom, facetAddInfo);
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
	}

	public void delete(FederatedPromotion prom, ElementEnt deletedElement) {
		IPsrMBean pubSubMBean = null;
		try {
			MBeanServer server = MBeanServerLocator.locate();
			pubSubMBean =(IPsrMBean)MBeanProxyExt.create(IPsrMBean.class, "DeliveryManager:service=pubSubRepFederationProxy", server);			
		} catch (MalformedObjectNameException e) {			
			log.error(e.getMessage());	      		    
		}	
		
		if(deletedElement instanceof ServiceEnt){
			log.info("ServiceEnt "+ ((ServiceEnt)deletedElement).getElemPK().getId() + " deleted");
			if(pubSubMBean != null) pubSubMBean.publish(new PSRDeletion(((ServiceEnt)deletedElement).getElemPK().getId()));
		}
		if(deletedElement instanceof FacetEnt){
			log.info("FacetEnt "+ ((FacetEnt)deletedElement).getElemPK().getId() + " deleted");
			if(pubSubMBean != null) pubSubMBean.publish(new PSRDeletion(((FacetEnt)deletedElement).getElemPK().getId(), ((FacetEnt)deletedElement).getService().getElemPK().getId()));
		}
		if(deletedElement instanceof FacetXmlEnt){
			log.info("FacetXmlEnt "+ ((FacetXmlEnt)deletedElement).getElemPK().getId() + " deleted");
		}
	}

	public void dismissFederation(FederationEnt federation) {
		leave(federation);
		// TODO: delete all promoted elements
	}

	public Map<String, String> getFederationCreationOptions(String federationid) {
		return new HashMap<String,String>();
	}

	@SuppressWarnings("unchecked")
	public void initialize() {
	
		// Called when booting the server. Must implement resubscription actions , listener intialization, and so on
		//Listener initialization
       
		
		// Lookup federations joined with the gossip method
		Query q=em.createNamedQuery(FederationEnt.FIND_PUBSUBREP);
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
			PubSubReplyFederationExtraInfo fedExtraInfo = new PubSubReplyFederationExtraInfo(federationFilter);			
			
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
			IPsrMBean pubSubMBean =(IPsrMBean)MBeanProxyExt.create(IPsrMBean.class, "DeliveryManager:service=pubSubRepFederationProxy", server);			
			pubSubMBean.subscribe(federationFilter);
			
			// send the query
			pubSubMBean.publishRepliable(new DFederation(federation.getId(), new PSRQuery()));
		} catch (MalformedObjectNameException e) {			
			log.error(e.getMessage());	      		    
		}	
	}

	public void leave(FederationEnt federation) {
		MBeanServer server = MBeanServerLocator.locate();
		try {
			IPsrMBean pubSubMBean =(IPsrMBean)MBeanProxyExt.create(IPsrMBean.class, "DeliveryManager:service=pubSubRepFederationProxy", server);			
			pubSubMBean.unsubscribe(((PubSubReplyFederationExtraInfo)federation.getExtraInfo()).getFederationFilter());					
		} catch (MalformedObjectNameException e) {			
			log.error(e.getMessage());	      		    
		}	  

	}

	public void stop() {
	}
	
	public void send(FederatedPromotion fedPromotion, FacetAddInfo facetAddInfo){
		// lookup federation coordination manager and refresh
		MBeanServer server = MBeanServerLocator.locate();
		try {
			IPsrMBean pubSubMBean =(IPsrMBean)MBeanProxyExt.create(IPsrMBean.class, "DeliveryManager:service=pubSubRepFederationProxy", server);			
			pubSubMBean.publish(new DFederation(fedPromotion.getFederation().getId(), facetAddInfo));
			
		} catch (MalformedObjectNameException e) {			
			log.error(e.getMessage());	      		    
		}	
		
	}
	
	public void send(FederatedPromotion fedPromotion, DService dService) {
//		lookup federation coordination manager and refresh
		MBeanServer server = MBeanServerLocator.locate();

		try {
			IPsrMBean pubSubMBean =(IPsrMBean)MBeanProxyExt.create(IPsrMBean.class, "DeliveryManager:service=pubSubRepFederationProxy", server);			
			pubSubMBean.publish(new DFederation(fedPromotion.getFederation().getId(), dService));
			
			if(fedPromotion.isShareAll()){
//				add additional facets
				log.info("Sending Additional Facet for service "+dService.getServiceID());
				Collection<FacetAddInfo> facetAddInfo = modelManager.getFacetAdditionalInfo(dService.getServiceID());
				if(facetAddInfo!=null){
					for (FacetAddInfo s : facetAddInfo) {
						pubSubMBean.publish(new DFederation(fedPromotion.getFederation().getId(), s));
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
			modelManager.add(fed, federatedService);
			
			Collection<FacetSpec> facetSpecs = federatedService.getSpecType();
			if(facetSpecs!=null){
				for(FacetSpec f: facetSpecs){
					ServiceEnt se = null;
					try{ 
						se = ServiceEnt.searchByID(em, federatedService.getServiceID());
					}catch(NotFoundException e){
						log.error("ServiceEnt "+federatedService.getServiceID()+" not added");
					}
					modelManager.add(fed, f, se);
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
			 modelManager.add(fed, federatedFacetSpec);
			
		}else{
			log.warn("Cannot find FederatedService of the service "+ federatedFacetSpec.getServiceID()+" in the Fedearation "+federationId);
		}

	}
	
	private void restartSubscribe(String federationId){
		InterestEnvelope federationFilter = new InterestEnvelope(new InterestFederation(federationId), registry.getRegistryId());
		//lookup federation coordination manager and refresh
		MBeanServer server = MBeanServerLocator.locate();

		try {
			IPsrMBean pubSubMBean =(IPsrMBean)MBeanProxyExt.create(IPsrMBean.class, "DeliveryManager:service=pubSubRepFederationProxy", server);			
			pubSubMBean.subscribe(federationFilter);					
		} catch (MalformedObjectNameException e) {			
			log.error(e.getMessage());	      		    
		}	  

	}
	
	public void receivedDeletion(String federationId, PSRDeletion deletion) {
		//controls if it can find FederationEnt
		FederationEnt fed = this.em.find(FederationEnt.class, federationId);
		if(fed == null) {
			log.warn("Not in the federation " + federationId);
			return;
		}

		if(deletion.isService()) {
			FederatedService fedSrv = modelManager.lookup(deletion.getServiceId(), fed);
			em.remove(fedSrv);
		} else {
			FederatedFacet fedFacet = modelManager.lookup(deletion.getServiceId(), deletion.getFacetId(), fed);
			em.remove(fedFacet);
		}
	}
	
	public void receivedResponse(String federationId, PSRResponse response) {
		log.info("Received response to my initial query -- federation " + federationId);
		for (Deliverable d : response.getElems()) {
			if (d instanceof DService) {
				this.received(federationId, (DService) d);
			} else if (d instanceof FacetAddInfo) {
				this.received(federationId, (FacetAddInfo) d);
			}
		}
	}
	
	public void receivedQuery(String federationId, MessageID messageID) {
		//controls if it can find FederationEnt
		FederationEnt fed = this.em.find(FederationEnt.class, federationId);
		if(fed == null) {
			log.warn("Not in the federation " + federationId);
			return;
		}
		
		PSRResponse resp = new PSRResponse();
		
		for (FederatedPromotion p : fed.getPromotions()) {
			ElementEnt element = p.getElement();
			if (element instanceof ServiceEnt) {
				String serviceId = ((ServiceEnt) element).getElemPK().getId();
				DService dsrv = modelManager.getServiceData(serviceId);
				resp.add(dsrv);
				
				if(p.isShareAll())
					resp.addFacets(modelManager.getFacetAdditionalInfo(serviceId));
				
			} else if (element instanceof FacetEnt) {
				FacetEnt facet = (FacetEnt) element;
				String facetId = facet.getElemPK().getId();
				
				FacetAddInfo dfacet = modelManager.getFacetAdditionalData(facet.getService().getElemPK().getId(), facetId);
				resp.add(dfacet);
			} 
		}

		try {
			MBeanServer server = MBeanServerLocator.locate();
			IPsrMBean pubSubMBean =(IPsrMBean)MBeanProxyExt.create(IPsrMBean.class, "DeliveryManager:service=pubSubRepFederationProxy", server);			

			pubSubMBean.reply(new Envelope(new DFederation(federationId,resp)), messageID);
		} catch (MalformedObjectNameException e) {			
			log.error(e.getMessage());	      		    
		}	  

				
	}
}
