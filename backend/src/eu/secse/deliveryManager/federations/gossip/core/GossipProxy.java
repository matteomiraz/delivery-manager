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

package eu.secse.deliveryManager.federations.gossip.core;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
import eu.secse.deliveryManager.data.FederatedPromotion;
import eu.secse.deliveryManager.data.FederationEnt;
import eu.secse.deliveryManager.data.FederationExtraInfo;
import eu.secse.deliveryManager.data.ServiceEnt;
import eu.secse.deliveryManager.federations.gossip.data.DeletedElement;
import eu.secse.deliveryManager.federations.gossip.data.DeletedFacetSchema;
import eu.secse.deliveryManager.federations.gossip.data.DeletedFacetXML;
import eu.secse.deliveryManager.federations.gossip.data.DeletedService;
import eu.secse.deliveryManager.federations.gossip.data.DeliveryManagerGossipInfo;
import eu.secse.deliveryManager.federations.gossip.data.FederationSubscriptionStatus;
import eu.secse.deliveryManager.federations.gossip.data.GossipFederationInfo;
import eu.secse.deliveryManager.federations.gossip.data.GossipPromotionExtraInfo;
import eu.secse.deliveryManager.federations.gossip.data.RequestedElement;
import eu.secse.deliveryManager.federations.gossip.messaging.IGossipMessagingManager;
import eu.secse.deliveryManager.federations.gossip.messaging.IMessageSender;
import eu.secse.deliveryManager.federations.gossip.messaging.Message;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.Deletion;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.FacetHeader;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.FacetXMLHeader;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.Promotion;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.Restarting;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.ServiceHeader;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.Subscription;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.UnSubscription;
import eu.secse.deliveryManager.federations.gossip.timers.IHeartBeatTimedChecker;
import eu.secse.deliveryManager.federations.gossip.timers.IHeartBeatTimer;
import eu.secse.deliveryManager.federations.gossip.timers.ISubscriptionTimeout;
import eu.secse.deliveryManager.federations.gossip.timers.ISubscriptionTimer;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.FacetAddInfo;
import eu.secse.deliveryManager.model.FacetSpec;
import eu.secse.deliveryManager.model.FacetSpecXML;

@Stateless
public class GossipProxy implements IGossipProxy {

	//TODO : detect multiple promotions of the same element in a single federation by multiple dm (raise error)
	
	private static final Log log = LogFactory.getLog(IGossipProxy.class);
	
	private IHeartBeatTimer hbeattimer;
	private ISubscriptionTimer subtimer;
	private IHeartBeatTimedChecker beatcheckertimer;
	private ISubscriptionTimeout subtimeout;
	private IMessageSender sender;
	private boolean initialized=false;
	
	@PersistenceContext(unitName="deliveryManager")
	private EntityManager manager;	
	
	@EJB
	private ModelManager model_manager;
		
	private IGossipMessagingManager gossip_manager;
		
	private String my_address;
	
	public void init() {
		if (initialized) return;
		
		initialized=true;		
		
		//Perform JNDI lookups
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			beatcheckertimer=(IHeartBeatTimedChecker)ctx.lookup("deliverymanager/HeartBeatTimedChecker/local");
			subtimer=(ISubscriptionTimer)ctx.lookup("deliverymanager/SubscriptionTimer/local");
			hbeattimer=(IHeartBeatTimer)ctx.lookup("deliverymanager/HeartBeatTimer/local");
			subtimeout=(ISubscriptionTimeout)ctx.lookup("deliverymanager/SubscriptionTimeout/local");			
			sender=(IMessageSender)ctx.lookup("deliverymanager/MessageSender/local");
			
		} catch (NamingException e) {
			log.fatal("Could not lookup ejb:" +e.getMessage());
			return;
		}
		log.debug("Looking up gossip manager bean");
		
		gossip_manager=lookupGossipBean();
		
