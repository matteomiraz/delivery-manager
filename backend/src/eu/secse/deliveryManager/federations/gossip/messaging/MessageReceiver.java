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

package eu.secse.deliveryManager.federations.gossip.messaging;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.secse.deliveryManager.core.ModelManager;
import eu.secse.deliveryManager.data.FacetEnt;
import eu.secse.deliveryManager.data.FacetXmlEnt;
import eu.secse.deliveryManager.data.FederatedFacet;
import eu.secse.deliveryManager.data.FederatedService;
import eu.secse.deliveryManager.data.FederatedXml;
import eu.secse.deliveryManager.data.FederationEnt;
import eu.secse.deliveryManager.data.ServiceEnt;
import eu.secse.deliveryManager.federations.gossip.core.IGossipProxy;
import eu.secse.deliveryManager.federations.gossip.data.DeletedFacetSchema;
import eu.secse.deliveryManager.federations.gossip.data.DeletedFacetXML;
import eu.secse.deliveryManager.federations.gossip.data.DeletedService;
import eu.secse.deliveryManager.federations.gossip.data.DeliveryManagerGossipInfo;
import eu.secse.deliveryManager.federations.gossip.data.GossipFederationInfo;
import eu.secse.deliveryManager.federations.gossip.data.IncompleteFacetSchema;
import eu.secse.deliveryManager.federations.gossip.data.IncompleteFacetXML;
import eu.secse.deliveryManager.federations.gossip.data.IncompleteService;
import eu.secse.deliveryManager.federations.gossip.data.RequestedFacetSchema;
import eu.secse.deliveryManager.federations.gossip.data.RequestedFacetXML;
import eu.secse.deliveryManager.federations.gossip.data.RequestedService;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.ContactAnswer;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.DFacetXML;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.Deletion;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.DetailsRequest;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.FacetHeader;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.FacetSpecificationWrapper;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.FacetXMLHeader;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.ForwardedSubscription;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.HeartBeatMessage;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.InViewAck;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.Promotion;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.PromotionDetails;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.PromotionHeader;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.Restarting;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.ServiceHeader;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.Subscription;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.SubscriptionAck;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.UnSubscription;
import eu.secse.deliveryManager.federations.gossip.timers.IPartialViewCleaner;
import eu.secse.deliveryManager.federations.gossip.timers.IncompleteFetcher;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.model.FacetAddInfo;
import eu.secse.deliveryManager.model.FacetSpec;
import eu.secse.deliveryManager.model.FacetSpecXML;

@Stateless
@TransactionAttribute(value=TransactionAttributeType.REQUIRED)
public class MessageReceiver implements IMessageReceiver {
	private static final Log log = LogFactory.getLog(MessageReceiver.class);

	private GossipFederationInfo ginfo=null;
	private FederationEnt fed=null;
	
	@PersistenceContext(unitName="deliveryManager")
	private EntityManager em;
	
	@EJB private IncompleteFetcher fetcher;	
	@EJB private IGossipProxy proxy;	
	
	@EJB private IMessageSender sender;
	@EJB private IPartialViewCleaner cleaner;	
	@EJB private IGossipMessagingManager gossipmanager;	
	@EJB private ModelManager manager;
	
	private String myaddress;
	
