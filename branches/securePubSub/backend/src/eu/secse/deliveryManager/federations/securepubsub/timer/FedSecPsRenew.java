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

package eu.secse.deliveryManager.federations.securepubsub.timer;

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
import eu.secse.deliveryManager.data.FacetEnt;
import eu.secse.deliveryManager.data.FederatedPromotion;
import eu.secse.deliveryManager.data.ServiceEnt;
import eu.secse.deliveryManager.federations.securepubsub.core.ISecPubSubProxy;
import eu.secse.deliveryManager.federations.securepubsub.data.SecPubSubPromotionExtraInfo;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.FacetAddInfo;

@Stateless
public class FedSecPsRenew implements IFedSecPsRenew {
	private static final Log log = LogFactory.getLog(IFedSecPsRenew.class);

	@Resource
	TimerService timerService;

	@PersistenceContext(unitName="deliveryManager")
	protected EntityManager em;

	@EJB private ModelManager modelManager;

	@EJB private ISecPubSubProxy proxy;

	public Timer createTimer(long init, long step) {
		return this.timerService.createTimer(init, step, "FedPs-Renew");
	}

	@Timeout
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void timeoutHandler(Timer timer) {
		log.info("received timer event: " + timer.getInfo());

		this.renewFederatedElements();
	}

	private void renewFederatedElements() {
		log.debug("Renew.renewFederatedElements()");

		Collection<FederatedPromotion> fedPromotions = SecPubSubPromotionExtraInfo.getToRenew(em);
		for (FederatedPromotion fedProm : fedPromotions) {
			if (fedProm.getElement() instanceof ServiceEnt) {
				ServiceEnt service = (ServiceEnt) fedProm.getElement();
				DService dService = modelManager.getServiceData(service.getElemPK().getId());
				proxy.send(fedProm, dService);
			} else if (fedProm.getElement() instanceof FacetEnt) {
				FacetEnt facet = (FacetEnt) fedProm.getElement();
				FacetAddInfo dfacet = modelManager.getFacetAdditionalData(facet.getService().getElemPK().getId(), facet.getElemPK().getId());
				proxy.send(fedProm, dfacet);
			} else 
				log.debug("WARNING: renewing an unknown element: " + fedProm.getClass().getCanonicalName());
		}
	}

}

