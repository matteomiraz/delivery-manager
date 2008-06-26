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

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.secse.deliveryManager.data.ElementEnt;
import eu.secse.deliveryManager.data.ElementExtraInfo;
import eu.secse.deliveryManager.data.FacetEnt;
import eu.secse.deliveryManager.data.FacetXmlEnt;
import eu.secse.deliveryManager.data.ServiceEnt;
import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.exceptions.NotSubscribedException;
import eu.secse.deliveryManager.interest.InterestAdditionalInformation;
import eu.secse.deliveryManager.interest.InterestAdditionalInformationId;
import eu.secse.deliveryManager.interest.InterestService;
import eu.secse.deliveryManager.interest.MultipleInterestSpecificationFacet;
import eu.secse.deliveryManager.logger.IPerformanceLogger;
import eu.secse.deliveryManager.logger.ISecseLoggerProxy;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.FacetAddInfo;
import eu.secse.deliveryManager.registry.IRegistryProxy;
import eu.secse.deliveryManager.sharing.core.IInterestManager;
import eu.secse.deliveryManager.sharing.core.IShareManager;
import eu.secse.deliveryManager.sharing.data.CreatedElementExtraInfo;
import eu.secse.deliveryManager.sharing.data.ReceivedElementExtraInfo;
import eu.secse.deliveryManager.sharing.data.filters.InterestEnt;
import eu.secse.deliveryManager.sharing.data.filters.InterestFacetEnt;
import eu.secse.deliveryManager.sharing.data.filters.InterestServiceEnt;
import eu.secse.deliveryManager.sharing.data.filters.InterestFacetEnt.FacetInterestType;
import eu.secse.deliveryManager.webServices.GenericInterest;

@Stateless
@WebService(name = "IDeliveryManager", serviceName = "DeliveryManager", targetNamespace = "http://secse.eu/deliveryManager/deliveryManager")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT, use=SOAPBinding.Use.LITERAL, parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
public class DeliveryManager implements IDeliveryManager  {
	
	private static final Log log = LogFactory.getLog(IDeliveryManager.class);
	
	@EJB private IRegistryProxy registry;
	@EJB private ModelManager modelManager;
	@EJB private IShareManager shareManager;
	@EJB private IInterestManager interestManager;

	@EJB private ISecseLoggerProxy secseLogger;
	@EJB private IPerformanceLogger pLogger;
	
	@PersistenceContext(unitName="deliveryManager")
	protected EntityManager em;
	
	@WebMethod public long[] getAllInterestIds() {
		log.debug("Getting all interests' ids");
		Collection<InterestEnt> interests = InterestEnt.getAllInterests(em);
		long[] ids = new long[interests.size()];
		int k = 0;
		for(InterestEnt i: interests)
			ids[k++] = i.getId();
		return ids;
	}

	@WebMethod public ShareDetails getShareDetails(@WebParam(name="sharingId") long id) throws NotFoundException{
		log.debug("getting share details ");
		CreatedElementExtraInfo elemExtraInfo = em.find(CreatedElementExtraInfo.class, id);
		if(elemExtraInfo!=null){
			ShareDetails shareDetails = new ShareDetails(elemExtraInfo.getId(), elemExtraInfo.getInfoType(), elemExtraInfo.getElement().getElemPK().getId(),
					elemExtraInfo.getElement().getElemPK().getId(), elemExtraInfo.isKeepsAlive(), elemExtraInfo.getNotifier(), elemExtraInfo.isSharing(),
					elemExtraInfo.isShareAll());
			return shareDetails;
		}
		else throw new NotFoundException("Impossible retrieve ElementExtraInfo with id "+id);
		
	}
	
	@WebMethod public void unshare(@WebParam(name="sharingId")long id) throws NotFoundException{
		log.debug("Unsharing element "+id);
		secseLogger.event("Unsharing element "+id);
		
		CreatedElementExtraInfo elemExtraInfo = em.find(CreatedElementExtraInfo.class, id);
		if(elemExtraInfo!=null) {
			ElementEnt element = elemExtraInfo.getElement();
			
			if(pLogger.isEnabled()) pLogger.log(System.currentTimeMillis(), "DM_UNSHARE", element.getElemPK().getId());
			
			if(element instanceof ServiceEnt)
				modelManager.getGenericProxy(elemExtraInfo).delete((ServiceEnt)element);
			else{
				if(element instanceof FacetEnt)
					modelManager.getGenericProxy(elemExtraInfo).delete((FacetEnt)element);
				else{
					if(element instanceof FacetXmlEnt)
						modelManager.getGenericProxy(elemExtraInfo).delete((FacetXmlEnt)element);
					else log.error("Impossible unshare element "+element.getElemPK().getId());
				}
			}
			em.remove(elemExtraInfo);
		}
		else throw new NotFoundException("Impossible retrieve ElementExtraInfo with id "+id);
	}
	