	@PostConstruct 
	public void fillAttributes() {
		myaddress=gossipmanager.getAddress();
	}
	
	
	public void receive(Message m) {
		String federationid=m.getFederationId();
		ginfo=getExtraInfo(federationid);
		fed=getFederation(federationid);		
		if (ginfo==null) {
			log.warn("Received message from non-joined or non-gossip federation: " + federationid);
			return;
		}
		log.info("Received message of type " + m.getClass() + " from " + m.getSender());
		log.debug("Message content: " + m.toString());		
		if (m instanceof Promotion) {
			handlePromotion((Promotion)m); 													
		} else if (m instanceof Deletion ) {
			handleDeletion((Deletion)m);
		} else if (m instanceof Subscription) {
			Subscription s=(Subscription)m;
			handleSubscription(s);
		} else if (m instanceof ForwardedSubscription) {
			handleForwardedSubscription((ForwardedSubscription)m);
		} else if (m instanceof HeartBeatMessage) {
			handleHeartBeatMessage((HeartBeatMessage)m);
		}  else if (m instanceof UnSubscription) {
			UnSubscription unsub=(UnSubscription)m;
			handleUnsubscription(unsub);
		} else if (m instanceof SubscriptionAck) {
			handleSubscriptionAck((SubscriptionAck)m);
		} else if (m instanceof ContactAnswer ) {
			handleContactAnswer((ContactAnswer)m);
		} else if (m instanceof InViewAck ) {
			handleInViewAck((InViewAck)m);
		} else if (m instanceof DetailsRequest) {
			handleDetailsRequest((DetailsRequest)m);
		} else if (m instanceof PromotionDetails) {
			handlePromotionDetails((PromotionDetails)m);
		} else if (m instanceof Restarting) {
			handleRestarting((Restarting)m);
		}
		em.flush();
	}

	
	private void handleDeletion(Deletion deletion) {
		//notify manager, broadcast if not already in deletions
		
		Collection<PromotionHeader> deleted = deletion.getDeletedelements();
		Collection<DeliveryManagerGossipInfo> partial_view=proxy.getPartialView(fed);
		for (PromotionHeader delele:deleted) {
			if (delele instanceof ServiceHeader) {
				ServiceHeader serv=(ServiceHeader)delele;
				String serviceid=serv.getServiceid();				
				DeletedService delservice=DeletedService.getDeletedService(em,fed.getId(),serviceid);
				boolean newinfo=false;
				if (delservice==null) {					
					//insert
					delservice=new DeletedService(fed.getId(),serviceid,delele.getPromotionTimestamp());
					em.persist(delservice);
					em.flush();
					newinfo=true;
				}
				else if (delservice.getDeletiontime().getTime()<delele.getPromotionTimestamp()) {
					delservice.setDeletiontime(new Date(delele.getPromotionTimestamp()));
					newinfo=true;
				}	
				if (newinfo) {
					//	Notify manager
					removeServiceFromManager(serviceid);
					
					if (partial_view.isEmpty()) {
						//set pending;
						delservice.setPending(true);
					} else {
						//broadcast and set not pending
						delservice.setPending(false);
						for (DeliveryManagerGossipInfo partial_member:partial_view) {
							Deletion d=new Deletion(myaddress,partial_member.getAddress(),fed.getId());
							d.getDeletedelements().add(delele);
							sender.send(d);
						}
					}
				} 									
			} else if (delele instanceof FacetHeader) {
				FacetHeader facet_head=(FacetHeader)delele;
				String serviceid=facet_head.getServiceid();
				//Check if this facet or the associated service has been deleted
				DeletedService service=DeletedService.getDeletedService(em,fed.getId(),serviceid);
				if (service==null) {
					//Go on, check facet
					DeletedFacetSchema deletedfacet=DeletedFacetSchema.getDeletedFacetSchema(em,fed.getId(),facet_head.getFacetid(),facet_head.getServiceid());
					boolean newinfo=false;
					if (deletedfacet==null) {
						newinfo=true;
//						insert
						deletedfacet=new DeletedFacetSchema(fed.getId(),facet_head.getFacetid(),facet_head.getServiceid(),delele.getPromotionTimestamp());
						em.persist(deletedfacet);
						em.flush();
					} else if (deletedfacet.getDeletiontime().getTime()<delele.getPromotionTimestamp()) {
						newinfo=true;
						deletedfacet.setDeletiontime(new Date(delele.getPromotionTimestamp()));
						newinfo=true;
					}
					if (newinfo) {
						//Notify  manager
						removeFacetFromManager(serviceid,facet_head.getFacetid());
						if (partial_view.isEmpty()) {
							//set pending;
							deletedfacet.setPending(true);
						} else {
							//broadcast and set not pending
							deletedfacet.setPending(false);
							for (DeliveryManagerGossipInfo partial_member:partial_view) {
								Deletion d=new Deletion(myaddress,partial_member.getAddress(),fed.getId());
								d.getDeletedelements().add(delele);
								sender.send(d);
							}
						}
					} 
					
				}
			} else {
				//must be XML
				FacetXMLHeader facet_xml = (FacetXMLHeader)delele;
				String serviceid=facet_xml.getServiceid();
				String facetid=facet_xml.getFacetid();
				String xml_id=facet_xml.getXmlid();
				//check  if the service or the facet have already been deleted
				DeletedService deletedservice=DeletedService.getDeletedService(em,fed.getId(),serviceid);
				DeletedFacetSchema deletedfacetschema=DeletedFacetSchema.getDeletedFacetSchema(em,fed.getId(),serviceid,facetid);
				if (deletedservice==null && deletedfacetschema==null) {
					//XML has to be removed (no timestamp check, timestamp is for different xml attached to same facet
					DeletedFacetXML deletedxml=DeletedFacetXML.getDeletedFacetXML(em,fed.getId(),serviceid,facetid,xml_id);
					boolean newinfo=false;
					if (deletedxml==null) {
						deletedxml=new DeletedFacetXML(fed.getId(),facetid,serviceid,xml_id,delele.getPromotionTimestamp());
						em.persist(deletedxml);
						em.flush();						
						newinfo=true;
					} else if (deletedxml.getDeletiontime().getTime()<delele.getPromotionTimestamp()) {
						newinfo=true;
						deletedxml.setDeletiontime(new Date(delele.getPromotionTimestamp()));
					}
					if (newinfo) {
						//Notify  manager
						removeFacetXMLFromManager(serviceid,facetid,xml_id);
						if (partial_view.isEmpty()) {
							//set pending;
							deletedxml.setPending(true);
						} else {
							//broadcast and set not pending
							deletedxml.setPending(false);
							for (DeliveryManagerGossipInfo partial_member:partial_view) {
								Deletion d=new Deletion(myaddress,partial_member.getAddress(),fed.getId());
								d.getDeletedelements().add(delele);
								sender.send(d);
							}
						}
					}	 		
				}
			}
		}
	}

	//In the context of "fed"
	private void removeServiceFromManager(String serviceid) {
							FederatedService fed_serv=manager.lookup(serviceid,fed);
							if (fed_serv!=null) {								
//Cascaded:								
//								FederatedElementExtraInfo extra=fed_serv.getExtraInfo();
//								if (extra!=null) {
////									extra.setElem(null);
//									em.remove(extra);
//								} 							
								//fed_serv.setExtraInfo(null);
								manager.delete(fed_serv);
							}
	}
	
	//In the context of "fed"
	private void removeFacetFromManager(String serviceid,String facetid) {
		FederatedFacet fed_facet=manager.lookup(serviceid,facetid,fed);
//Cascaded:
//		FederatedElementExtraInfo extra=fed_facet.getExtraInfo();
//		if (extra!=null) {
//			extra.setElem(null);
//			em.remove(extra);
//		} 
//		fed_facet.setExtraInfo(null);
		if (fed_facet!=null) {
			manager.delete(fed_facet);
		}
	}
	