		if (gossip_manager!=null) {
			log.debug("Correctly looked up gossip manager bean");
		} else {
			log.error("Could not lookup gossip manager bean");			
		}
		my_address=gossip_manager.getAddress();
		
	}
	
	

	/** Called by the federation coordinator
	 * 
	 */
	public void join(FederationEnt federation,
			Map<String, String> options) {
		init();
		log.debug("Joining federation " + federation.getId()); 
		//Create the federation data
		FederationExtraInfo info=federation.getExtraInfo();
		if (info == null) {
			log.debug("Creating Extra information for the federation with id " + federation.getId());
			GossipFederationInfo gfedinfo=new GossipFederationInfo(federation,options);			
			federation.setExtraInfo(gfedinfo);			
			gfedinfo.setFederation(federation);
			manager.persist(gfedinfo);
			manager.flush();
		} else {
			log.debug("Federation info already existing");
		}
		//Join		
		initialSubscribe(federation);
	}

	@SuppressWarnings("unchecked")
	public void initialize() {
		init();
		// Called when booting the server. Must implement resubscription actions , listener intialization, and so on
		//Listener initialization
       IGossipMessagingManager messagemanager=lookupGossipBean();   
		if (messagemanager==null) log.error("Could not lookup Gossip Messaging bean, gossip federations will not work");
		messagemanager.startListener();
		// Lookup federations joined with the gossip method
		Query q=manager.createNamedQuery(FederationEnt.FIND_GOSSIP);
		Collection<FederationEnt> gossip_federations=q.getResultList();
		//Resubscribe
		for (FederationEnt fed:gossip_federations) {				
			restartSubscribe(fed.getId());			
		}
	}

	public void stop() {		
		init();
		if (gossip_manager!=null) {
			gossip_manager.stopListener();			
		}
	}

	public void isolated(String federationid) {
		init();
		rejoinFederation(federationid);		
	}

	/** Sends the necessary messages to perform unsubscription. Does not clear the DB
	 * 
	 * @param fed
	 */
	private void unsubscribe(FederationEnt fed) {
		init();
		//Tell nodes in the InView to substitute ID		
		Vector<DeliveryManagerGossipInfo> lastinview=new Vector<DeliveryManagerGossipInfo>(getInView(fed));
		Vector<DeliveryManagerGossipInfo> partialview=new Vector<DeliveryManagerGossipInfo>(getPartialView(fed));
		
		int C=gossip_manager.getC();
		String myaddress=gossip_manager.getAddress();
		
		int lprime=lastinview.size();
		int swapnumber=lprime-C-1;
		UnSubscription unsub=null;

		for (int i=0; i<swapnumber;i++) {
			//Ask to swap
			int swapindex=0;
			if (partialview.size()!=0) {
				swapindex=i%partialview.size();
			}
			unsub=new UnSubscription(myaddress,lastinview.get(i).getAddress(),fed.getId());
			if (partialview.size()>0) {
				unsub.setAlternative(partialview.get(swapindex).getAddress());
			}
			unsub.setUnsubscribingId(myaddress);
			sender.send(unsub);
		}
		

		for (int i=swapnumber;i<lastinview.size() && i>0;i++) {
			//Simply ask to remove from the list
			unsub=new UnSubscription(myaddress,lastinview.get(i).getAddress(),fed.getId());
			unsub.setUnsubscribingId(myaddress);			
			sender.send(unsub);
		}
		
	
	}
	
	/** Clears the database information for the federation
	 * 
	 * @param fed
	 */
	@SuppressWarnings("unchecked")
	private void clearFederationData(FederationEnt fed) {
		init();
		log.debug("Clearing proxy federation data for federation " + fed.getId());
		GossipFederationInfo fedinfo=(GossipFederationInfo)fed.getExtraInfo();
						
		Vector<DeliveryManagerGossipInfo> feddms = new Vector<DeliveryManagerGossipInfo>(fedinfo.getDminfo()) ;				
		
		
		for (DeliveryManagerGossipInfo dminfo: feddms) {
			dminfo.setRequested(new Vector<RequestedElement>());
			manager.remove(dminfo);
			fedinfo.getDminfo().remove(dminfo);
		}					
		
		fed.setExtraInfo(null);
		manager.remove(fedinfo);
		
		for (DeletedElement delele: DeletedElement.searchByFederation(manager,fed.getId())) {
			manager.remove(delele);
		}
		
		//fedinfo.setStatus(FederationSubscriptionStatus.UNSUBSCRIBED.getValue());
		log.debug("Cleaned proxy federation data for federation " + fed.getId());
	}
	
	private void rejoinFederation(String federationid) {
		init();
		//First unsubscribe
		FederationEnt fed=manager.find(FederationEnt.class,federationid);
		if (fed==null) {
			log.warn("Trying to unsubscribe from nonjoined federation");
			return;
		}
		unsubscribe(fed);
		//Clear timers
		clearTimers(fed);
		//Resubscribe
		initialSubscribe(fed);
	}

	
	private IGossipMessagingManager lookupGossipBean() {				
	    MBeanServer server = MBeanServerLocator.locate();
		try {
			IGossipMessagingManager messagemanager=(IGossipMessagingManager)MBeanProxyExt.create(IGossipMessagingManager.class, "DeliveryManager:service=GossipMessaging", server);			
			return messagemanager;
		} catch (MalformedObjectNameException e) {			
	        log.error(e.getMessage());	      		    
		}	   
		return null;
	}
	
	
	private void initialSubscribe(FederationEnt fed) {
		log.debug("Starting initial subscription to federation " + fed.getId());
		init();
		//To avoid problems with the db
		clearTimers(fed);
		
//		 If federation is not owned by us, send subscription message and wait. otherwise, we are automatically in
		GossipFederationInfo info=(GossipFederationInfo)fed.getExtraInfo();
		if (fed.isOwnership()) {			
			log.debug("Federation " + fed.getId() + " is owned by us, do nothing");
			info.setStatus(FederationSubscriptionStatus.FEDERATED.getValue());
		//when the federation is created CoordinationManager call join method
			info.setCoordinator_alone(true);
		}
		else {
			//Send subscription message and wait (timeout) for an answer
			log.debug("Sending subscription message for federation " + fed.getId() );
			String contact_address=info.getInitialcontact();
			Subscription s=new Subscription(gossip_manager.getAddress(),contact_address,fed.getId(),gossip_manager.getAddress());
			sender.send(s);
			subtimeout.startTimer(fed.getId());
		}					
	} 
	
	private void startFederationTimers(FederationEnt fed) {
		init();
		beatcheckertimer.startTimer(fed.getId());
		subtimer.startTimer(fed.getId());
		hbeattimer.startTimer(fed.getId());		
	}
	
	private void clearTimers(FederationEnt fed) {
		init();
		beatcheckertimer.stopTimer(fed.getId());
		subtimer.stopTimer(fed.getId());
		hbeattimer.stopTimer(fed.getId());		
	}
	
	@SuppressWarnings("unchecked")
	public Collection<DeliveryManagerGossipInfo> getInView(FederationEnt fed) {
		init();
		GossipFederationInfo ginfo=(GossipFederationInfo)fed.getExtraInfo();
		Collection<DeliveryManagerGossipInfo> dms=ginfo.getDminfo();
		Collection<DeliveryManagerGossipInfo> retu=new Vector<DeliveryManagerGossipInfo>();
		for (DeliveryManagerGossipInfo d:dms) {
			if (d.isInview()) retu.add(d);
			else log.debug(d.getAddress() + " is in federation " + fed.getId() + " but not in the in view");
		}
		return retu;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<DeliveryManagerGossipInfo> getPartialView(FederationEnt fed) {
		init();
		GossipFederationInfo ginfo=(GossipFederationInfo)fed.getExtraInfo();
		//Read new dm info
		manager.refresh(ginfo);
		Collection<DeliveryManagerGossipInfo> dms=ginfo.getDminfo();		
		Collection<DeliveryManagerGossipInfo> retu=new Vector<DeliveryManagerGossipInfo>();
		for (DeliveryManagerGossipInfo d:dms) {
			if (d.isPartialview()) {
				log.debug(d.getAddress() + " is in the partial view of federation " + fed.getId());
				retu.add(d);
			}
			else log.debug(d.getAddress() + " is in federation " + fed.getId() + " but not in the partial view");
		}
		return retu;
		
	}
	
	@SuppressWarnings("unchecked")
	public Collection<DeliveryManagerGossipInfo> getLiveInView(FederationEnt fed) {
		init();
		GossipFederationInfo ginfo=(GossipFederationInfo)fed.getExtraInfo();
		Collection<DeliveryManagerGossipInfo> dms=ginfo.getDminfo();
		Collection<DeliveryManagerGossipInfo> retu=new Vector<DeliveryManagerGossipInfo>();
		for (DeliveryManagerGossipInfo d:dms) {
			if (d.isLiveinview()) retu.add(d);
			else log.debug(d.getAddress() + " is in federation " + fed.getId() + " but not in the live in view");
		}
		return retu;
	}

	public DeliveryManagerGossipInfo getDM(String address, String federationid) {
		init();
		FederationEnt fe=manager.find(FederationEnt.class,federationid);
		if (fe==null) { 
			log.warn("Trying to lookup nonexisting federation: " + federationid);
			return null;
		}
		GossipFederationInfo ginfo=(GossipFederationInfo)fe.getExtraInfo();
		//manager.refresh(ginfo);
		Query q=manager.createNamedQuery(DeliveryManagerGossipInfo.get_dm_by_address);
		q.setParameter("federation",federationid);
		q.setParameter("address",address);
		DeliveryManagerGossipInfo dm_info=null;
		try {
			dm_info=(DeliveryManagerGossipInfo)q.getSingleResult();			
		} catch(NoResultException nr) {
			log.debug("Creating new entry for new Delivery Manager " + address );
			dm_info=new DeliveryManagerGossipInfo(address); 	
			dm_info.setFederationinfo(ginfo);
			ginfo.getDminfo().add(dm_info);			
			manager.persist(dm_info);			
//			manager.refresh(ginfo);
		}	
		log.debug("Delivery Manager information for manager " + address + " already existing");
		return dm_info;		
	}



	public void addService(FederatedPromotion prom, DService serv) {
		init();
		log.info("Promoting service " + serv.getServiceID() + " in federation " + prom.getFederation().getId());
		initializePromotion(prom);
		
		
		//Fail-safe behaviour: do not check whether the service has already been sent, resend it again.
		// Resending it will create no problem
		
		//Broadcast to every federation member
		String federationid=prom.getFederation().getId();		
		
		Collection<DeliveryManagerGossipInfo> partialview=getPartialView(prom.getFederation());
		if (!partialview.isEmpty()){
			log.debug("Partial view of federation " + prom.getFederation().getId() +" is not empty, broadcasting promotion");
			String serviceid=serv.getServiceID();			
			GossipPromotionExtraInfo extra=(GossipPromotionExtraInfo)prom.getExtraInfo();
			//Put service and facets in the payload
			for (DeliveryManagerGossipInfo dm: partialview) {			
				Promotion p=new Promotion(my_address,dm.getAddress(),federationid);
				p.getPayload().add(new ServiceHeader(serviceid,extra.getPromotionTimestamp().getTime()));
				if (serv.getSpecType()!=null) {
					for (FacetSpec spec:serv.getSpecType()) {
						String facetid=spec.getSchemaID();
						p.getPayload().add(new FacetHeader(serviceid,facetid,spec.getDmTimestamp().getTime(),false,extra.getPromotionTimestamp().getTime()));
						FacetSpecXML xml=spec.getFacetSpecificationXML();
						if (xml!=null) {
							p.getPayload().add(new FacetXMLHeader(serviceid,facetid,xml.getXmlID(),extra.getPromotionTimestamp().getTime(),false,spec.getDmTimestamp().getTime()));							
						}
					}
				}
				if (prom.isShareAll()) {
					//add additional
					Collection<FacetAddInfo> addinfo=model_manager.getFacetAdditionalInfo(serviceid);
					for (FacetAddInfo add: addinfo) {
						String facetid=add.getSchemaID();
						p.getPayload().add(new FacetHeader(serviceid,facetid,add.getDmTimestamp().getTime(),true,extra.getPromotionTimestamp().getTime()));
						FacetSpecXML fsxml=add.getFacetSpecificationXML();
						if (fsxml!=null) {
							p.getPayload().add(new FacetXMLHeader(serviceid,facetid,fsxml.getXmlID(),extra.getPromotionTimestamp().getTime(),false,add.getDmTimestamp().getTime()));
						}
					}
					
				}
				sender.send(p);
			}
		} else {
			log.debug("Partial view of federation " + prom.getFederation().getId() +" is empty, could not send service to anyone");
		}
		
		
	}



	public void addFacetSpec(FederatedPromotion prom, FacetSpec facetSpecification) {
		init();
		//Fail-safe behaviour: do not check whether the facet has already been sent, resend it again.
		// Resending it will create no problem
		
		//Broadcast to every federation member
		String federationid=prom.getFederation().getId();		
		
		Collection<DeliveryManagerGossipInfo> partialview=getPartialView(prom.getFederation());
		if (!partialview.isEmpty()) {					
			String serviceid=getServiceId(prom);
			//Put facet in the payload
			GossipPromotionExtraInfo extra=(GossipPromotionExtraInfo)prom.getExtraInfo();
			for (DeliveryManagerGossipInfo dm: partialview) {
				Promotion p=new Promotion(my_address,dm.getAddress(),federationid);
				p.getPayload().add(new FacetHeader(serviceid,facetSpecification.getSchemaID(),facetSpecification.getDmTimestamp().getTime(),false,extra.getPromotionTimestamp().getTime()));				
				sender.send(p);
			}
		}		
	}



	public void addFacetAddInfo(FederatedPromotion prom, FacetAddInfo facetAddInfo) {
		init();
		//Fail-safe behaviour: do not check whether the facet has already been sent, resend it again.
		// Resending it will create no problem
		
		//Broadcast to every federation member
		String federationid=prom.getFederation().getId();
		
		Collection<DeliveryManagerGossipInfo> partialview=getPartialView(prom.getFederation());
		if (!partialview.isEmpty()) {			
			String serviceid=facetAddInfo.getServiceID();
//			Put facet in the payload
			GossipPromotionExtraInfo extra=(GossipPromotionExtraInfo)prom.getExtraInfo();
			for (DeliveryManagerGossipInfo dm: partialview) {
				Promotion p=new Promotion(my_address,dm.getAddress(),federationid);
				p.getPayload().add(new FacetHeader(serviceid,facetAddInfo.getSchemaID(),facetAddInfo.getDmTimestamp().getTime(),true,extra.getPromotionTimestamp().getTime()));				
				sender.send(p);
			}
		}
	}


	public void addFacetXml(FederatedPromotion prom, FacetSpecXML xml, Date dmTimestamp, String facetSchemaId) {
		init();
//		Fail-safe behaviour: do not check whether the facet has already been sent, resend it again.
		// Resending it will create no problem
		
		//Broadcast to every federation member
		String federationid=prom.getFederation().getId();
		
		Collection<DeliveryManagerGossipInfo> partialview=getPartialView(prom.getFederation());
		if (!partialview.isEmpty()) {			
			//Send the facet, not the xml
			String serviceid=getServiceId(prom);
			GossipPromotionExtraInfo extra=(GossipPromotionExtraInfo)prom.getExtraInfo();
			FacetEnt ent=model_manager.lookup(serviceid,facetSchemaId);
			for (DeliveryManagerGossipInfo dm: partialview) {
				Promotion p=new Promotion(my_address,dm.getAddress(),federationid);
				p.getPayload().add(new FacetHeader(serviceid,facetSchemaId,dmTimestamp.getTime(),ent.isAddInfo(),extra.getPromotionTimestamp().getTime()));				
				sender.send(p);
			}
		}
	}


	public void delete(FederatedPromotion prom, ElementEnt elementoCancellato) {
		init();
		//	Fail-safe behaviour: do not check whether the element has already been deleted, resend it again.
		// Resending it will create no problem		
		//Broadcast to every federation member
		Collection<DeliveryManagerGossipInfo> partialview=getPartialView(prom.getFederation());
		//Use Current timestamp for deletion
		long currentTimestamp=new Date().getTime();
		if (!partialview.isEmpty()) {
			for (DeliveryManagerGossipInfo dm: partialview) {
				Deletion d=new Deletion(my_address,dm.getAddress(),prom.getFederation().getId());
				if (elementoCancellato instanceof ServiceEnt) {
					d.getDeletedelements().add(new ServiceHeader(((ServiceEnt)elementoCancellato).getElemPK().getId(),currentTimestamp));
				} else if (elementoCancellato instanceof FacetEnt) {
					FacetEnt cancent=(FacetEnt)elementoCancellato;
					d.getDeletedelements().add(new FacetHeader(cancent.getService().getElemPK().getId(),cancent.getElemPK().getId(),cancent.getTimestamp().getTime(),cancent.isAddInfo(),currentTimestamp));									
				} else if (elementoCancellato instanceof FacetXmlEnt) {
					FacetXmlEnt  cancxml=(FacetXmlEnt)elementoCancellato;
					FacetXMLHeader xmlhead=new FacetXMLHeader(cancxml.getFacet().getService().getElemPK().getId(),cancxml.getFacet().getElemPK().getId(),cancxml.getElemPK().getId(),currentTimestamp,cancxml.getFacet().isAddInfo(),cancxml.getFacet().getTimestamp().getTime());
					d.getDeletedelements().add(xmlhead);
				}
				sender.send(d);
			}
		} else {
			String federationid=prom.getFederation().getId();
			//Create DeletedElement in DB
			if (elementoCancellato instanceof ServiceEnt) {
				ServiceEnt serv=(ServiceEnt)elementoCancellato;
				DeletedService dserv=new DeletedService(federationid,serv.getElemPK().getId(),currentTimestamp);
				manager.persist(dserv);				
			} else if (elementoCancellato instanceof FacetEnt) {
				FacetEnt fent=(FacetEnt)elementoCancellato;
				DeletedFacetSchema dfacet=new DeletedFacetSchema(federationid,fent.getElemPK().getId(),fent.getService().getElemPK().getId(),currentTimestamp);
				manager.persist(dfacet);						
			} else {
				FacetXmlEnt fxmlent=(FacetXmlEnt)elementoCancellato;
				DeletedFacetXML xml=new DeletedFacetXML(federationid,fxmlent.getFacet().getElemPK().getId(),fxmlent.getFacet().getService().getElemPK().getId(),fxmlent.getElemPK().getId(),currentTimestamp);
				manager.persist(xml);				
			}
		}			
	}




	public void leave(FederationEnt federation) {
		init();
		discardPromotions(federation);
		unsubscribe(federation);		
		clearTimers(federation);		
		clearFederationData(federation);		
		
	}

	

 
	public void periodicalResubscribe(String federationid) {
		init();
		//Resubscribe to a member of the partial view
		FederationEnt fed=lookupFederation(federationid);
		if (fed==null) {
			log.error("Could not lookup federation entity with id " +federationid);
			return;
		}
		String id=chooseRandomMember(fed.getId(),new HashSet<DeliveryManagerGossipInfo>());
		if (id==null) {
			log.debug("Periodical resubscription failed because partial view is empty");
		} else {
			Subscription s=new Subscription(gossip_manager.getAddress(),id,fed.getId(),gossip_manager.getAddress());
			sender.send(s);
		}		
	}



	public void isolationResubscribe(String federationid) {
		init();
		//better to restart everything
		restartSubscribe(federationid);		
	}	
	
	private String getServiceId(FederatedPromotion prom) {
		ElementEnt ent=prom.getElement();
		String serviceid=null;
		if (ent instanceof ServiceEnt) {
			serviceid=((ServiceEnt)ent).getElemPK().getId();
		} else if (ent instanceof FacetEnt) {
			serviceid=((FacetEnt)ent).getService().getElemPK().getId();
		} else {
			//must be a FacetXMLEnt (otherwise crash, then remove this comment)
			serviceid=((FacetXmlEnt)ent).getFacet().getService().getElemPK().getId();
		}		
		return serviceid;
	}
	
	private FederationEnt lookupFederation(String federationid) {
		init();
		FederationEnt fed=manager.find(FederationEnt.class,federationid);
		if (fed==null) {
			log.warn("Trying to lookup unexisting federation");
		}
		return fed;
	}



	public void restartSubscribe(String federationid) {
		init();
		FederationEnt fed=lookupFederation(federationid);
		GossipFederationInfo ginfo=(GossipFederationInfo)fed.getExtraInfo();
		if (!ginfo.isCoordinator()) {
			//Send "Restarting" to known nodes			
			for (DeliveryManagerGossipInfo dm_info:getPartialView(fed)) {
				Restarting rest=new Restarting(gossip_manager.getAddress(),dm_info.getAddress(),fed.getId());
				sender.send(rest);
			}
			Collection<DeliveryManagerGossipInfo> in=new HashSet<DeliveryManagerGossipInfo>(getInView(fed));
			in.addAll(getLiveInView(fed));
			HashSet<String> addresses=new HashSet<String>();
			for (DeliveryManagerGossipInfo dm:in) {
				addresses.add(dm.getAddress());
			}
			for (String dm_address:addresses) {
				Restarting rest=new Restarting(gossip_manager.getAddress(),dm_address,fed.getId());
				sender.send(rest);
			}
		}
		initialSubscribe(fed);			
		
	}



	public void subscriptionNotAnswered(String federationid) {
		init();
		//retry subscription
		initialSubscribe(lookupFederation(federationid));
	}



	public void successfulSubscription(String federationid) {
		init();
//		start timers		
		FederationEnt fed=lookupFederation(federationid);
		GossipFederationInfo ginfo=(GossipFederationInfo)fed.getExtraInfo();
		ginfo.setStatus(FederationSubscriptionStatus.FEDERATED.getValue());
		startFederationTimers(fed);	
		//TODO (optional: robustness) Resume pending promotions
		
		
	}



	public int computeHopCounter(String federationid) {	
		init();
			/* The size of partial view converges to (c+1)log(n). We set the hop counter equal to 
			 * the estimated log(n).  
			 */
			FederationEnt fed=lookupFederation(federationid);			
			int log=getPartialView(fed).size();	
			if (log<10) return 10;
			return log;
	}

	public String chooseRandomMember(String federationid,Set<DeliveryManagerGossipInfo> exclude) {
		init();
		FederationEnt fed=lookupFederation(federationid);
		Vector<DeliveryManagerGossipInfo> partial_partialview=new Vector<DeliveryManagerGossipInfo>(getPartialView(fed));
		if (exclude!=null) {
			partial_partialview.removeAll(exclude);
		}
		if (partial_partialview.size()==0) return null;
		int destnum=(int)Math.floor(Math.random()*(partial_partialview.size()));
		DeliveryManagerGossipInfo random=partial_partialview.get(destnum);				
		return random.getAddress();
	}



	public void coordinatorIsNotAlone(String federationid) {
		init();
		startFederationTimers(lookupFederation(federationid));
	}



	public void coordinatorIsAlone(String federationid) {
		init();
//		No timeouts until someone rejoins
		clearTimers(lookupFederation(federationid));
	}
	
	private void initializePromotion(FederatedPromotion prom) {
		init();
		GossipPromotionExtraInfo extra=(GossipPromotionExtraInfo)prom.getExtraInfo();
		if (extra==null) {
			if (getPartialView(prom.getFederation()).size()>0) {
				extra=new GossipPromotionExtraInfo(prom,false,new Date().getTime());
			} else {
				extra=new GossipPromotionExtraInfo(prom,true,new Date().getTime());
			}					
			manager.persist(extra);
			prom.setExtraInfo(extra);
			manager.flush();
		}	
	}



	public Map<String, String> getFederationCreationOptions(String federationid) {
		init();
		HashMap<String,String> options=new HashMap<String,String>();
		options.put("initialcontact",gossip_manager.getAddress());
		return options;
	}



	public void dismissFederation(FederationEnt federation) {
		leave(federation);
	}

	
	private void discardPromotions(FederationEnt federation) {
		init();
		log.debug("Discarding promotions for federation " + federation);
		for (FederatedPromotion prom:federation.getPromotions()) {
			ElementEnt promotedelement=prom.getElement();			
			if (promotedelement instanceof ServiceEnt) {
				ServiceEnt servent=(ServiceEnt)promotedelement;
				log.debug("Discarding service " + servent.getElemPK().getId() + " in federation " + federation.getId());
				Deletion d=new Deletion(gossip_manager.getAddress(),null,federation.getId());
				d.getDeletedelements().add(new ServiceHeader(servent.getElemPK().getId(),new Date().getTime()));
				broadcastMessage(d,federation.getId());				
			} else if (promotedelement instanceof FacetEnt) {				
				FacetEnt facetent=(FacetEnt)promotedelement;
				log.debug("Discarding facet " + facetent.getElemPK().getId() + " in federation " + federation.getId());
				Deletion d=new Deletion(gossip_manager.getAddress(),null,federation.getId());
				d.getDeletedelements().add(new FacetHeader(facetent.getService().getElemPK().getId(),facetent.getElemPK().getId(),facetent.getTimestamp().getTime(),facetent.isAddInfo(),new Date().getTime()));
				broadcastMessage(d,federation.getId());
			} else {
				//must be XML
				FacetXmlEnt xmlent=(FacetXmlEnt)promotedelement;
				log.debug("Discarding xml " + xmlent.getElemPK().getId() + " in federation " + federation.getId());
				Deletion d=new Deletion(gossip_manager.getAddress(),null,federation.getId());
				FacetEnt facetent=xmlent.getFacet();
				d.getDeletedelements().add(new FacetXMLHeader(facetent.getService().getElemPK().getId(),facetent.getElemPK().getId(),xmlent.getElemPK().getId(),new Date().getTime(),facetent.isAddInfo(),facetent.getTimestamp().getTime()));
				broadcastMessage(d,federation.getId());
			}
		}
	}

	public void broadcastMessage(Message m,String federationid) {	
		FederationEnt federation=manager.find(FederationEnt.class,federationid);
		for (DeliveryManagerGossipInfo ginfo:getPartialView(federation)) {
			sender.send(m.createCopy(ginfo.getAddress()));
		}		
	}
}
