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

package eu.secse.deliveryManager.sharing.timer;


import java.util.Collection;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.secse.deliveryManager.core.ModelManager;
import eu.secse.deliveryManager.data.ElementEnt;
import eu.secse.deliveryManager.data.FacetEnt;
import eu.secse.deliveryManager.data.ServiceEnt;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.FacetAddInfo;
import eu.secse.deliveryManager.sharing.core.IShareManager;
import eu.secse.deliveryManager.sharing.data.CreatedElementExtraInfo;
import eu.secse.deliveryManager.sharing.data.ReceivedElementExtraInfo;

@Stateless
public class SharingTimers implements ISharingTimers {
	private static final Log log = LogFactory.getLog(ISharingTimers.class);
	
	@Resource
	TimerService timerService;
	
	@PersistenceContext(unitName="deliveryManager")
	protected EntityManager em;
	
	@EJB protected ModelManager modelManager;

	@Timeout
	public void timeoutHandler(Timer timer) {
		log.info("received timer event: " + timer.getInfo());

		if(timer.getInfo().equals("expire")) this.deleteElements();
		else renewElements();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void deleteElements() {
		log.debug(" Deleting expired elements");
		Collection<ElementEnt> expiredElements = ReceivedElementExtraInfo.getToExpire(em);
		for (ElementEnt elementEnt : expiredElements) {
			log.info("The " + (elementEnt instanceof ServiceEnt ?"service":"facet specification") + " with id " + elementEnt.getElemPK().getId() + " is expired ");
			ReceivedElementExtraInfo extraInfo = (ReceivedElementExtraInfo)(elementEnt.getExtraInfo().get(ReceivedElementExtraInfo.INFO_TYPE));
			elementEnt.getExtraInfo().remove(ReceivedElementExtraInfo.INFO_TYPE);
			em.remove(extraInfo);
			em.flush();
			if(elementEnt instanceof ServiceEnt)
				modelManager.delete((ServiceEnt)elementEnt);
			else modelManager.delete((FacetEnt)elementEnt);
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void renewElements() {
		log.debug("Renew shared elements");

		IShareManager shareManager;
		try{
			InitialContext ctx = new InitialContext(); 
			shareManager = (IShareManager)ctx.lookup("deliverymanager/ShareManager/local");
		} catch(NamingException ex){
			log.error("SharingExpire not found: " + ex.getMessage() + " due to:" + ex.getCause());
			return;
		}

		
		Collection<ElementEnt> elementsToRenew = CreatedElementExtraInfo.getToRenew(em);
		for (ElementEnt elementEnt : elementsToRenew) {
			log.debug("considering " + elementEnt.getElemPK().getType() + " " + elementEnt.getElemPK().getId());
			if (elementEnt instanceof ServiceEnt) {
				ServiceEnt serviceEnt = (ServiceEnt)elementEnt;
				DService dsrv = modelManager.getServiceData(serviceEnt.getElemPK().getId());
				if(dsrv != null) // thank you, alberto! 
					shareManager.send(dsrv, serviceEnt);
			} else {
				if(elementEnt instanceof FacetEnt){
					FacetEnt facetEnt = (FacetEnt)elementEnt;
					FacetAddInfo facetAddInfo = modelManager.getFacetAdditionalData(facetEnt.getService().getElemPK().getId(), facetEnt.getElemPK().getId());
					if(facetAddInfo != null) // thank you, alberto! 
						shareManager.send(facetEnt, facetAddInfo);
				} else log.debug("WARNING: renewing an unknown element: " + elementEnt.getClass().getCanonicalName());
			}
		}
	}

	public Timer createRenewTimer(long init, long step) {
		return this.timerService.createTimer(init, step, "renew");
	}

	public Timer createExpireTimer(long init, long step) {
		return this.timerService.createTimer(init, step, "expire");
	}
}