	//In the contest of "fed"
	private void removeFacetXMLFromManager(String serviceid, String facetid, String xml_id) {
		FederatedXml fed_xml=manager.lookup(serviceid,facetid,xml_id,fed);
//		FederatedElementExtraInfo extra=fed_xml.getExtraInfo();
//		if (extra!=null) {
//			extra.setElem(null);
//			em.remove(extra);
//		} 
//		fed_xml.setExtraInfo(null);
		if (fed_xml!=null) {
			manager.delete(fed_xml);
		}
	}
		
	
	private void handleForwardedSubscription(ForwardedSubscription fw) {		
		DeliveryManagerGossipInfo ginfo=proxy.getDM(fw.getSubscribedId(),fed.getId());
		if (ginfo.isPartialview() || !randomAcceptSubscription(fw.getSubscribedId())) {
			//Forward
			forwardSubscription(fw);
		}		
	}
	
	private void forwardSubscription(ForwardedSubscription fw) {
//		If hop counter is 0, discard
		if (fw.getHopcount()==0 ) {
			return;					
		}

//		Forward to a random member		
		HashSet<String> exclude=new HashSet<String>();
		exclude.add(fw.getSubscribedId());
		
		String id=chooseRandomMember(exclude);			
			
		int counter=fw.getHopcount();
		//SubscribedMember sub=new SubscribedMember(fw.getSubscribedid(),0);
		String sub=fw.getSubscribedId();
		DeliveryManagerGossipInfo dm=proxy.getDM(sub,fed.getId());
		if (dm.isPartialview()) {
			counter--;
		}
		ForwardedSubscription fw2=new ForwardedSubscription(myaddress,id,fed.getId());				
		fw2.setSubscribedId(fw.getSubscribedId());
		sender.send(fw2);
	}
	
	private String chooseRandomMember(Set<String> exclude) {
		Vector<String> sub=new Vector<String>();
		for (DeliveryManagerGossipInfo dm:proxy.getPartialView(fed)) {
			sub.add(dm.getAddress());
		}
		if (exclude!=null) {
			sub.removeAll(exclude);
		}	
		if (sub.size()==0) return null;
		int destnum=(int)Math.floor(Math.random()*(sub.size()));
		String random=sub.get(destnum);				
		return random;
	}
	
	
	/** Randomly decides whether to accept a subscription
	 * 
	 * @return true if the subscription was accepted
	 */
	private boolean randomAcceptSubscription(String id) {
//		Accept with a certain probability, otherwise forward
		DeliveryManagerGossipInfo ginfo=proxy.getDM(id,fed.getId());
		if (!ginfo.isPartialview() && !(myaddress.equals(id))) {
			double acceptprob=Math.random()*(proxy.getPartialView(fed).size()+1);
			int acc=(int)Math.floor(acceptprob);
			if (acc==0) {
				confirmSubscription(id);
				return true;
			}			
		}
		return false;
		
	}

	private void handleHeartBeatMessage(HeartBeatMessage message) {
		//Ok, we are in the partial view of some delivery manager (update db)
		HeartBeatMessage hb=(HeartBeatMessage) message;
		String sender=hb.getSender();							
		DeliveryManagerGossipInfo dminfo=proxy.getDM(sender,fed.getId());
		dminfo.setLiveinview(true);					
	}
	
	private void handlePromotionDetails(PromotionDetails details) {
		//Notify manager (check timestamp for facets!), broadcast to everyone waiting
		Collection<Deliverable> content=details.getContent();
		for (Deliverable d: content) {
			if (d instanceof DService) {
				DService dservice=(DService)d;				
//				IncompleteService serv=IncompleteService.search(em,dservice.getServiceID(),fed);
				fetcher.removeFromQueue(new ServiceHeader(dservice.getServiceID(),new Date().getTime()));
//				if (serv!=null) {
//					serv.getIncompleteIn().remove(fed);
//					if (serv.getIncompleteIn().isEmpty()) {
//						em.remove(serv);
//					}
//				}
				addService(dservice);								
				broadcastService(dservice);
			} else if (d instanceof FacetSpecificationWrapper) {
				FacetSpecificationWrapper specificationwrapper=(FacetSpecificationWrapper)d;
				FacetSpec specinfo=specificationwrapper.getSpec();
				if (isUpdated(specificationwrapper.getServiceId(),specinfo.getSchemaID(),specinfo.getDmTimestamp().getTime())) {					
					ServiceEnt ent=manager.lookup(specificationwrapper.getServiceId());
					manager.add(fed,specinfo,ent);
					fetcher.removeFromQueue(new FacetHeader(ent.getElemPK().getId(),specinfo.getSchemaID(),specinfo.getDmTimestamp().getTime(),false,new Date().getTime()));
				}		
				
				broacastSpecFacet(specificationwrapper.getServiceId(),specinfo);
				
			} else if (d instanceof FacetAddInfo) {
				FacetAddInfo addinfo=(FacetAddInfo)d;
				fetcher.removeFromQueue(new FacetHeader(addinfo.getServiceID(),addinfo.getSchemaID(),addinfo.getDmTimestamp().getTime(),true,new Date().getTime()));
				addAdditionalFacetSchema(addinfo);
				broadcastAdditionalFacet(addinfo);
			} else { 
				//Must be XML
				DFacetXML xml=(DFacetXML)d;
				addFacetXML(xml);
				//TODO: the addinfo parameter is unused so we set it as true
				fetcher.removeFromQueue(new FacetXMLHeader(xml.getServiceid(),xml.getFacetschemaid(),xml.getXmlID(),new Date().getTime(),true,xml.getSchemaTimestamp().getTime()));
				broadcastFacetXML(xml);
			}			
		} 

	}

	//Broadcast a service and all the facets, received with the Dservice, that have been requested.
	private void broadcastService(DService dservice) {
		RequestedService service=RequestedService.searchById(em,dservice.getServiceID());		
		Collection<FacetSpec> specs=new Vector<FacetSpec>();
		if (dservice.getSpecType()!=null) {
			specs.addAll(dservice.getSpecType());
			dservice.getSpecType().clear();
		}
//		remove facets: send only if requested		
		if (service!=null) {
			for (DeliveryManagerGossipInfo ginfo:service.getWaiting()) {
				PromotionDetails forwdetails=new PromotionDetails(myaddress,ginfo.getAddress(),fed.getId());							
				forwdetails.getContent().add(dservice);				
				sender.send(forwdetails);
				ginfo.getRequested().remove(service);
			}
			//Sent to everyone, so remove			
			em.remove(service);			
		} 			
		//Send specification facets that have been requested
		for (FacetSpec receivedfacet:specs) {
			broacastSpecFacet(dservice.getServiceID(), receivedfacet);
		}	
	}

