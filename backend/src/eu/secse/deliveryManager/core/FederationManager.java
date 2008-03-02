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

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.mx.util.MBeanProxyExt;
import org.jboss.mx.util.MBeanServerLocator;

import eu.secse.deliveryManager.data.ElementEnt;
import eu.secse.deliveryManager.data.ElementEntPK;
import eu.secse.deliveryManager.data.FacetEnt;
import eu.secse.deliveryManager.data.FacetXmlEnt;
import eu.secse.deliveryManager.data.FederatedElement;
import eu.secse.deliveryManager.data.FederatedFacet;
import eu.secse.deliveryManager.data.FederatedPromotion;
import eu.secse.deliveryManager.data.FederatedService;
import eu.secse.deliveryManager.data.FederatedXml;
import eu.secse.deliveryManager.data.FederationEnt;
import eu.secse.deliveryManager.data.ServiceEnt;
import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.exceptions.NotJoinedException;
import eu.secse.deliveryManager.exceptions.PromotionNotValidException;
import eu.secse.deliveryManager.federations.data.Federation;
import eu.secse.deliveryManager.federations.data.FederationProperty;
import eu.secse.deliveryManager.logger.IPerformanceLogger;
import eu.secse.deliveryManager.logger.ISecseLoggerProxy;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.FacetAddInfo;
import eu.secse.deliveryManager.webServices.FederationDetails;
import eu.secse.deliveryManager.webServices.IFederationManagerWS;