	@WebMethod public GenericInterest getInterestById(@WebParam(name="interestId")long id)
			throws NotSubscribedException {
		log.debug("getting interest by id "+id);
		InterestEnt interest = em.find(InterestEnt.class, id);
		if(interest==null) return null;
		else{
			if (interest instanceof InterestServiceEnt) {
				InterestServiceEnt iServiceEnt = (InterestServiceEnt) interest;
				return new GenericInterest(iServiceEnt.getServiceID(),null,null,false,iServiceEnt.getDescription());
				
			}
			else{
				InterestFacetEnt iFacetEnt = (InterestFacetEnt)interest;
				return new GenericInterest(iFacetEnt.getServiceId(),iFacetEnt.getFacetSchemaId(),iFacetEnt.getFacetInterest(),iFacetEnt.isAdditionalInformation(),iFacetEnt.getDescription());
			}
		}
	}

	@WebMethod public long shareAllServiceAdditionalInformations(@WebParam(name="serviceId") String serviceId) throws NotFoundException {
		log.debug("Sharing service " + serviceId + " and all its facets"); 
		secseLogger.event("Sharing service " + serviceId + " and all its facets");
		if(pLogger.isEnabled()) pLogger.log(System.currentTimeMillis(), "DM_SHARE_ALL_ADDINFO", serviceId);

		// make sure that I can share the service: it is created originally in the corrent registry
		if(!registry.isServiceLocallyCreated(serviceId))
				throw new NotFoundException("You cannot share a service created by another registry");
		
		ServiceEnt  serviceEnt =  modelManager.add(serviceId);
		if (serviceEnt==null) {
			throw new NotFoundException("The service with id " + serviceId + " cannot be shared (modelmanager error)");
		}
		
		CreatedElementExtraInfo elementExtraInfo = new CreatedElementExtraInfo(serviceEnt, CreatedElementExtraInfo.INFO_TYPE, false, true, null);
		elementExtraInfo.setShareAll(true);
	
		serviceEnt.getExtraInfo().put(CreatedElementExtraInfo.INFO_TYPE, elementExtraInfo);
		em.persist(elementExtraInfo);
		em.flush();
		DService dSrv = modelManager.getServiceData(serviceId);	
		shareManager.add(dSrv, serviceEnt);	
		return elementExtraInfo.getId();
		
	}

	@WebMethod public long shareServiceAdditionalInformation(@WebParam(name="serviceId")String serviceId, @WebParam(name="facetSchemaId")String facetSchemaId) throws NotFoundException {
		log.debug("Sharing additional information facet with schema id "  + facetSchemaId + " (service " + serviceId + ")"); 
		
		secseLogger.event("Sharing additional information facet with schema id "  + facetSchemaId + " (service " + serviceId + ")"); 

		if(pLogger.isEnabled()) pLogger.log(System.currentTimeMillis(), "DM_SHARE_ADDINFO", facetSchemaId);
		
		// make sure that I can share the facet (part 1): it is created originally in the corrent registry
		if(!registry.isFacetLocallyCreated(facetSchemaId))
			throw new NotFoundException("You cannot share a facet created by another registry");
		 
		
		// make sure that I can share the facet (part 2): the service is 
		//  * local and not share all its facets
		//  * remote and addinfo is true
		ServiceEnt srv = ServiceEnt.searchByID(em, serviceId);
		if(srv==null) throw new NotFoundException("The service with id " + serviceId + " cannot be shared (modelmanager error)");
		
		boolean shareFacet = false;
		//retrieve the value of shareAll from ElementExtraInfo
		ElementExtraInfo serviceExtraInfo = srv.getExtraInfo().get(ReceivedElementExtraInfo.INFO_TYPE); 
		if(serviceExtraInfo== null){
			serviceExtraInfo = srv.getExtraInfo().get(CreatedElementExtraInfo.INFO_TYPE); 
			if(serviceExtraInfo!= null)
				shareFacet = serviceExtraInfo.isShareAll();
		}
		else shareFacet = serviceExtraInfo.isShareAll();
	
		if(srv.isOwnership() && shareFacet)
			throw new NotFoundException("The service already shares all its facets");

		if(!srv.isOwnership() && !srv.isAllowedAddInfo())
			throw new NotFoundException("The publisher does not allows to add additional facets to this service: you cannot share them to the other registries");

		
		// store in the db the sharing of the facet
		FacetEnt facetEnt = modelManager.add(serviceId,facetSchemaId);
		if (facetEnt==null) {
			throw new NotFoundException("The facet with id " + facetSchemaId + " cannot be shared (modelmanager error)");
		}
		
		CreatedElementExtraInfo elementExtraInfo = new CreatedElementExtraInfo(facetEnt, CreatedElementExtraInfo.INFO_TYPE, false, true, null);
		elementExtraInfo.setShareAll(true);
		facetEnt.getExtraInfo().put(CreatedElementExtraInfo.INFO_TYPE, elementExtraInfo);
		em.persist(elementExtraInfo);
		em.flush();
		FacetAddInfo fspec=modelManager.getFacetAdditionalData(serviceId,facetSchemaId);
		shareManager.add(fspec, facetEnt);
		return elementExtraInfo.getId();
	}	
		