	private void broacastSpecFacet(String serviceid, FacetSpec receivedfacet) {		
		RequestedFacetSchema requestedfacet=RequestedFacetSchema.getRequestedSchema(em,serviceid,receivedfacet.getSchemaID());	
		if (requestedfacet!=null) {
			for (DeliveryManagerGossipInfo info:requestedfacet.getWaiting()) {
				PromotionDetails facetDetail=new PromotionDetails(myaddress,info.getAddress(),fed.getId());
				facetDetail.getContent().add(new FacetSpecificationWrapper(receivedfacet,serviceid));
				sender.send(facetDetail);
			}
			em.remove(requestedfacet);
		}
		
	}
	
	private void broadcastFacetXML(DFacetXML xml) {
		log.debug("Broadcasting details of facetxml with id " + xml.getXmlID() + " in federation " + fed.getId());		
		RequestedFacetXML reqxml=RequestedFacetXML.getRequestedFacetXML(em,xml.getServiceid(),xml.getFacetschemaid(),xml.getXmlID());
		if (reqxml!=null) {
			//Broadcast to all
			for (DeliveryManagerGossipInfo ginfo:reqxml.getWaiting()) {
				
				PromotionDetails prom=new PromotionDetails(myaddress,ginfo.getAddress(),fed.getId());
				prom.getContent().add(xml);
				sender.send(prom);
			}
			em.remove(reqxml);
		}	
		
	}
	
	private void broadcastAdditionalFacet(FacetAddInfo addinfo) {
		Query facet=em.createNamedQuery(RequestedFacetSchema.get_by_id);
		facet.setParameter("serviceid",addinfo.getServiceID());
		facet.setParameter("facetid",addinfo.getSchemaID());
		RequestedFacetSchema requestedfacet=(RequestedFacetSchema)facet.getSingleResult();	
		if (requestedfacet!=null) {
			for (DeliveryManagerGossipInfo info:requestedfacet.getWaiting()) {
				PromotionDetails facetDetail=new PromotionDetails(myaddress,info.getAddress(),fed.getId());
				facetDetail.getContent().add(addinfo);
				sender.send(facetDetail);
			}
			em.remove(requestedfacet);
		}
		
	}
		
	
	

	/** Returns true if the facet id is more recent than 
	 * 
	 * @param serviceid 
	 * @param facetid
	 * @param timestamp
	 * @return
	 */
	private boolean isUpdated(String serviceid, String facetid, long newtimestamp) {
		//TODO Is the timestamp correct? Should I use the DM Timestamp?
		FacetEnt oldent=manager.lookup(serviceid,facetid);
		if (oldent==null || oldent.getTimestamp().getTime() < newtimestamp) {
			return true;
		} else {
			return false;
		}
	}
	
	/** Ask the model manager to add a service (and all its facet schemas and xmls)
	 * 
	 * @param serv
	 */
	private void addService(DService serv) {
		manager.add(fed,serv);
		ServiceEnt ent=manager.lookup(serv.getServiceID());
		if (ent==null) {
			log.error("ModelManager returned null service: requested service id is " + serv.getServiceID());
			return;
		}
		//Facets and xml
		if (serv.getSpecType()!=null) {
			for (FacetSpec specfacet:serv.getSpecType()) {
				addFacetSchema(specfacet,serv.getServiceID());			
			}
		}
	}
	
	/** Ask the model manager to add a facet (and its xml) 
	 * 
	 * @param facet_spec
	 * @param serviceid
	 */
	private void addFacetSchema(FacetSpec facet_spec,String serviceid ) {
		ServiceEnt ent=manager.lookup(serviceid);
		manager.add(fed,facet_spec,ent);
		if (facet_spec.getFacetSpecificationXML()!=null && facet_spec.getFacetSpecificationXML().getDocument()!=null) {
			FacetSpecXML specxml=facet_spec.getFacetSpecificationXML();			
			DFacetXML dxml=new DFacetXML(serviceid,facet_spec.getSchemaID(),specxml.getXmlID(),specxml.getName(),specxml.getDocument(),specxml.getTimestamp(),specxml.getIsoTimestamp(),facet_spec.getDmTimestamp());
			addFacetXML(dxml);			
		}
	}
	
	private void addAdditionalFacetSchema(FacetAddInfo addinfo) {
		FacetEnt ent=manager.lookup(addinfo.getServiceID(),addinfo.getSchemaID());
		if (ent!=null && ent.getTimestamp().before(addinfo.getDmTimestamp())) {		
			manager.add(fed,addinfo);		
		}
	}
	
	private void addFacetXML(DFacetXML xml) {
		FacetEnt facet_ent=manager.lookup(xml.getServiceid(),xml.getFacetschemaid());
		//What about the timestamp (am I using the correct one)?
		FacetSpec spec=manager.getFacetSpecificationData(xml.getServiceid(),xml.getFacetschemaid());
		if (spec==null) {
			log.error("Could not retrive facet spec to determine type name. Aborting add");
		} else {
			manager.add(fed,xml,facet_ent,spec.getTypeName(),xml.getSchemaTimestamp());
		}
	}
	
