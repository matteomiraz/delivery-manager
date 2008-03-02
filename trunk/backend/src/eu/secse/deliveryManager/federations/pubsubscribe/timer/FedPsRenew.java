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

package eu.secse.deliveryManager.federations.pubsubscribe.timer;

import java.util.Collection;

import javax.annotation.Resource;
import javax.ejb.EJB;
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

import eu.secse.deliveryManager.core.ModelManager;
import eu.secse.deliveryManager.data.FederatedPromotion;
import eu.secse.deliveryManager.data.ServiceEnt;
import eu.secse.deliveryManager.federations.pubsubscribe.core.IPubSubProxy;
import eu.secse.deliveryManager.federations.pubsubscribe.data.PubSubPromotionExtraInfo;
import eu.secse.deliveryManager.model.DService;

@Stateless
public class FedPsRenew implements IFedPsRenew {
	private static final Log log = LogFactory.getLog(IFedPsRenew.class);
	
	@Resource
	TimerService timerService;
	
	@PersistenceContext(unitName="deliveryManager")
	protected EntityManager em;

	@EJB private ModelManager modelManager;
	
	@EJB private IPubSubProxy proxy;
	
	public Timer createTimer(long init, long step) {
		return this.timerService.createTimer(init, step, "FedPs-Renew");
	}
	
	@Timeout
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void timeoutHandler(Timer timer) {
		log.info("received timer event: " + timer.getInfo());

		//TODO: implementare this.renewServices();
		//TODO: implementare this.renewFacetTypes();
		
		this.renewFederatedElements();
	}

	private void renewFederatedElements() {
		log.debug("Renew.renewFederatedElements()");
		
		Collection<FederatedPromotion> fedPromotions = PubSubPromotionExtraInfo.getToRenew(em);
		for (FederatedPromotion fedProm : fedPromotions) {
		if (fedProm.getElement() instanceof ServiceEnt) {
					ServiceEnt service = (ServiceEnt) fedProm.getElement();
					DService dService = modelManager.getServiceData(service.getElemPK().getId());
					proxy.send(fedProm, dService);}
					
				/*else if (fedProm.getElement() instanceof ServiceEnt) {
					
					ElementEnt  fedele =  manager.add(serviceId);
					if (fedele==null) {
						throw new NotFoundException("The service with id " + serviceId + " can not be shared (modelmanager error)");
					}
					this.iFed.send((FederatedService) el);
				}
				else if (el instanceof FederatedFacet) this.iFed.send((FederatedFacet) el);
				else log.debug("WARNING: renewing an unknown element: " + el.getClass().getCanonicalName()); */
			/* else {
				log.warn("Cannot renew element due to " + e.getMessage());
			}*/
		}
	}

	/*private void renewServices() {
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
*/
	/*private void renewFacetTypes() {
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
	}*/
}

