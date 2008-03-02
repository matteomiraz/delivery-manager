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
import eu.secse.deliveryManager.data.ElementEnt;
import eu.secse.deliveryManager.data.FederatedElement;
import eu.secse.deliveryManager.data.FederatedService;
import eu.secse.deliveryManager.federations.pubsubscribe.data.PubSubFedElemExtraInfo;

@Stateless
public class FedPsExpire implements IFedPsExpire {
	private static final Log log = LogFactory.getLog(IFedPsExpire.class);
	
	@Resource
	TimerService	 timerService;
	
	@PersistenceContext(unitName="deliveryManager")
	protected EntityManager em;
	
	@EJB protected ModelManager modelManager;

	@Timeout
	public void timeoutHandler(Timer timer) {
		log.info("received timer event: " + timer.getInfo());

		this.deleteFederatedElements();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void deleteFederatedElements() {
		log.debug("Expire.deleteFederatedElements()");
		Collection<FederatedElement> felems = PubSubFedElemExtraInfo.getExpired(this.em);
		for (FederatedElement el : felems) {
			log.info("The " + (el instanceof FederatedService?"service":"facet specification") + " with id " + el.getId() + " is no longer in the federation " + el.getFederation().getName());
			ElementEnt element = el.getElement();
			this.em.remove(el);
			this.em.flush();

			modelManager.deleteFromPlugin(element);
			this.em.flush();
		}
	}

	public Timer createTimer(long init, long step) {
		return this.timerService.createTimer(init, step, "FedPs-Expire");
	}
}