	private void handleRestarting(Restarting restarting) {
		//Remove sending node from lists of the federation. Check if isolated
		DeliveryManagerGossipInfo ginfo=proxy.getDM(restarting.getSender(),fed.getId());
		ginfo.setInview(false);
		ginfo.setPartialview(false);
		ginfo.setLiveinview(false);
		if (proxy.getInView(fed).isEmpty()) {
			proxy.isolated(fed.getId());
		}
	}

	private void handleDetailsRequest(DetailsRequest request) {
		//If details are available, get from register and send, otherwise put to wait
		PromotionDetails details=new PromotionDetails(myaddress,request.getSender(),request.getFederationId());
		for (PromotionHeader prom:request.getPayload()) {
			if (prom instanceof ServiceHeader) {
				//Only send service, without specification facets
				handleServiceDetailsRequest(request, details, prom);															
			} if (prom instanceof FacetHeader) {
				FacetHeader facet_head=(FacetHeader)prom;			
				if (facet_head.isAddInfo()) {					
					//Additional info
					handleAdditionalFacetDetailsRequest(request, details, facet_head);				
				} else {
					//Specification facet
					handleSpecificationFacetDetails(request, details, facet_head);				
				}							
			} else if (prom instanceof FacetXMLHeader) {
				handleFacetXMLDetailsRequest(request,details,(FacetXMLHeader)(prom));				
			}			
		}
		//Check if it's empty
		if (!details.getContent().isEmpty()) {
			sender.send(details);
		}
	}

	
	private void handleFacetXMLDetailsRequest(DetailsRequest request, PromotionDetails details, FacetXMLHeader xml_head) {		
		//Look for additional or specification			
		FacetSpec spec=manager.getFacetSpecificationData(xml_head.getServiceid(),xml_head.getFacetid());
		if (spec==null) {
			spec=manager.getFacetAdditionalData(xml_head.getServiceid(),xml_head.getFacetid());
		}
		if (spec==null && spec.getFacetSpecificationXML()==null) {
//			add to request
			RequestedFacetXML xml=RequestedFacetXML.getRequestedFacetXML(em,xml_head.getServiceid(),xml_head.getFacetid(),xml_head.getXmlid());
			if (xml==null) {
				xml=new RequestedFacetXML(xml_head.getFacetid(),xml_head.getServiceid(),xml_head.getXmlid());				
				em.persist(xml);
				em.flush();
			}
			DeliveryManagerGossipInfo ginfo=proxy.getDM(request.getSender(),request.getFederationId());
			xml.getWaiting().add(ginfo);
			ginfo.getRequested().add(xml);
			return;		
		}				
		//send
		DFacetXML facetxml=new DFacetXML(xml_head.getServiceid(),xml_head.getFacetid(),spec.getFacetSpecificationXML(),spec.getDmTimestamp());
		details.getContent().add(facetxml);								
	}
	
	private void handleSpecificationFacetDetails(DetailsRequest request, PromotionDetails details, FacetHeader facet_head) {
		FacetSpec specificationfacet=manager.getFacetSpecificationData(facet_head.getServiceid(),facet_head.getFacetid());
		if (specificationfacet!=null) {		
			details.getContent().add(new FacetSpecificationWrapper(specificationfacet,facet_head.getServiceid()));			
		} else {
			log.debug("Element not found (it's ok, asking the registry if the element is there)");
			RequestedFacetSchema rservice=RequestedFacetSchema.getRequestedSchema(em,facet_head.getServiceid(),facet_head.getFacetid());						
			if (rservice==null) {
				rservice=new RequestedFacetSchema(facet_head.getFacetid(),facet_head.getServiceid());														
				em.persist(rservice);
				em.flush();
			} 
			DeliveryManagerGossipInfo ginfo=proxy.getDM(request.getSender(),request.getFederationId());
			rservice.getWaiting().add(ginfo);
			ginfo.getRequested().add(rservice);
		}
	}

	private void handleServiceDetailsRequest(DetailsRequest request, PromotionDetails details, PromotionHeader prom) {
		ServiceHeader service_head=(ServiceHeader)prom;
		DService service = manager.getServiceData(service_head.getServiceid());
		
		if (service!=null) {
			
			if (service.getSpecType() != null) {
				service.getSpecType().clear();
			}
			details.getContent().add(service);
		} else {
//					Add to requested 					
			RequestedService rservice=RequestedService.searchById(em,service_head.getServiceid());
			if (rservice==null) {
				rservice=new RequestedService(service_head.getServiceid());				
				em.persist(rservice);
				em.flush();
			}
			DeliveryManagerGossipInfo ginfo=proxy.getDM(request.getSender(),request.getFederationId());
			rservice.getWaiting().add(ginfo);
			ginfo.getRequested().add(rservice);			
		}
	}

	private void handleAdditionalFacetDetailsRequest(DetailsRequest request, PromotionDetails details, FacetHeader facet_head) {
		FacetAddInfo addinfo=manager.getFacetAdditionalData(facet_head.getServiceid(),facet_head.getFacetid());
		if (addinfo!=null) {							
					details.getContent().add(addinfo);
		} else {
			log.debug("Element not found (it's ok, asking the registry if the element is there)");
			RequestedFacetSchema req_schema=RequestedFacetSchema.getRequestedSchema(em,facet_head.getServiceid(),facet_head.getFacetid());
			if (req_schema==null) {
				req_schema=new RequestedFacetSchema(facet_head.getFacetid(),facet_head.getServiceid());				
				em.persist(req_schema);
				em.flush();
			}
			DeliveryManagerGossipInfo ginfo=proxy.getDM(request.getSender(),request.getFederationId());
			req_schema.getWaiting().add(ginfo);
			ginfo.getRequested().add(req_schema);			
		}
	}
	
	private void handleInViewAck(InViewAck ack) {
		DeliveryManagerGossipInfo dm=proxy.getDM(ack.getSender(),ginfo.getFederation().getId());
		dm.setInview(true);		
	}