@Stateless
@WebService(name = "IFederationManager", serviceName = "FederationManager", targetNamespace = "http://secse.eu/deliveryManager/federationManager")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL, parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
@TransactionAttribute(value=TransactionAttributeType.REQUIRED)
public class FederationManager implements
		IFederationManagerWS {
	
	@PersistenceContext(unitName="deliveryManager")
	private EntityManager em;
	
	@EJB private IFederationCoordinator coordinator;
	@EJB private ModelManager manager;
	
	@EJB private ISecseLoggerProxy secseLogger;
	@EJB private IPerformanceLogger pLogger;
	
	private static final Log log = LogFactory.getLog(IFederationManagerWS.class);	
	
	@SuppressWarnings("unchecked")
	@WebMethod
	public FederationDetails[] getJoinedFederations() {
		log.debug("getting joined federations");
		Query q=em.createNamedQuery(FederationEnt.FIND_ALL);
		List<FederationEnt> fed=q.getResultList();
		FederationDetails[] fedids=new FederationDetails[fed.size()];
		int i=0;
		while (i < fed.size() ) {
			fedids[i]=new FederationDetails(fed.get(i).getId(),fed.get(i).getName(),fed.get(i).getMethod());
			i++;
		}
		return fedids;
	}
	
	/**
	 * 
	 * Get all federation details: name, id and type of 
	 * all the federations holded by the quicker Federation Directory 
	 * 
	 * @return allFederationDetails the information of all federations
	 */
	@SuppressWarnings("unchecked")
	@WebMethod
	public FederationDetails[] getAllFederations(){
		log.debug("Requesting all federations");
		FederationDetails[] allFederations = null;
		MBeanServer server = MBeanServerLocator.locate();
		try{
			IFederationCoordinationManager coordmanager = (IFederationCoordinationManager)MBeanProxyExt.create(IFederationCoordinationManager.class, "DeliveryManager:service=federationCoordinator", server);
			coordmanager.synchronizeWithDirectory();}
		
			catch (MalformedObjectNameException e) {			
				log.error(e.getMessage());	      		    
			}
			em.flush();
			Query q = em.createNamedQuery(Federation.getall);
			Collection<Federation>  federations = q.getResultList();
			allFederations = new FederationDetails[federations.size()];
			int k = 0;
			for(Federation f : federations){
				allFederations[k] = new FederationDetails(f.getId(),f.getName(),f.getMethod());
				k++;
			}
			return allFederations;
		
	}

	@WebMethod
	public void joinFederation(@WebParam(name="federationId")String federationId) {		
		log.debug("Joining the federation " + federationId);	
		secseLogger.event("Joining the federation " + federationId);
		if(pLogger.isEnabled()) pLogger.log(System.currentTimeMillis(), "FM_JOIN", federationId);
		
		Federation fed=em.find(Federation.class,federationId);
		if (fed==null) {
			//lookup federation coordination manager and refresh
			  MBeanServer server = MBeanServerLocator.locate();
				try {
					log.debug("Federation not found, trying synchronization");
					IFederationCoordinationManager coordmanager=(IFederationCoordinationManager)MBeanProxyExt.create(IFederationCoordinationManager.class, "DeliveryManager:service=federationCoordinator", server);			
					coordmanager.synchronizeWithDirectory();					
				} catch (MalformedObjectNameException e) {			
			        log.error(e.getMessage());	      		    
				}	   
				em.flush();
				fed=em.find(Federation.class,federationId);
				if (fed==null) {
					log.error("Could not join federation " + federationId + ". It is not in the directory");
					return;
				}
		}
			
		FederationProxy proxy=coordinator.getProxy(fed.getMethod());
		if (proxy==null) {
			log.error("Could not find proxy for method " + fed.getMethod());
		}
		FederationEnt fedent=em.find(FederationEnt.class,fed.getId());					
		if (fedent!=null) {
			//already joined!
			log.warn("Already joined federation " + fed.getId() + ", not rejoining");
			return;
		}
		FederationEnt ent=new FederationEnt(fed.getId(),fed.getName(),fed.getLease(),fed.isOwnership(),fed.getMethod(), new Vector<FederatedElement>(),null );
		em.persist(ent);
		em.flush();
		HashMap<String,String> options=new HashMap<String,String>();
		for (FederationProperty prop:fed.getProperties()) {
			options.put(prop.getName(),prop.getValue());
		}
		proxy.join(ent,options);		
	}
	
	


	@WebMethod
	public boolean isJoinedFederation(@WebParam(name="federationId")String federationId) {		
		return (em.find(FederationEnt.class,federationId)!=null);
	}

//	private Federation getFederationByName(String name) {
//		Federation fed=null;
//		Query byname=em.createNamedQuery(Federation.byname);
//		byname.setParameter("name",name);
//		List<Federation> federations=(List<Federation>)byname.getResultList();
//		if (federations.size()==0) {
//			log.error("No federation with name " + name + " found");
//			return null;
//		}
//		if (federations.size()>1) {
//			 fed=federations.get(0);
//			log.warn("More than one federation with name" + name + ". Joining federation with id " + fed.getId());
//		
//		}
//		return fed;
//	}
	
	@WebMethod
	public void discardPromotion(@WebParam(name="promotionId")long promotionId) throws NotFoundException {		
		log.debug("discarding promotion "+promotionId);
		log.warn("discarding promotion "+promotionId);
		
		FederatedPromotion p=em.find(FederatedPromotion.class,promotionId);
		if (p==null) {
			log.warn("Trying to dismiss nonexisting promotion " + promotionId);	
			throw new NotFoundException("Trying to dismiss nonexisting promotion " + promotionId);
		}
		ElementEnt ent=p.getElement();
		if (ent instanceof ServiceEnt) {
			ServiceEnt serv=(ServiceEnt)ent;
			FederatedService fserv=manager.lookup(serv.getElemPK().getId(),p.getFederation());
			log.debug("Deleting federated service");
			manager.delete(fserv);
		} else if (ent instanceof FacetEnt) {
			FacetEnt fent=(FacetEnt)ent;
			FederatedFacet ffacet=manager.lookup(fent.getService().getElemPK().getId(),fent.getElemPK().getId(),p.getFederation());
			log.debug("Deleting federated facet");
			manager.delete(ffacet);
		} else  {
			//Must be XML
			FacetXmlEnt xent=(FacetXmlEnt)ent;
			FacetEnt fent=xent.getFacet();
			FederatedXml fxml=manager.lookup(fent.getService().getElemPK().getId(),fent.getElemPK().getId(),xent.getElemPK().getId(),p.getFederation());
			log.debug("Deleting federated xml");
			manager.delete(fxml);
		}
		em.remove(p);
	}

	@WebMethod
	public PromotionDetails getDetails(@WebParam(name="promotionId")long promotionId) {
		log.debug("Getting promotion details of "+promotionId);
		FederatedPromotion prom=em.find(FederatedPromotion.class,promotionId);
		if (prom==null) {
			log.warn("Requested details of nonexisting promotion: " + promotionId);
			return null;
		}
		ElementEnt promele=prom.getElement();
		if (promele instanceof ServiceEnt) { 
			ServiceEnt sent=(ServiceEnt)promele;
			PromotionDetails det=new PromotionDetails(promotionId,prom.getFederation().getId(),sent.getElemPK().getId(),prom.isShareAll());
			return det;
		} else if (promele instanceof FacetEnt){
			FacetEnt fent=(FacetEnt)promele;
			PromotionDetails det=new PromotionDetails(promotionId,prom.getFederation().getId(),fent.getService().getElemPK().getId(),fent.getElemPK().getId());
			return det;
		}
		log.debug("Unrecognized promoted element, returning null");
		return null;
	}

	@SuppressWarnings("unchecked")
	@WebMethod
	public long[] getPromotions(@WebParam(name="federationId")String federationName) throws NotJoinedException {
		log.debug("Getting promotions of federation "+federationName);
		Query q=em.createNamedQuery(FederatedPromotion.getByFederationEnt);
		FederationEnt fed=em.find(FederationEnt.class,federationName);
		if (fed==null) {
			throw new NotJoinedException(federationName);
		}
		q.setParameter("federation",fed);
		Collection<FederatedPromotion> promotions=q.getResultList();
		long[] proms=new long[promotions.size()];
		int i=0;
		for (FederatedPromotion prom:promotions) {
			proms[i++]=prom.getId();
		}
		return proms;
	}

	@WebMethod
	public void leaveFederation(@WebParam(name="federationId")String federationId) throws NotJoinedException {
		log.debug("Leaving federation " + federationId);
		if(pLogger.isEnabled()) pLogger.log(System.currentTimeMillis(), "FM_LEAVE", federationId);

		
		FederationEnt fed=em.find(FederationEnt.class,federationId);
		if (fed==null) throw new NotJoinedException(federationId);
		FederationProxy proxy=coordinator.getProxy(fed.getMethod());
		if (fed.isOwnership()) {
			proxy.dismissFederation(fed);			
			coordinator.dismissFederation(fed.getId());				
		} else {
			proxy.leave(fed);
		}
		
//		Vector<FederatedPromotion> promotions=new Vector<FederatedPromotion>(fed.getPromotions());
//		fed.getPromotions().clear();
//		
//		for (FederatedPromotion prom:promotions) {
//			em.remove(prom);
//		}
//		
//		Vector<FederatedElement> federatedelements=new Vector<FederatedElement>(fed.getElements());
//			
//		fed.getElements().clear();
//		//First remove xmls,  then facets, then services;		
//		for (FederatedElement fele:federatedelements) {
//			if (fele instanceof FederatedXml) em.remove(fele);
//		}
//		for (FederatedElement fele:federatedelements) {
//			if (fele instanceof FederatedFacet) em.remove(fele);
//		}
//		for (FederatedElement fele:federatedelements) {
//			if (fele instanceof FederatedService) em.remove(fele);
//		}
//		
//		fed.getPromotions().clear();		
		em.remove(fed);
	}

	/* Promuove le FacetAddInfo
	 * 
	 * PRECONDIZIONI:
	 * Verifica se è OK la condivisione: (esiste ServiceEnt) && (esiste FederationEnt) &&
	 * ( (esiste FederatedService(received == true)) XOR (esiste FederatedPromotion(shareAll == false) sul servizio)) &&
	 * (ServiceEnt.own == true || servEnt.allowAddInfo == true)
	 * 
	 * AZIONI:
	 * -Creare la FederatedPromotion per la Facet e, nel caso in cui non esiste la FacetEnt, la crea.
	 * -Manda in giro la promozione
	 * -Crea FederatedFacet(received = false). Nel caso in cui non esista il FederatedService, lo crea con received a false,
	 *  altrimenti se esiste il FederatedService deve avere per forza il received a true. 
	 * 
	 * (non-Javadoc)
	 * @see eu.secse.deliveryManager.webServices.IFederationManagerWS#promoteFacetSpecification(java.lang.String, java.lang.String, java.lang.String)
	 */
	
	@WebMethod
	public long promoteFacetSpecification(@WebParam(name="serviceId")String serviceId, @WebParam(name="facetSpecificationSchemaId")String facetSpecificationSchemaId,@WebParam(name="federationId") String federationId) throws NotJoinedException, NotFoundException, PromotionNotValidException {
		log.debug("promoting facet specification "+facetSpecificationSchemaId+ " for service "+serviceId+" in federation "+federationId);
		secseLogger.event("promoting facet specification "+facetSpecificationSchemaId+ " for service "+serviceId+" in federation "+federationId);
		if(pLogger.isEnabled()) pLogger.log(System.currentTimeMillis(), "FM_PROMOTE_ADDINFO: " + federationId, facetSpecificationSchemaId);
		
		//verifies if FederationEnt exists
		FederationEnt fed=em.find(FederationEnt.class,federationId);
		if (fed==null) throw new NotJoinedException("Federation " + federationId + " is not joined");

		//!!!!Codice vecchio di Antonio, ora verifico semplicemente che ServiceEnt esista, altrimenti do
		// un messaggio di errore
		//----------------------------------------------------
		//if ServiceEnt doesn't exist, ModelManager creates a new one, retrieving the respecting Service
		//in the registry. Otherwise return the already created ServiceEnt
		/*ServiceEnt servent =  manager.add(serviceId);
		if (servent==null) {
			throw new NotFoundException("The service with id " + serviceId + " is not shared");
		}------------------------------------------------------*/
		
		//verifies if ServiceEnt exists
		ServiceEnt servEnt = manager.lookup(serviceId);
		if (servEnt==null) {
			throw new NotFoundException("The service with id " + serviceId + " is not shared");
		}
		if(!servEnt.isOwnership() && !servEnt.isAllowedAddInfo()){
			throw new PromotionNotValidException("Cannot promote FacetAddInfo for the service "+ serviceId+ " because Delivery Manager is not the owner of the service and can't share add info");
		}
		//Check the promotion to see wether the service is promoted with facets...
		if (manager.isPromotedWithFacets(serviceId,federationId)) {
			throw new PromotionNotValidException("The service with id " + serviceId + " is already sharing all his facets");
		}
		boolean condition1, condition2;
		condition1 = condition2 = false;
		FederatedService fedService =  manager.lookup(serviceId, fed);
		if(fedService!= null){
			if(fedService.isReceived()){
				condition1 = true;
				log.info("The service "+ serviceId + " is created by another registry of the federation.");
			}
		}
		
		FederatedPromotion fedProm = manager.lookup(servEnt, fed);
		if(fedProm!= null){
			if(fedProm.isShareAll() == false){
				log.info("The service "+ serviceId + " is created by this registry and promoted in  the federation.");
				condition2 = true;
			}	
		}
		if(condition1 ^ condition2){
			//execute actions
			//Retrieves FacetEnt. If it doesn't exist create a new one, and retrieves also possible FacetXmlEnt
			FacetAddInfo fspec=manager.getFacetAdditionalData(serviceId,facetSpecificationSchemaId);
			
			//Create FederatedPromotion
			FederatedPromotion fp=new FederatedPromotion(fed,servEnt,null,false);		
			em.persist(fp);
			em.flush();
			
			//	Create the federated facet
			FederatedFacet facet=manager.retrieve(serviceId,facetSpecificationSchemaId,fed, false);
			facet.setReceived(false);
			
			//publish the promotion
			FederationProxy proxy=coordinator.getProxy(fed.getMethod());
			proxy.addFacetAddInfo(fp,fspec);
			return fp.getId();
		}
		else{
			if(condition1 && condition2) throw new PromotionNotValidException("Cannot promote FacetAddInfo for the service "+ serviceId+" because neither FederatedPromotion nor FederatedService exists for this service");
			else throw new PromotionNotValidException("Cannot promote FacetAddInfo for the service "+ serviceId+" because both FederatedPromotion and FederatedService exists for this service");
		}
		
	}
	
	/* Promuove il servizio con tutte le Facet di specifica e le FacetAddInfo
	 * 
	 * PRECONDIZIONI:
	 *(esiste ServiceEnt || esiste il servizio nel registro ) && (esiste FederationEnt) && ((non esiste FederatedService && non esiste FederatedPromotion) su quel servizio e su quella federazione)
	 * 
	 * AZIONI:
	 * - Crea FederatedPromotion per il servizio (con shareAll = true)
	 * - Manda in giro la promozione
	 * - Crea FederatedService con received = false.
	 * 
	 * (non-Javadoc)
	 * @see eu.secse.deliveryManager.webServices.IFederationManagerWS#promoteServiceSpecifications(java.lang.String, java.lang.String)
	 */
	@WebMethod
	public long promoteServiceFacets(@WebParam(name="serviceId")String serviceId, @WebParam(name="federationId")String federationId) throws NotJoinedException, NotFoundException, PromotionNotValidException {
		log.debug("Promoting service "+serviceId+ "with all its facets in federation "+federationId);
		secseLogger.event("Promoting service "+serviceId+ "with all its facets in federation "+federationId);
		if(pLogger.isEnabled()) pLogger.log(System.currentTimeMillis(), "FM_PROMOTE_ALL: " + federationId, serviceId);
		
		//verifies if FederationEnt exists
		FederationEnt fed=em.find(FederationEnt.class,federationId);
		if (fed==null) throw new NotJoinedException("Federation " + federationId + " is not joined");
		
		DService service=manager.getServiceData(serviceId);
		//Verifies if exists ServiceEnt or the service exists in the registry
		ServiceEnt servele=  manager.add(serviceId);
		if (servele==null) {
			throw new NotFoundException("The service with id " + serviceId + " can not be shared (modelmanager error)");
		}
		//Verifies that FederatedService doesn't exist
		FederatedService fedService = manager.lookup(serviceId, fed);
		if(fedService!= null) throw new PromotionNotValidException("Cannot promote the service with id "+serviceId+ ": a FederatedService entity already exists for this service ");
		//Verifies that FederatedPromotion doesn't exist
		FederatedPromotion fp = manager.lookup(servele, fed);
		if(fp!= null) throw new PromotionNotValidException("Cannot promote the service with id "+serviceId+ ": the service has been already promoted");
		
		//creo la promozione
		fp=new FederatedPromotion(fed,servele,null,true);
		em.persist(fp);
		em.flush();
		
//		Create the federated service, if not there with set received a true
		manager.retrieve(serviceId,fed, false);			
		
		FederationProxy proxy=coordinator.getProxy(fed.getMethod());
		proxy.addService(fp,service);
		return fp.getId();
	}
	
	/* Promuove il servizio senza le FacetAddInfo
	 * 
	 * PRECONDIZIONI:
	 *(esiste ServiceEnt || esiste il servizio nel registro) && (esiste FederationEnt) && ((non esiste FederatedService && non esiste FederatedPromotion) su quel servizio e su quella federazione)
	 * 
	 * AZIONI:
	 * - Crea FederatedPromotion per il servizio (con shareAll = false)
	 * - Manda in giro la promozione
	 * - Crea FederatedService con received = false.
	 * 
	 * (non-Javadoc)
	 * @see eu.secse.deliveryManager.webServices.IFederationManagerWS#promoteServiceSpecifications(java.lang.String, java.lang.String)
	 */
	@WebMethod
	public long promoteServiceSpecifications(@WebParam(name="serviceId")String serviceId, @WebParam(name="federationId")String federationId) throws NotJoinedException, NotFoundException, PromotionNotValidException {
		//verifies if FederationEnt exists
		log.debug("Promoting service " + serviceId + " in federation " + federationId);
		secseLogger.event("Promoting service " + serviceId + "with specification facets in federation " + federationId);
		if(pLogger.isEnabled()) pLogger.log(System.currentTimeMillis(), "FM_PROMOTE: " + federationId, serviceId);
		
		FederationEnt fed=em.find(FederationEnt.class,federationId);
		if (fed==null) throw new NotJoinedException("Federation " + federationId + " is not joined");
		
//		Verifies if exists ServiceEnt or the service exists in the registry
		log.debug("Verifies if exists SerivceEnt " + serviceId + " or the service exists in the registry");
		DService service=manager.getServiceData(serviceId);
		ServiceEnt  serviceEnt =  manager.add(serviceId);
		if (serviceEnt==null) {
			throw new NotFoundException("The service with id " + serviceId + " can not be shared (modelmanager error)");
		}
		
//		Verifies that FederatedService doesn't exist
		log.debug("Verifies that FederatedService doesn't exist in federation" + federationId);
		FederatedService fedService = manager.lookup(serviceId, fed);
		if(fedService!= null) throw new PromotionNotValidException("Cannot promote the service with id "+serviceId+ ": a FederatedService entity already exists for this service ");
		//Verifies that FederatedPromotion doesn't exist
		log.debug("Verifies that FederatedPromotion doesn't exist");
		FederatedPromotion fp = manager.lookup(serviceEnt, fed);
		if(fp!= null) throw new PromotionNotValidException("Cannot promote the service with id "+serviceId+ ": the service has been already promoted");
		
		fp = new FederatedPromotion(fed,serviceEnt,null,false);
		em.persist(fp);
		em.flush();
		
		//Create the federated service with set received to false
		log.debug("Create the federated service with set received to false");
		manager.retrieve(serviceId,fed, false);
		em.flush();

		FederationProxy proxy=coordinator.getProxy(fed.getMethod());
		log.debug("Asking proxy to promote service " + serviceId + " in federation " + federationId);
		proxy.addService(fp,service);
		return fp.getId();
	}

	@WebMethod
	public PromotionDetails getPromotionByServiceId(@WebParam(name="federationId")String federationName, @WebParam(name="serviceId") String serviceId) throws NotJoinedException, NotFoundException {
		log.debug("Getting promotion by service id "+serviceId+" in federation "+federationName);
		FederationEnt fed=getFed(federationName);
		
		ServiceEnt sent=em.find(ServiceEnt.class,new ElementEntPK(serviceId, ServiceEnt.SERVICE_ENT));
		if (sent==null) {
			throw new NotFoundException("Service with id " + serviceId + " not found");
		}
		Query q=em.createNamedQuery(FederatedPromotion.getByElementAndFederation);
		q.setParameter("federation",fed);
		q.setParameter("element",sent);
		FederatedPromotion fp=(FederatedPromotion)q.getSingleResult();
		if (fp==null) {
			throw new NotFoundException("The selected promotion does not exist");
		}
		long id=fp.getId();
		PromotionDetails details=new PromotionDetails(id,fed.getId(),sent.getElemPK().getId(),fp.isShareAll());
		return details;
	}

	@WebMethod
	public PromotionDetails getPromotionByFacetSchemaId(@WebParam(name="federationId")String federationName, @WebParam(name="serviceId")String serviceId, @WebParam(name="facetSchemaId")String facetSchemaId) throws NotJoinedException, NotFoundException {
		log.debug("Getting promotion by facet schema id "+facetSchemaId+ " in federation "+federationName);
		FederationEnt fed=getFed(federationName);
		FacetEnt fent=em.find(FacetEnt.class, new ElementEntPK(facetSchemaId, FacetEnt.FACET_ENT));
		if (fent==null) {
			throw new NotFoundException("Facet with id " + facetSchemaId + " not found");
		}
		Query q=em.createNamedQuery(FederatedPromotion.getByElementAndFederation);
		q.setParameter("federation",fed);
		q.setParameter("element",fent);
		FederatedPromotion fp=(FederatedPromotion)q.getSingleResult();
		if (fp==null) {
			throw new NotFoundException("The selected promotion does not exist");
		}
		long id=fp.getId();
		PromotionDetails details=new PromotionDetails(id,fed.getId(),serviceId,facetSchemaId);
		return details;		
	}

	@WebMethod
	public String[] getSupportedTypology() {
		log.debug("Getting supported tipology");
		return coordinator.getSupportedMethods();		
	}

	@WebMethod
	public String createFederation(@WebParam(name="name")String name, @WebParam(name="type")String type) throws NotJoinedException {
		// new federation id
		String id=System.currentTimeMillis()+"£"+name+"£"+type;

		log.debug("Creating federation "+name+" of type "+type);
		secseLogger.event("Creating federation "+name+" of type "+type);
		if(pLogger.isEnabled()) pLogger.log(System.currentTimeMillis(), "FM_CREATE", id);
		
		Federation f=new Federation();
		f.setMethod(type);
		f.setName(name);
		//TODO use a more robust federation id generation algorithm
		Calendar c=Calendar.getInstance();
		c.add(Calendar.WEEK_OF_MONTH,1);		
		f.setLease(c.getTime());
		c.add(Calendar.DAY_OF_MONTH,-1);
		f.setRenew(c.getTime());		
		f.setOwnership(true);
		f.setId(id);
		FederationProxy proxy=coordinator.getProxy(type);
		Map<String,String> options=proxy.getFederationCreationOptions(id);
		Collection<FederationProperty> props=new Vector<FederationProperty>();
		for (Entry<String,String> e:options.entrySet()) {
			FederationProperty prop=new FederationProperty(e.getKey(),e.getValue());
			props.add(prop);
		}
		f.setProperties(props);
		f.setOwnership(true);
		em.persist(f);
		em.flush();
		coordinator.createFederation(f);
		joinFederation(id);
		return id;		
	}

	private FederationEnt getFed(String federationId) throws NotJoinedException {
		FederationEnt fed=em.find(FederationEnt.class,federationId);
		if (fed==null) throw new NotJoinedException("Federation " + federationId + " is not joined");
		return fed;
	}
	
	@WebMethod
	@SuppressWarnings("unchecked")
	public FederationDetails[] searchFederationByName(@WebParam(name="nameRegExp")String nameRegExp) {
		FederationDetails[] matchingFederations = null;
		Query q = em.createNamedQuery(Federation.like);
		q.setParameter("name", nameRegExp);
		Collection<Federation> federations = q.getResultList();
		matchingFederations = new FederationDetails[federations.size()];
		int k=0;
		for(Federation f: federations){
			matchingFederations[k] = new FederationDetails(f.getId(), f.getName(), f.getMethod());
		}
		return matchingFederations;
	}

	@WebMethod
	public FederationDetails searchFederationByUID(@WebParam(name="uid")String uid) {
		FederationDetails matchingFederation = null;
		Query q = em.createNamedQuery(Federation.byUID);
		q.setParameter("id", uid);
		Federation  f = (Federation)q.getSingleResult();
		matchingFederation = new FederationDetails(f.getId(),f.getName(),f.getMethod());
		return matchingFederation;
	}
}
