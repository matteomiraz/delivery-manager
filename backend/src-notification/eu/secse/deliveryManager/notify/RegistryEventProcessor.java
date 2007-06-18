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


package eu.secse.deliveryManager.notify;

import java.util.Collection;
import java.util.regex.Pattern;

import javax.annotation.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;

import eu.secse.deliveryManager.notify.data.NotificatedFacetSchema;
import eu.secse.deliveryManager.notify.data.NotificatedFacetXml;
import eu.secse.deliveryManager.notify.data.NotificatedItem;
import eu.secse.deliveryManager.notify.data.NotificatedService;
import eu.secse.deliveryManager.notify.data.NotificationInterest;
import eu.secse.deliveryManager.registry.IRegistryProxy;

@Stateless
public class RegistryEventProcessor implements IRegistryEventProcessor {
	Logger log = Logger.getLogger(IRegistryEventProcessor.class);
	
	@PersistenceContext(unitName="notification") EntityManager em;

	@EJB private IRegistryProxy registry;
	@EJB private Mailer mailer;
	
	public void serviceAdded(String serviceId) {
		log.info("serviceAdded");
		
		for (NotificationInterest i : NotificationInterest.getAll(em)) {
			log.debug("processing interest " + i.getId() + " of " + i.getUser().getEmail());
			if(i.isNotifyService() && i.getProviderName() == null && check(serviceId, i)) {
				mailer.send(i.getUser().getEmail(), "Service added", "The service with id: " + serviceId + " has been added in the registry");
				em.persist(new NotificatedService(i, serviceId));
			}
		}
	}

	private boolean check(String serviceId, NotificationInterest i) {
		try {
			log.debug("check: " + i.getId());
			
			if(i.getServiceId() != null) {
				if(!i.getServiceId().equals(serviceId)) {
					log.debug("check: Service ID not matches: expected: \"" + i.getServiceId() + "\", found: \"" + serviceId + "\"returning false");
					return false;
				}
			}
			
			if(i.getServiceNameRegex() != null) {
				String serviceName = registry.getServiceName(serviceId);
				if (!Pattern.matches(i.getServiceNameRegex(), serviceName)) {
					log.debug("check: Service Name not matches: returning false");
					return false;
				}
			}

			if(i.getBaseServiceId() != null) {
				boolean ok = registry.isANextVersionOfSpecifiedService(serviceId, i.getBaseServiceId());
				if(!ok) {
					log.debug("check: This service (id: " + serviceId + ") is not a next version of the service " + i.getBaseServiceId() + ": returning false");
					return false;
				}
			}

			if(i.getProviderName() != null) {
				String provider = registry.getServiceProvider(serviceId);
				if(!i.getProviderName().equals(provider)) {
					log.debug("check: Service Provider not matches(found: " + provider + " expected: " + i.getProviderName() + "): returning false");
					return false;
				}
			}
			
			log.debug("check: returning true");
			return true;
		} catch (Throwable e) {
			log.debug("check error: " +e.getMessage());
			return false;
		}
	}
	
	public void serviceRemoved(String serviceId) {
		Collection<NotificatedService> l = NotificatedService.getByIDs(em, serviceId);
		
		for (NotificatedService s : l) {
			mailer.send(s.getInterest().getUser().getEmail(), "Service removed from registry", "The service with id " + serviceId + " has been removed from the registry");
			em.remove(s);
		}
	}
	
	public void FacetSchemaAdded(String serviceId, String schemaId) {
		for (NotificationInterest i : NotificationInterest.getAll(em)) {
			if(i.isNotifyFacetSpecificationSchema() && check(serviceId, i)) {
				mailer.send(i.getUser().getEmail(), "Facet specification schema added", "The facet specification schema with id: " + schemaId + " (service: " + serviceId + ") has been added in the registry");
				em.persist(new NotificatedFacetSchema(i, serviceId, schemaId));
			}
		}
	}
	
	public void FacetSchemaRemoved(String serviceId, String schemaId) {
		Collection<NotificatedFacetSchema> l = NotificatedFacetSchema.getByIDs(em, serviceId, schemaId);
		
		for (NotificatedFacetSchema f : l) {
			mailer.send(f.getInterest().getUser().getEmail(), "Facet specification schema removed from registry", "The facet specification schema with id " + schemaId + "(service " + serviceId + ") has been removed from the registry");
			em.remove(f);
		}
	}

	public void facetXmlAdded(String serviceId, String schemaId, String xmlId, boolean additionalInformation) {
		// checking services that need a provider name
		for (NotificationInterest i : NotificationInterest.getServiceProvider(em, serviceId)) {
			if(!checkAlreadySent(serviceId, i) && check(serviceId, i)) {
				mailer.send(i.getUser().getEmail(), "Service added", "The service with id: " + serviceId + " has been added in the registry");
				em.persist(new NotificatedService(i, serviceId));
			}
		}
		
		for (NotificationInterest i : NotificationInterest.getAll(em)) {
			if(i.isNotifyFacetSpecificationXml() && check(serviceId, i)) {
				mailer.send(i.getUser().getEmail(), "Facet specification XML added", "The facet specification xml with id: " + xmlId + "(service: " + serviceId + ", schema:" + schemaId + ") has been added in the registry");
				em.persist(new NotificatedFacetXml(i, serviceId, schemaId, xmlId));
			}
		}
	}
	
	private boolean checkAlreadySent(String serviceId, NotificationInterest i) {
		log.debug("Checking interest id: " + i.getId());
		for (NotificatedItem ni : i.getNotificatedItems()) {
			if (ni instanceof NotificatedService) {
				if(((NotificatedService) ni).getServiceId().equals(serviceId)) {
					log.debug("Check: returning true");
					return true;
				}
			}
		}
		log.debug("Check: returning false");
		return false;
	}

	public void facetXmlRemoved(String serviceId, String schemaId, String xmlId, boolean additionalInformation) {
		Collection<NotificatedFacetXml> l = NotificatedFacetXml.getByIDs(em, serviceId, schemaId, xmlId);
		
		for (NotificatedFacetXml f : l) {
			mailer.send(f.getInterest().getUser().getEmail(), "Facet specification XML removed from registry", "The facet specification XML with id " + xmlId + "(schema: " + schemaId + "; service " + serviceId + ") has been removed from the registry");
			em.remove(f);
		}
	}
}