	private FederationEnt getFederation(String federationid) {
		return em.find(FederationEnt.class,federationid);
	}
	
	private GossipFederationInfo getExtraInfo(String federationid) {
		FederationEnt fed=getFederation(federationid);
		if (fed==null) return null;
		GossipFederationInfo gf=(GossipFederationInfo)fed.getExtraInfo();
		return gf;
	}
	
	
	private void handleContactAnswer(ContactAnswer answer) {
		//Add to partial view 			
		addPartialMember(answer.getSender());	
		Collection<PromotionHeader> headers=answer.getHeaders();
		//Add to Incomplete elements if we don't have them, start fetching first one
		if (headers==null) {
			log.debug("no headers in contact answer");
			return;
		}
		for (PromotionHeader h: headers) {
			if (h instanceof ServiceHeader) {				
				ServiceEnt ent=manager.lookup(h.getServiceid());
				if (ent!=null) {
					//notify presence
					manager.add(fed,h.getServiceid());
				} else {
					//Add to request queue
					DeliveryManagerGossipInfo dm=proxy.getDM(answer.getSender(),fed.getId());
					fetcher.queueElement(h,dm);
				}
			} else if (h instanceof FacetHeader){
				FacetHeader facet_head=(FacetHeader)h;
				FacetEnt ent=manager.lookup(facet_head.getServiceid(),facet_head.getFacetid());
				if (ent!=null ) {
//					notify presence
					manager.add(fed,h.getServiceid(),facet_head.getFacetid(),facet_head.isAddInfo());
				} else {
					//Request
					DeliveryManagerGossipInfo dm=proxy.getDM(answer.getSender(),fed.getId());
					fetcher.queueElement(h,dm);
				}
			} else  { 
//				Must be XML
				FacetXMLHeader facet_head=(FacetXMLHeader)h;
				FacetXmlEnt ent=manager.lookup(facet_head.getServiceid(),facet_head.getFacetid(),facet_head.getXmlid());
				if (ent!=null ) {
					//Facet xml already present, do nothing
					manager.add(fed,h.getServiceid(),facet_head.getFacetid(),facet_head.getXmlid());
				} else {
					//Request					
					DeliveryManagerGossipInfo dm=proxy.getDM(answer.getSender(),fed.getId());
					fetcher.queueElement(h,dm);
				}
			}			
		}
	}
	
	
	private void handleSubscription(Subscription sub) {
		Vector<DeliveryManagerGossipInfo> partialview=new Vector<DeliveryManagerGossipInfo>(proxy.getPartialView(fed));
		if (sub.isResubscription()) {
			acceptSubscription(sub);
			return;
		} else if (sub.getHopCount()==0 || (fed.isOwnership() && partialview.isEmpty())) {
			//accept, acting as a contact
			ContactAnswer c=new ContactAnswer(gossipmanager.getAddress(),sub.getSender(),fed.getId());
			sender.send(c);
			DeliveryManagerGossipInfo dm=proxy.getDM(sub.getSubscribedAddress(),fed.getId());
			dm.setInview(true);
			acceptSubscription(sub);		
			//Start timers
			proxy.coordinatorIsNotAlone(fed.getId());
		}		
		 else {
			//indirection 
			int count=sub.getHopCount();
			String destination=null;
			int memnum=contactForwardRoulette();
			if (memnum==-1) {
				destination=ginfo.getInitialcontact();
			}
			else {
				destination=partialview.get(memnum).getAddress();
			}
			sub=new Subscription(gossipmanager.getAddress(),destination,fed.getId(),sub.getSubscribedAddress());		
			sub.setHopCount( (count==-1) ? partialview.size()*2 : count - 1 );

			sender.send(sub);
		}			
		if (sub.getHopCount()!=-1 && !sub.isResubscription()) {
			DeliveryManagerGossipInfo sender=proxy.getDM(sub.getSender(),fed.getId());
			sender.incrementContactFrom();						
		}
		
		if (fed.isOwnership() && ginfo.isCoordinator_alone()) {
			ginfo.setCoordinator_alone(false);
			proxy.coordinatorIsNotAlone(fed.getId());
		}
	}
	
	private void acceptSubscription(Subscription sub) {
//		accept				
		DeliveryManagerGossipInfo dm=proxy.getDM(sub.getSubscribedAddress(),fed.getId());
		boolean inpartial=dm.isPartialview();
		//Do not take into account for forwarding
		dm.setPartialview(false);
		em.flush();
		Vector<DeliveryManagerGossipInfo> partialview=new Vector<DeliveryManagerGossipInfo>(proxy.getPartialView(fed));
		for (DeliveryManagerGossipInfo member:partialview) {
			ForwardedSubscription fw=new ForwardedSubscription(gossipmanager.getAddress(),member.getAddress(),fed.getId());
			fw.setSubscribedId(sub.getSubscribedAddress());
			fw.setHopcount(proxy.computeHopCounter(fed.getId()));						
			sender.send(fw);					
		}
		// C additional copies (only if initial subscription!)
		if (!sub.isResubscription()) {
			for (int i=0;i<gossipmanager.getC();i++) {
					ForwardedSubscription fw=new ForwardedSubscription(gossipmanager.getAddress(),proxy.chooseRandomMember(fed.getId(),null),fed.getId());					
					fw.setSubscribedId(sub.getSubscribedAddress());
					fw.setHopcount(proxy.computeHopCounter(fed.getId()));				
					if (fw.getDestination()!=null) {
						sender.send(fw);
					}
			}
		}		
		if (proxy.getPartialView(fed).isEmpty() && fed.isOwnership()) {
//			node startup , put in partial view
			confirmSubscription(sub.getSubscribedAddress());
		}
		if (inpartial) {
			dm.setPartialview(true);
		}		
	}

