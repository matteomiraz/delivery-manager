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

package eu.secse.deliveryManager.notify.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.secse.deliveryManager.core.ModelManager;
import eu.secse.deliveryManager.data.FederatedElement;
import eu.secse.deliveryManager.data.FederatedPromotion;
import eu.secse.deliveryManager.data.ServiceEnt;
import eu.secse.deliveryManager.exceptions.InvalidDeliveryMethodException;
import eu.secse.deliveryManager.model.FacetSpec;
import eu.secse.deliveryManager.model.XMLCommons;
import eu.secse.deliveryManager.notify.data.Event;
import eu.secse.deliveryManager.notify.data.NotificatedService;
import eu.secse.deliveryManager.notify.data.NotificationInterest;
import eu.secse.deliveryManager.registry.IRegistryProxy;
import eu.secse.deliveryManager.utils.IConfiguration;

@Stateless
public class RegistryEventProcessor implements IRegistryEventProcessor {

	
	private static final Log log = LogFactory.getLog(IRegistryEventProcessor.class);

	@EJB
	private IConfiguration config;


	/** XPath Header: <b>XPATH_HEADER</b> + xpathExpression + XPATH_FOOTER */
	private static final String XPATH_HEADER = "boolean(";
	/** XPath Footer: XPATH_HEADER + xpathExpression + <b>XPATH_FOOTER</b> */
	private static final String XPATH_FOOTER = ")";

	@PersistenceContext(unitName="deliveryManager") EntityManager em;

	@EJB private ModelManager modelManager;

	@EJB private IRegistryProxy registry;

	private HashMap<String,String> deliveryMethodNames = new HashMap<String, String>();



	public void facetSchemaAdded(String serviceId, String schemaId) {
		for (NotificationInterest i : NotificationInterest.getAll(em)) {
			if(!checkAlreadySent(serviceId, i) && check(serviceId, i, null, false)) {
				send(new Event(i.getUser().getEmail(),"Service added","The service " +serviceId+" that matches your requirements has been added in the registry", true, serviceId), i.getDeliveryMethod());
				em.persist(new NotificatedService(i, serviceId));
			}

		}
	}

	public void facetSchemaRemoved(String serviceId, String schemaId) {

		Collection<NotificatedService> notificatedServices = NotificatedService.getByIDs(em, serviceId);

		for(NotificatedService s : notificatedServices ){
			if(check(serviceId, s.getInterest(), schemaId, true)){
				send(new Event(s.getInterest().getUser().getEmail(),"Facet specification schema removed from registry", "The service " + serviceId + " doesn't match your requirements", false, serviceId),s.getInterest().getDeliveryMethod());
				em.remove(s);
			}
		}

	}

	public void facetXmlAdded(String serviceId, String schemaId, String xmlId,
			boolean additionalInformation) {
		// checking services that need a provider name
		for (NotificationInterest i : NotificationInterest.getAll(em)) {
			send(new Event(i.getUser().getEmail(),"Service added", "The service with id: " + serviceId + " that matches your requirements has been added in the registry", true, serviceId),i.getDeliveryMethod());
			em.persist(new NotificatedService(i, serviceId));
		}
	}


	public void facetXmlRemoved(String serviceId, String schemaId,
			String xmlId, boolean additionalInformation) {
		Collection<NotificatedService> notificatedServices = NotificatedService.getByIDs(em, serviceId);

		for(NotificatedService s : notificatedServices ){
			if(check(serviceId, s.getInterest(), schemaId, true)){
				send(new Event(s.getInterest().getUser().getEmail(),"Facet specification XML removed from registry","The service " + serviceId + " doesn't match your requirements", false, serviceId),s.getInterest().getDeliveryMethod());
				em.remove(s);
			}
		}

	}

	public void serviceAdded(String serviceId) {
		log.info("serviceAdded");
		for (NotificationInterest i : NotificationInterest.getAll(em)) {
			log.info("processing interest " + i.getId() + " of " + i.getUser().getEmail());
			if(/*i.isNotifyService()  &&*/ check(serviceId, i, null, false)) {
				log.info("check: true");
				send(new Event(i.getUser().getEmail(),"Service added", "The service with id: " + serviceId + " that matches your requirements has been added in the registry", true, serviceId),i.getDeliveryMethod());
				em.persist(new NotificatedService(i, serviceId));
			} else log.info("check: false");
		}

	}

