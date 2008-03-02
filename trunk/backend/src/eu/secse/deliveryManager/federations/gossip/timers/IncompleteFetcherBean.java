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

package eu.secse.deliveryManager.federations.gossip.timers;

import java.util.Collection;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.TimerService;
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
import eu.secse.deliveryManager.federations.gossip.data.DeliveryManagerGossipInfo;
import eu.secse.deliveryManager.federations.gossip.data.IncompleteElement;
import eu.secse.deliveryManager.federations.gossip.data.IncompleteFacetSchema;
import eu.secse.deliveryManager.federations.gossip.data.IncompleteFacetXML;
import eu.secse.deliveryManager.federations.gossip.data.IncompleteService;
import eu.secse.deliveryManager.federations.gossip.messaging.IGossipMessagingManager;
import eu.secse.deliveryManager.federations.gossip.messaging.IMessageSender;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.DetailsRequest;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.FacetHeader;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.FacetXMLHeader;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.PromotionHeader;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.ServiceHeader;

/** Fetches incomplete elements
 * 
 * @author ercasta
 *
 */
@Stateless
public class IncompleteFetcherBean implements
		IncompleteFetcher, TimedObject{
	
	private static final Log log = LogFactory.getLog(IncompleteFetcher.class);
	
//	//TODO: (optional) Implement detection of unreceived details
//	private static final long timeout_millis=60*1000*5; //five minutes
//	
//	//TODO: (optional) implement max number of retry
//	private static final int max_retry=5;
//	
	private static final long retry_fetch=2*1000; //two seconds;
	

	private static final int max_parallel_fetch = 5; //at most 5 concurrent fetches
	
	private static final int max_retry = 3; // try to fetch an element this many times
	
	private IGossipMessagingManager gossip_messenger;
	
	@Resource
	private TimerService timer;
	
	@PersistenceContext(unitName="deliveryManager")
	private EntityManager manager;	
	
	@EJB 
	private ModelManager model_manager;
	
	@EJB
	private IMessageSender sender;
	
	
	public void queueElement(PromotionHeader header, DeliveryManagerGossipInfo from) {
		
		//boolean start_fetch=canFetch();
		
		IncompleteElement promotedelement=null;
		
	
		//Check if the incomplete element is already in the incomplete elements table		
		if (header instanceof ServiceHeader) {
			ServiceHeader serv_head=(ServiceHeader)header;		
			IncompleteService service=IncompleteService.getIncompleteService(manager,serv_head.getServiceid());
			if (service != null) {
				promotedelement=service;			
			} else {
				log.debug("Enqueueing service " + serv_head.getServiceid() + " to fetch from " + from.getAddress() + " in federation " + from.getFederationinfo().getFederation().getId());
				service=new IncompleteService(from,serv_head.getServiceid());
				from.getIncomplete().add(service);				
				promotedelement=service;
				service.setFetching(false);
				manager.persist(service);
				manager.flush();							
			}			
		} else if (header instanceof FacetHeader) {
			FacetHeader facet_head=(FacetHeader)header;					
			IncompleteFacetSchema incompletefacet=IncompleteFacetSchema.getIncompleteFacetSchema(manager,facet_head.getServiceid(),facet_head.getFacetid());
			if (incompletefacet!=null) {
				promotedelement=incompletefacet;
			} else {
				log.debug("Enqueueing facet " + facet_head.getServiceid() + " to fetch from " + from.getAddress() + " in federation " + from.getFederationinfo().getFederation().getId());
				incompletefacet=new IncompleteFacetSchema(from,facet_head.getFacetid(),facet_head.getServiceid(),facet_head.getPromotionTimestamp(),facet_head.isAddInfo());
				from.getIncomplete().add(incompletefacet);
				promotedelement=incompletefacet;
				promotedelement.setFetching(false);
				manager.persist(incompletefacet);
				manager.flush();				
			}	
		} else {
			//must be XML header
			FacetXMLHeader xml_header=(FacetXMLHeader)header;
			IncompleteFacetXML incompletexml=IncompleteFacetXML.getIncompleteFacetXML(manager,xml_header.getServiceid(),xml_header.getFacetid(),xml_header.getServiceid());
			if (incompletexml!=null) {
				promotedelement=incompletexml;								
			} else {
				log.debug("Enqueueing xml " + xml_header.getServiceid() + " to fetch from " + from.getAddress() + " in federation " + from.getFederationinfo().getFederation().getId());
				incompletexml=new IncompleteFacetXML(from,xml_header.getXmlid(),xml_header.getFacetid(),xml_header.getServiceid(),xml_header.isAddInfo(),xml_header.getFacetTimestamp());
				from.getIncomplete().add(incompletexml);
				promotedelement=incompletexml;
				promotedelement.setFetching(false);
				manager.persist(incompletexml);
				manager.flush();
			}			
		}
		
//		if (start_fetch && !promotedelement.isFetching()) {
//			//fetch(promotedelement);
//			tryFetchSomething();
//		} 
		
		checkFutureFetch();

	}

	private void checkFutureFetch() {
		//		Check if future fetch is needed;
				Query future_fetch=manager.createNamedQuery(IncompleteElement.to_be_fetch);
				if (future_fetch.getResultList().size()>0) {
					//schedule future fetch; 
					timer.createTimer(retry_fetch,null);
				}
	}

	/** Fill fetch buffer
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void ejbTimeout(Timer arg0) {
		if (canFetch()) {
//			Query q=manager.createNamedQuery(IncompleteElement.to_be_fetch);
//			Query current_fetching=manager.createNamedQuery(IncompleteElement.currently_fetching);
//			int current_fetching_count=current_fetching.getResultList().size();
//			List<IncompleteElement> element=(List<IncompleteElement>)q.getResultList();
//			int i=0;			
//			while (i < max_parallel_fetch-current_fetching_count) {
//				IncompleteElement e=element.get(i);
//				fetch(e);
//				i++;				
//			}
			tryFetchSomething();
			removeUnfetchable();
		}			
		checkFutureFetch();		
	}

	/** Try to fetch some elements. First look among services, with their facets and xmls.
	 * Then look among facets, and then among xmls. 
	 * 
	 *
	 */
	private void tryFetchSomething() {
		//NOTE: timestamps in promotion headers is 0 because it is not used in requests
		lookupMessenger();
		Collection<IncompleteService> services=IncompleteService.getAll(manager);
		//Check at every iteration to avoid fetching too many services at the same time
		while (!services.isEmpty() && canFetch()){
			IncompleteService service=services.iterator().next();
			services.remove(service);
			if (!service.isFetching()) {
				service.setFetching(true);
				service.setNum_try(service.getNum_try()+1);
				String sourcedm  = service.getFrom().getAddress();
				DetailsRequest request=new DetailsRequest(gossip_messenger.getAddress(),sourcedm,service.getFrom().getFederationinfo().getFederation().getId());
				ServiceHeader serv_header=new ServiceHeader(service.getServiceid(),0);
				request.getPayload().add(serv_header);
				//Look for associated facets and xml
				
	//			Request all schemas that must be requested to the same DM from which we are fetching the service
				Collection<IncompleteFacetSchema> incompl_schemas=IncompleteFacetSchema.getByServiceId(manager,service.getServiceid(),service.getFrom());					
				for (IncompleteFacetSchema incompl_facet: incompl_schemas) {
					if (!incompl_facet.isFetching()) {
						FacetHeader header=new FacetHeader(incompl_facet.getServiceid(),incompl_facet.getFacetid(),incompl_facet.getTimestamp(),incompl_facet.isAddInfo(),0);
						request.getPayload().add(header);
						incompl_facet.setFetching(true);
						incompl_facet.setNum_try(incompl_facet.getNum_try()+1);
					}
				}
							
				//Request all xml that must be requested to the same DM from which we are fetching the service
				Collection<IncompleteFacetXML> incompl_xmls=IncompleteFacetXML.getByServiceId(manager,service.getServiceid(),service.getFrom());
				for (IncompleteFacetXML incomp_xml : incompl_xmls) {
					if (!incomp_xml.isFetching()) {
						FacetXMLHeader facetxmlheader=new FacetXMLHeader(incomp_xml.getServiceId(),incomp_xml.getFacetSchemaId(),incomp_xml.getXmlId(),0,incomp_xml.isAddInfo(),incomp_xml.getFacetTimeStamp());
						request.getPayload().add(facetxmlheader);
						incomp_xml.setFetching(true);
						incomp_xml.setNum_try(incomp_xml.getNum_try()+1);
					}
				}
				sender.send(request);
			}
		}
		
		//Now try with single facet schemas, only if the corresponding service are already here
		Collection<IncompleteFacetSchema> incompl_schemas=IncompleteFacetSchema.getSchemasToBeFetch(manager);
		for (IncompleteFacetSchema incompl_facet: incompl_schemas) {
			if (canFetch() && model_manager.lookup(incompl_facet.getServiceid())!=null) {
				FacetHeader header=new FacetHeader(incompl_facet.getServiceid(),incompl_facet.getFacetid(),incompl_facet.getTimestamp(),incompl_facet.isAddInfo(),0);
				DetailsRequest request=new DetailsRequest(gossip_messenger.getAddress(),incompl_facet.getFrom().getAddress(),incompl_facet.getFrom().getFederationinfo().getFederation().getId());
				request.getPayload().add(header);
				incompl_facet.setFetching(true);
			} else {
				//It's pointless to go on querying, we have reached the maximum number of parallel fetches
				return;
			}
		}
		
		//Now try with xmls, only if the facet schemas are here
		Collection<IncompleteFacetXML> incompl_xmls=IncompleteFacetXML.getXMLToBeFetched(manager);
		for (IncompleteFacetXML incomp_xml : incompl_xmls) {
			if (canFetch() && model_manager.lookup(incomp_xml.getServiceId(),incomp_xml.getFacetSchemaId())!=null) {
				FacetXMLHeader xml_header=new FacetXMLHeader(incomp_xml.getServiceId(),incomp_xml.getFacetSchemaId(),incomp_xml.getXmlId(),0,incomp_xml.isAddInfo(),incomp_xml.getFacetTimeStamp());
				DetailsRequest request=new DetailsRequest(gossip_messenger.getAddress(),incomp_xml.getFrom().getAddress(),incomp_xml.getFrom().getFederationinfo().getFederation().getId());
				request.getPayload().add(xml_header);
				incomp_xml.setFetching(true);
			} else {
				//It's pointless to go on querying, we have reached the maximum number of parallel fetches
				return;
			}
			
		}
	}
	
	@SuppressWarnings("unchecked")
	public void removeFromQueue(PromotionHeader header) {
		if (header instanceof ServiceHeader) {
			ServiceHeader serv_header=(ServiceHeader)header;
			Query q=manager.createNamedQuery(IncompleteService.get_by_service_id);
			q.setParameter("serviceid",serv_header.getServiceid());
			Collection<IncompleteService> services=(Collection<IncompleteService>)q.getResultList();
			for (IncompleteService i:services) {
				i.getFrom().getIncomplete().remove(i);
				manager.remove(i);
			}
		} else if (header instanceof FacetHeader) {
			FacetHeader facet_head=(FacetHeader)header;
			Query q=manager.createNamedQuery(IncompleteFacetSchema.get_by_facet_schema_id);
			q.setParameter("facetid",facet_head.getFacetid());
			Collection<IncompleteFacetSchema> facets=(Collection<IncompleteFacetSchema>)q.getResultList();
			for (IncompleteFacetSchema fac:facets) {
				fac.getFrom().getIncomplete().remove(fac);
				manager.remove(fac);
			}
		} else {
			//must be XML
			FacetXMLHeader xml_head=(FacetXMLHeader)header;
			Query q=manager.createNamedQuery(IncompleteFacetXML.get_by_xml_id);
			q.setParameter("xmlid",xml_head.getXmlid());
			Collection<IncompleteFacetXML> facetxml=(Collection<IncompleteFacetXML>)q.getResultList();
			for (IncompleteFacetXML xml:facetxml) {
				xml.getFrom().getIncomplete().remove(xml);
				manager.remove(xml);
			}
		}
		
	}
	
//	private void fetch(IncompleteElement ele) {		
//		lookupMessenger();
//		 
//		DetailsRequest request=new DetailsRequest(gossip_messenger.getAddress(),ele.getFrom().getAddress(),ele.getFrom().getFederationinfo().getFederation().getId());
//		PromotionHeader header=null;
//		if (ele instanceof IncompleteService) {
//			IncompleteService incomp_service=(IncompleteService)ele;
//			header=new ServiceHeader(incomp_service.getServiceid(),0);		
//		} else if (ele instanceof IncompleteFacetSchema) {
//			IncompleteFacetSchema incomp_schema=(IncompleteFacetSchema)ele;
//			header=new FacetHeader(incomp_schema.getServiceid(),incomp_schema.getFacetid(),incomp_schema.getTimestamp(),incomp_schema.isAddInfo(),0);
//			//Check if service has to be fetched too (fetch it BEFORE fetching facet)
//		} else {
//			//must be XML
//			IncompleteFacetXML incomp_xml=(IncompleteFacetXML)ele;
//			header=new FacetXMLHeader(incomp_xml.getServiceId(),incomp_xml.getFacetSchemaId(),incomp_xml.getXmlId(),0,incomp_xml.isAddInfo(),incomp_xml.getFacetTimeStamp());
//		}		
//		log.debug("Fetching " + header.getClass().getName() + " with id " + header.getServiceid() + " from " +ele.getFrom().getAddress());
//		request.getPayload().add(header);
//		ele.setFetching(true);
//		manager.flush();
//		sender.send(request);
//	}
	
	private void lookupMessenger() {
		  MBeanServer server = MBeanServerLocator.locate();
			try {
				gossip_messenger=(IGossipMessagingManager)MBeanProxyExt.create(IGossipMessagingManager.class, "DeliveryManager:service=GossipMessaging", server);
			} catch (MalformedObjectNameException e) {			
		        log.error(e.getMessage());	      		    
			}	   
	}
	
	@SuppressWarnings("unchecked")
	private boolean canFetch() {
		//Check if fetching can start now
		Query parallel=manager.createNamedQuery(IncompleteElement.currently_fetching);
		Collection<IncompleteElement> parallel_fetching=parallel.getResultList();
		if (parallel_fetching.size() < max_parallel_fetch) {
			return true;
		} 
		return false;
	}
	
	private void removeUnfetchable() {
		for (IncompleteElement ele: IncompleteElement.getTriedManyTimes(manager,max_retry)) {
			manager.remove(ele);
		}
		
	}

}