	/** Accept the subscription, if the contact is not already in the partial view. 
	 * Schedule removal of the subscription when it expires
	 *  Send ack to subscriber.
	 * @param id
	 */
	private void confirmSubscription(final String id) {
		if (id!=gossipmanager.getAddress()) {
			addPartialMember(id);
			//send ack
			SubscriptionAck ack=new SubscriptionAck(gossipmanager.getAddress(),id,fed.getId());
			sender.send(ack);
		}
				
	}
	
	
	/** Remove the unsubscribed member from the partial view and substitute its id with 
	 * another id (carried by the message) 
	 * 
	 * @param unsub
	 */
	private void handleUnsubscription(UnSubscription unsub) {
		DeliveryManagerGossipInfo info=proxy.getDM(unsub.getUnsubscribingId(),unsub.getFederationId());
		info.setPartialview(false);		
		if (unsub.isMustswap() && ! (myaddress.equals(unsub.getAlternative()))) {
			addPartialMember(unsub.getAlternative());
			InViewAck ack=new InViewAck(myaddress,unsub.getAlternative(),fed.getId());			
			sender.send(ack);
		}
		
		em.flush();
		
		//Check situation
		
		if (proxy.getInView(fed).size()==0) {
			if (fed.isOwnership()) {
				proxy.coordinatorIsAlone(fed.getId());
			} else {
				proxy.isolated(fed.getId());
			}
		}
	}


	
	private void handleSubscriptionAck(SubscriptionAck ack) {
		//Add ack-er to live in-view, signal successful join
		String sender=ack.getSender();		
		DeliveryManagerGossipInfo info=proxy.getDM(sender,ack.getFederationId());
		info.setLiveinview(true);
		info.setInview(true);
		//Ok, we have joined
		proxy.successfulSubscription(fed.getId());
	}
	
	private void handlePromotion(Promotion p) {		
		for (PromotionHeader header:p.getPayload()) {
			if (header instanceof ServiceHeader) {
				//Check if it's among the federated elements OR the promotions OR the received headers OR the deleted Elements.
				ServiceHeader serv_head=(ServiceHeader)header;
				String serviceid=serv_head.getServiceid() ;
				//check 1: if in federated elements, nothing to do
				if (manager.lookup(serviceid,fed)!=null) {
					continue;
				}
				//check 2: in promotions: nothing to do, promotion already spread
				if (manager.isPromoted(serviceid,fed.getId())) {
					continue;
				}
				//check 3: in received services: already spread the promotion, waiting for details				
				if (IncompleteService.getIncompleteServiceInFederation(em,fed,serviceid)!=null) {
					continue;
				}
				//check 4: in deleted services: spread the deletion (if timestamp is more recent, else remove the deletion)
				if (isServiceDeletionMoreRecentThan(fed.getId(),serviceid,header.getPromotionTimestamp())) {				
						//spread deletion						
						Deletion d=new Deletion(myaddress,null,fed.getId());
						proxy.broadcastMessage(d,d.getFederationId());												
						continue;					
				}
				// Ok, it's a new promotion: spread to everyone, enqueue the details request, add to received promotions
				//add to incomplete elements				
				//spread promotion
				for (DeliveryManagerGossipInfo dminfo:ginfo.getDminfo()) {
					Promotion prom=new Promotion(myaddress,dminfo.getAddress(),fed.getId());
					prom.getPayload().add(header);					
					sender.send(prom);
				}
				//fetch details
				fetcher.queueElement(header,proxy.getDM(p.getSender(),p.getFederationId()));		
				
			}
			else if (header instanceof FacetHeader) {
//				Check if it's among the federated elements OR the promotions OR the received headers OR the deleted Elements.
				FacetHeader facet_head=(FacetHeader)header;
				String serviceid=facet_head.getServiceid();
				String facetid=facet_head.getFacetid();
				boolean addInfo=facet_head.isAddInfo();				
				//check 1: if in federated elements, nothing to do
				if (manager.lookup(serviceid,facetid)!=null) {
					//nothing to do, continue
					continue;			
				}
				//check 2: in promotions: this delivery manager promoted this element, so nothing to do
				if ((addInfo && manager.isFacetPromoted(serviceid,facetid,true,fed)) ||
						(!addInfo && manager.isPromotedWithFacets(serviceid,fed.getId()))) {
					//nothing to do, continue
					continue;
				}
//				check 3: in received services: already spread the promotion, waiting for details
				if(IncompleteFacetSchema.getIncompleteFacetSchema(em,serviceid,facetid)!=null) {
					//nothing to do, continue
					continue;
				}
//				check 4: in deleted services or in deleted facets: spread the deletion (if timestamp is more recent, else remove the deletion)
				//Two cases: first, additional info: check deletion of additional facet.
				//Second case: specification facet: check service or facet deletion (based on timestamp)
				if (answeredWithDeletion(serviceid,facetid,addInfo,facet_head.getPromotionTimestamp())) {
					//nothing to do, continue
					continue;
				}				
//				 Ok, it's a new promotion: spread to everyone, enqueue the details request, add to received promotions
				//add to incomplete elements
				Promotion pforw=new Promotion(myaddress,null,fed.getId());
				pforw.getPayload().add(new FacetHeader(serviceid,facetid,facet_head.getTimestamp(),addInfo,facet_head.getPromotionTimestamp()));
				proxy.broadcastMessage(pforw,pforw.getFederationId());
				fetcher.queueElement(header,proxy.getDM(p.getSender(),p.getFederationId()));
				
			} else if (header instanceof FacetXMLHeader) {
				FacetXMLHeader xml_head=(FacetXMLHeader)header;
				String serviceid=xml_head.getServiceid();
				String facetid=xml_head.getFacetid();
				String xmlid=xml_head.getXmlid();
//				Check if it's among the federated elements OR the promotions OR the received headers OR the deleted Elements.
//				check 1: if in federated elements, nothing to do
				if (manager.lookup(serviceid,facetid,xmlid,fed)!=null) {
//					nothing to do, continue
					continue;			
				}
				//check 2: in promotions: this delivery manager promoted this element, so nothing to do
				if (manager.isFacetPromoted(serviceid,facetid,xml_head.isAddInfo(),fed)) {
//					nothing to do, continue
					continue;
				}
//				check 3: in received services: already spread the promotion, waiting for details
				if (IncompleteFacetXML.getIncompleteFacetXML(em,serviceid,facetid,xmlid)!=null) {
					//nothing to do, continue
					continue;
				}
//				check 4: the service has been deleted
				if (answeredWithDeletion(serviceid,facetid,xml_head.isAddInfo(),xml_head.getPromotionTimestamp())) {
					//nothing to do, continue
					continue;
				}
				//check 5: see if the timestamp is more recent
				FacetEnt ent=manager.lookup(serviceid,facetid);
				if (ent!=null && ent.getTimestamp().getTime() > xml_head.getFacetTimestamp()) {
					//Not interested AND don't spread, the XML is old
					continue;
				}
//				 Ok, it's a new promotion: spread to everyone, enqueue the details request, add to received promotions
				Promotion pforw=new Promotion(myaddress,null,fed.getId());
				pforw.getPayload().add(xml_head);
				proxy.broadcastMessage(pforw,pforw.getFederationId());
				fetcher.queueElement(xml_head,proxy.getDM(p.getSender(),p.getFederationId()));
			}
		}	
	}
	
	
	/** If there is a deletion of the facet that is more recent than the promotion of the facet, or a deletion
	 * of the associated service which is more recent than the promotion of the facet, answer with a deletion
	 * and return true 
	 * 
	 * @param federationid
	 * @param serviceid
	 * @param facetid
	 * @param newpromotiontimestamp
	 * @return
	 */
	private boolean answeredWithDeletion(String serviceid, String facetid, boolean addInfo, long newpromotiontimestamp) {
		if (addInfo) { 
			if (isFacetDeletionMoreRecentThan(fed.getId(),serviceid,facetid,newpromotiontimestamp)) {
				//There is a more recent deletion, respond
				Deletion d=new Deletion(myaddress,null,fed.getId());
				d.getDeletedelements().add(new FacetHeader(serviceid,facetid,-1,addInfo,getFacetDeletionTime(fed.getId(),serviceid,facetid)));
				proxy.broadcastMessage(d,d.getFederationId());
				return true;
			}		
		}
		else {
			//For specification, also check service deletion
			if(isServiceDeletionMoreRecentThan(fed.getId(),serviceid,newpromotiontimestamp)) {
				//There is a more recent deletion, respond
				Deletion d=new Deletion(myaddress,null,fed.getId());
				d.getDeletedelements().add(new FacetHeader(serviceid,facetid,-1,addInfo,getFacetDeletionTime(fed.getId(),serviceid,facetid)));
				proxy.broadcastMessage(d,d.getFederationId());					
				return true;
			}
			if (isFacetDeletionMoreRecentThan(fed.getId(),serviceid,facetid,newpromotiontimestamp)) {
				//There is a more recent deletion, respond
				Deletion d=new Deletion(myaddress,null,fed.getId());
				d.getDeletedelements().add(new FacetHeader(serviceid,facetid,-1,addInfo,getFacetDeletionTime(fed.getId(),serviceid,facetid)));
				proxy.broadcastMessage(d,d.getFederationId());
				return true;
			}
		}
		return false;
	}
	