	public void serviceRemoved(String serviceId) {
		Collection<NotificatedService> l = NotificatedService.getByIDs(em, serviceId);

		for (NotificatedService s : l) {
			send(new Event(s.getInterest().getUser().getEmail(),"Service removed from registry", "The service with id " + serviceId + " has been removed from the registry", false, serviceId),s.getInterest().getDeliveryMethod());
			em.remove(s);
		}

	}

	private boolean check(String serviceId, String xpath, FacetSpec fs){
		if(fs!= null && xpath!=null){

			try {
				return ((Boolean) XMLCommons.newXPath().evaluate(XPATH_HEADER + xpath + XPATH_FOOTER, fs.getDocumentDOM(), XPathConstants.BOOLEAN));
			} catch (XPathExpressionException e) {
				log.debug(e.getMessage());
			}
		}


		return false;

	}

	private boolean check(String serviceId, NotificationInterest i, String schemaId, boolean isDelete) {
		try {
			log.debug("check: " + i.getId());

			if(i.getServiceId() != null && !i.getServiceId().equals("") ) {
				if(!i.getServiceId().equals(serviceId)) {
					log.debug("check: Service ID not matches: expected: \"" + i.getServiceId() + "\", found: \"" + serviceId + "\"returning false");
					return false;
				}
			}

			if(i.getServiceNameRegex() != null && !i.getServiceNameRegex().equals("")) {
				String serviceName = registry.getServiceName(serviceId);
				if (!Pattern.matches(i.getServiceNameRegex(), serviceName)) {
					log.debug("check: Service Name not matches: returning false");
					return false;
				}
			}

			if(i.getBaseServiceId() != null && !i.getBaseServiceId().equals("")) {
				boolean ok = registry.isANextVersionOfSpecifiedService(serviceId, i.getBaseServiceId());
				if(!ok) {
					log.debug("check: This service (id: " + serviceId + ") is not a next version of the service " + i.getBaseServiceId() + ": returning false");
					return false;
				}
			}

			if(i.getFacetXpathConstraints()!=null && !i.getFacetXpathConstraints().equals("") ){
				boolean ok = false;
				if(schemaId!=null && !isDelete){
					FacetSpec facetSpec = registry.getFacet(schemaId);
					if(check(serviceId,i.getFacetXpathConstraints(),facetSpec))
						ok = true;
				}
				if(ok == false) return false;
			}

			if(i.getFederationName() != null && !i.getFederationName().equals("") ){
				boolean ok = false;
				ServiceEnt serviceEnt = modelManager.lookup(serviceId);
				Collection<FederatedElement> federatedElements = serviceEnt.getFederatedElement();
				for(FederatedElement fs: federatedElements){
					log.info(i.getFederationName()+" vs "+fs.getFederation().getId());
					if(i.getFederationName().equals(fs.getFederation().getId()))
					{

						return true;
					}}
				for(FederatedPromotion fp: serviceEnt.getFederatedPromotion()){
						if(i.getFederationName().equals(fp.getFederation().getId())){

							return true;
						}
					}
					if(ok==false)
						return false;
				}


			log.debug("check: returning true");
			return true;
		} catch (Throwable e) {
			log.debug("check error: " +e.getMessage());
			return false;
		}
	}

	private boolean checkAlreadySent(String serviceId, NotificationInterest i) {
		log.debug("Checking interest id: " + i.getId());
		Collection<NotificatedService> notificatedServices = NotificatedService.getByIDs(em, serviceId);
		if(notificatedServices!=null && notificatedServices.size()>0)
			return true;
		else return false;
	}

	private String getName(String method) throws InvalidDeliveryMethodException{
		String s = deliveryMethodNames.get(method);
		if(s!=null && !s.equals("")) return s;
		s=config.getString("NotificationMethods.value."+method);
		if(s.startsWith("!"))
			throw new InvalidDeliveryMethodException("Unknown Delivery method "+ method);
		else return s;
	}

	private void send(Event e, String deliveryMethod){
		log.info("Sending message with delivery method " +deliveryMethod);
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			String lookupName = getName(deliveryMethod);
			ISender sender = (ISender)ctx.lookup(lookupName);
			sender.send(e);
		} catch (NamingException ex) {
			log.equals(ex.getMessage());
		} catch (InvalidDeliveryMethodException ex) {
			log.error(ex.getMessage());
		}

	}

}
