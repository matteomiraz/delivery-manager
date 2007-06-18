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


package eu.secse.deliveryManager;

import java.util.Collection;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.secse.deliveryManager.db.data.FacetEnt;
import eu.secse.deliveryManager.db.data.ServiceEnt;
import eu.secse.deliveryManager.db.federations.FederatedElement;
import eu.secse.deliveryManager.db.federations.FederatedFacet;
import eu.secse.deliveryManager.db.federations.FederatedService;

@Stateless
public class Expire implements IExpire {
	private static final Log log = LogFactory.getLog(IExpire.class);
	
	@Resource
	TimerService	 timerService;
	
	@PersistenceContext(unitName="deliveryManager")
	protected EntityManager em;

	@Timeout
	public void timeoutHandler(Timer timer) {
		log.info("received timer event: " + timer.getInfo());

		this.deleteServices();
		this.deleteFacets();
		this.deleteFederatedElements();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void deleteServices() {
		log.debug("Expire.deleteServices()");
		
		for (ServiceEnt s : ServiceEnt.getExpired(this.em)) {
			String serviceId = s.getServiceID();
			log.info("Removing service " + serviceId + ": expired on " + s.getLease());
			this.em.remove(s);
			
			try {
				Collection<FederatedService> fss = FederatedService.getByService(em, serviceId);
				for (FederatedService fs : fss) {
					log.info("Removing the promotion of the service id " + serviceId + " in the federation " + fs.getFederation().getFederationName()); 
					this.em.remove(fs);
				}
			} catch (Exception e) {
				// il servizio è in nessuna federazione => non devo cancellarlo!
			}
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void deleteFacets() {
		log.debug("Expire.deleteFacetTypes()");
		
		for (FacetEnt ft : FacetEnt.getExpired(this.em)) {
			String facetSchemaId = ft.getFacetSchemaID();
			log.info("Removing facet type " + facetSchemaId + ": expired on " + ft.getLease());
			this.em.remove(ft);
			
			try {
				Collection<FederatedFacet> fss = FederatedFacet.getByFacet(em, facetSchemaId);
				for (FederatedFacet fs : fss) {
					log.info("Removing the promotion of the facet specification schema id " + facetSchemaId + " in the federation " + fs.getFederation().getFederationName()); 
					this.em.remove(fs);
				}
			} catch (Exception e) {
				// il servizio è in nessuna federazione => non devo cancellarlo!
			}
		}
	}
	
	// nota: ogni elemento ricevuto attraverso una federazione, aggiorna anche il suo lease => 
	// in questo metodo devo solo cancellare gli elementi nelle tabelle delle federazioni!
	private void deleteFederatedElements() {
		log.debug("Expire.deleteFederatedElements()");
		
		Collection<FederatedElement> felems = FederatedElement.getExpired(this.em);
		for (FederatedElement el : felems) {
			log.info("The " + (el instanceof FederatedService?"service":"facet specification") + " with id " + el.getElementId() + " is no longer in the federation " + el.getFederation().getFederationName());
			this.em.remove(el);
		}
	}

	public Timer createTimer(long init, long step) {
		return this.timerService.createTimer(init, step, "IExpire");
	}
}