	/** When a promotion is received, check if there is a more recent deletion 
	 * 
	 */
	private boolean isServiceDeletionMoreRecentThan(String federationid, String serviceid, long newpromotiontimestamp) {
		DeletedService service=DeletedService.getDeletedService(em,fed.getId(),serviceid);
		if (service!=null && service.getDeletiontime().getTime() > newpromotiontimestamp) {			
			return true;			
		} 
		return false;		
	}
	
	private long getFacetDeletionTime(String federationid,String serviceid, String facetid) {
		DeletedFacetSchema deletedschema=DeletedFacetSchema.getDeletedFacetSchema(em,fed.getId(),serviceid,facetid);
		return deletedschema.getDeletiontime().getTime();
	}
	
	private boolean isFacetDeletionMoreRecentThan(String federationid, String serviceid, String facetid, long comparison) {
		DeletedFacetSchema deletedschema=DeletedFacetSchema.getDeletedFacetSchema(em,fed.getId(),serviceid,facetid);
		if (deletedschema!=null && deletedschema.getDeletiontime().getTime()>comparison ) {
			return true;			
		} 
		return false;
			
	}
	
	
	
	
	
	private int contactForwardRoulette() {
		Vector<DeliveryManagerGossipInfo> partialview=new Vector<DeliveryManagerGossipInfo>(proxy.getPartialView(fed));		
		double[] bounds=new double[partialview.size()];
		double max=0;
		double quota=1;
		//compute bounds;
		for (int i=0;i<partialview.size(); i++) {			
			DeliveryManagerGossipInfo member=partialview.get(i);			
			Long l=member.getContactfrom();
			if (l!=null) {
				double thisquota=quota/(l.doubleValue()+1);
				bounds[i]=thisquota+max;
				max+=thisquota;
			}	else {
				bounds[i]=quota+max;
				max+=quota;
			}
		}
		double rouletteball=Math.random()*max;
		for (int num=0;num<bounds.length;num++) {
			if (rouletteball < bounds[num] ) return num;
		}
		return -1;
	}
	
	

	private void addPartialMember(String member) {
		DeliveryManagerGossipInfo info=proxy.getDM(member,fed.getId());
		info.setPartialview(true);	
		cleaner.scheduleCleaning(info);					
	}			

	
}
