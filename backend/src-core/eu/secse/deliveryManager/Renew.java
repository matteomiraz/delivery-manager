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

import javax.annotation.EJB;
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
import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.model.DFacetSpecificationSchema;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.reds.IRedsProxy;
import eu.secse.deliveryManager.registry.IRegistryProxy;

@Stateless
public class Renew implements IRenew {
	private static final Log log = LogFactory.getLog(IRenew.class);
	
	@Resource
	TimerService timerService;
	
	@PersistenceContext(unitName="deliveryManager")
	protected EntityManager em;

	@EJB private IFederationModelManager iFed;
	
	@EJB private IRegistryProxy iReg;
	@EJB private IRedsProxy iReds;
	@EJB private ILeaseManager iLease;
	
	public Timer createTimer(long init, long step) {
		return this.timerService.createTimer(init, step, "IRenew");
	}
	
	@Timeout
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void timeoutHandler(Timer timer) {
		log.info("received timer event: " + timer.getInfo());

		this.renewServices();
		this.renewFacetTypes();
		
		this.renewFederatedElements();
	}

	private void renewFederatedElements() {
		log.debug("Renew.renewFederatedElements()");
		
		Collection<FederatedElement> felems = FederatedElement.getRenew(this.em);
		for (FederatedElement el : felems) {
			try {
				if (el instanceof FederatedService) this.iFed.send((FederatedService) el);
				else if (el instanceof FederatedFacet) this.iFed.send((FederatedFacet) el);
				else log.debug("WARNING: renewing an unknown element: " + el.getClass().getCanonicalName());
			} catch (NotFoundException e) {
				log.warn("Cannot renew element due to " + e.getMessage());
			}
		}
	}

	private void renewServices() {
		log.debug("renewServices()");
		
		Collection<ServiceEnt> srvs = ServiceEnt.getRenew(this.em);
		for (ServiceEnt srv : srvs) {
			if(!srv.isOwnership()) {
				log.debug("WARNING: the service " + srv.getServiceID() + " is not owned by the local registry!");
				continue;
			} else
				log.debug("Renewing the service " + srv.getServiceID());
				
			try {
				srv.setLease(this.iLease.getLease());
				srv.setRenew(this.iLease.getStandardRenew());
				
				// renew the service and all specification facets
				DService dsrv = this.iReg.getService(srv.getServiceID());
				dsrv.setLease(srv.getLease());
				this.iReds.publish(dsrv);
				
				// if the user wants to share all additional information facets, send all of them
				if(srv.isShareFacets())
					for (DFacetSpecificationSchema dft : iReg.getAdditionalInformationFacets(srv.getServiceID())) {
						dft.setLease(srv.getLease());
						this.iReds.publish(dft);
					}

			} catch (Exception e) {
				log.warn("error while renewing the service " + srv.getServiceID() + ": " + e.getMessage() + " due to " + e.getCause());
				e.printStackTrace();
			}
		}
		
	}

	private void renewFacetTypes() {
		log.debug("renewFacetTypes()");
		
		Collection<FacetEnt> fts = FacetEnt.getRenew(this.em);
		for (FacetEnt ft : fts) {
			if(ft.getService().isShareFacets()) {
				log.debug("WARNING: the facet type " + ft.getFacetSchemaID() + "cannot be renewed: the service already share all its additional information facets");
				continue;
			} else {
				log.debug("Renewing the facet type " + ft.getFacetSchemaID());

				ft.setLease(this.iLease.getLease());
				ft.setRenew(this.iLease.getStandardRenew());

				try {
					DFacetSpecificationSchema dfacet = this.iReg.getAdditionalInformationFacet(ft.getFacetSchemaID());
					dfacet.setLease(ft.getLease());
					this.iReds.publish(dfacet);
				} catch (Exception e) {
					log.warn("Cannot renew the facet type " + ft.getFacetSchemaID() + ": " + e.getMessage() + " due to " + e.getCause());
				}
			}
		}
	}
}