	@WebMethod public long shareServiceSpecifications(@WebParam(name="serviceId") String serviceId) throws NotFoundException {
		log.debug("Sharing service " + serviceId+ " with specification facet "); 
		secseLogger.event("Sharing service " + serviceId+ " with specification facet ");
		if(pLogger.isEnabled()) pLogger.log(System.currentTimeMillis(), "DM_SHARE_SERVICESPEC", serviceId);

		
		// make sure that I can share the service: it is created originally in the corrent registry
		if(!registry.isServiceLocallyCreated(serviceId))
				throw new NotFoundException("You cannot share a service created by another registry");
		
		
		//	Verifies if exists ServiceEnt or the service exists in the registry
		log.debug("Verifies if exists SerivceEnt " + serviceId + " or the service exists in the registry");
		
		ServiceEnt  serviceEnt =  modelManager.add(serviceId);
		if (serviceEnt==null) {
			throw new NotFoundException("The service with id " + serviceId + " cannot be shared (modelmanager error)");
		}
		
		DService dSrv = modelManager.getServiceData(serviceId);
		
		CreatedElementExtraInfo elementExtraInfo = new CreatedElementExtraInfo(serviceEnt, CreatedElementExtraInfo.INFO_TYPE, false, true, null);
		elementExtraInfo.setShareAll(false);
		serviceEnt.getExtraInfo().put(CreatedElementExtraInfo.INFO_TYPE, elementExtraInfo);
		em.persist(elementExtraInfo);
		em.flush();

//		DService dSrv = modelManager.getServiceData(serviceId);
		shareManager.add(dSrv, serviceEnt);
		return elementExtraInfo.getId();

	}

	@WebMethod public long subscribeAdditionalInformationFacet(@WebParam(name="serviceId") String serviceId, @WebParam(name="facetSchema")String facetSchema, @WebParam(name="xpath")String xpath, @WebParam(name="description")String description) throws NotSubscribedException {
		log.debug("subscribing AdditionalInformationFacet for service "+serviceId+ "facet "+facetSchema + "xpath "+xpath);
		secseLogger.event("subscribing AdditionalInformationFacet for service "+serviceId+ "facet "+facetSchema + "xpath "+xpath);
		if(pLogger.isEnabled()) pLogger.log(System.currentTimeMillis(), "DM_SUB_ADD_INFO", description);
		
		if(serviceId == null && facetSchema == null && xpath == null) throw new NotSubscribedException("At least one parameter must be not-null");
		
		FacetInterest facetInterest = new FacetInterest(facetSchema, xpath);
		
		InterestFacetEnt facetInterestEnt = new InterestFacetEnt(FacetInterestType.additionalInformationFacet, serviceId, null, new FacetInterest[]{facetInterest}, description);
		this.em.persist(facetInterestEnt);
		
		interestManager.subscribeAdditionalInformationFacet(serviceId, facetInterest, description);
		
		return facetInterestEnt.getId();
	}

	@WebMethod public long subscribeAdditionalInformationFacetById(@WebParam(name="serviceId") String serviceId,
			@WebParam(name="facetSchemaId")String facetSchemaId, @WebParam(name="description")String description) throws NotSubscribedException {
		log.debug("DeliveryManager.AdditionalInformationFacetById for service "+serviceId+ "facet "+facetSchemaId);
		secseLogger.event("DeliveryManager.AdditionalInformationFacetById for service "+serviceId+ "facet "+facetSchemaId);
		if(pLogger.isEnabled()) pLogger.log(System.currentTimeMillis(), "DM_SUB_ADD_INFO_ID", facetSchemaId);
		
		if(facetSchemaId == null) throw new NotSubscribedException("The facetSchemaId cannot be null");
		
		InterestFacetEnt interestFacetEnt = new InterestFacetEnt(FacetInterestType.additionalInformationFacetById, serviceId, facetSchemaId, null, description);
		em.persist(interestFacetEnt);
		interestManager.subscribeAdditionalInformationFacetById(serviceId, facetSchemaId, description);
		return interestFacetEnt.getId();
	}

	@WebMethod public long subscribeService(@WebParam(name="serviceId")String serviceId, @WebParam(name="description")String description) {
		log.debug("DeliveryManager.subscribeService() "+serviceId);
		secseLogger.event("DeliveryManager.subscribeService() "+serviceId);
		if(pLogger.isEnabled()) pLogger.log(System.currentTimeMillis(), "DM_SUB_SERVICE", serviceId);

		
		InterestServiceEnt interestServiceEnt = new InterestServiceEnt(serviceId, description);
		em.persist(interestServiceEnt);
		interestManager.subscribeService(serviceId, description);
		return interestServiceEnt.getId();
	}

	@WebMethod public long subscribeSpecificationFacet(@WebParam(name="facetInterest")FacetInterest[] interest,@WebParam(name="description")String description) throws NotSubscribedException {
		if(interest == null || interest.length == 0) throw new NotSubscribedException("You must specify at least one interest!");
		
		secseLogger.event("Subscribing to " + interest.length + " specification facets ");
		log.debug("Subscribing to " + interest.length + " specification facets ");
		if(pLogger.isEnabled()) pLogger.log(System.currentTimeMillis(), "DM_SUB_SPECFACET", description);
		
		InterestFacetEnt interestFacetEnt = new InterestFacetEnt(FacetInterestType.specificationFacet, null, null, interest, description);
		em.persist(interestFacetEnt);
		interestManager.subscribeSpecificationFacet(interest, description);
		return interestFacetEnt.getId();
	}

	@WebMethod public void unsubscribe(@WebParam(name="id") long id) throws NotSubscribedException {
		secseLogger.event("Unsubscribing to element "+id);
		log.debug("Unsubscribing to element "+id);
		InterestEnt interestEnt = em.find(InterestEnt.class, id);

		if(interestEnt == null ) throw new NotSubscribedException("Not subscribed to interest " + id);

		if (interestEnt instanceof InterestServiceEnt) {
			InterestServiceEnt intServEnt = (InterestServiceEnt)interestEnt; 
			interestManager.unsubscribe(new InterestService(intServEnt.getServiceID()));
			
		} else {
			if (interestEnt instanceof InterestFacetEnt) {
			InterestFacetEnt intFacetEnt = (InterestFacetEnt) interestEnt;
			if(intFacetEnt.getType().compareTo(FacetInterestType.specificationFacet)==0)
				try{
				interestManager.unsubscribe(new MultipleInterestSpecificationFacet(intFacetEnt.getFacetInterest()));
				} catch(Exception ex){
					ex.printStackTrace();
				}
				
			else{
				if(intFacetEnt.getType().compareTo(FacetInterestType.additionalInformationFacetById)==0)
					interestManager.unsubscribe(new InterestAdditionalInformationId(intFacetEnt.getServiceId(),intFacetEnt.getFacetSchemaId()));
				else {
					if(intFacetEnt.getType().compareTo(FacetInterestType.additionalInformationFacet)==0)
					try	{
						interestManager.unsubscribe(new InterestAdditionalInformation(intFacetEnt.getServiceId(), intFacetEnt.getFacetInterest()[0].getFacetSchema(), intFacetEnt.getFacetInterest()[0].getXpath()));
					}catch(Exception ex){
						ex.printStackTrace();
					}
					else throw new NotSubscribedException("Invalid facet interest type");
				}
			}
		}
	}
		em.remove(interestEnt);		
		
	}

	@WebMethod public long[] getAllSharedIds() {
		log.debug("Getting all shared ids");
		Collection<CreatedElementExtraInfo> elements = CreatedElementExtraInfo.getALL(em);
		long[] sharedIds = new long[elements.size()];
		int k = 0;
		for(CreatedElementExtraInfo c: elements){
			sharedIds[k++] = c.getId();
		}
		return sharedIds;
	}

	@WebMethod public String getVersion() {
		return "2.0.0";
	}

	
	@WebMethod public boolean isInSharing(@WebParam(name="serviceId") String serviceId, @WebParam(name="facetId") String facetId) {
		ElementEnt elem = null;
		if(facetId == null) 
			elem = modelManager.lookup(serviceId);
		else 
			elem = modelManager.lookup(serviceId, facetId);
		
		if(elem == null) return false;
		if(elem.getExtraInfo().containsKey(CreatedElementExtraInfo.INFO_TYPE)) return true;
		if(elem.getExtraInfo().containsKey(ReceivedElementExtraInfo.INFO_TYPE)) return true;
		return false;
	}

}
