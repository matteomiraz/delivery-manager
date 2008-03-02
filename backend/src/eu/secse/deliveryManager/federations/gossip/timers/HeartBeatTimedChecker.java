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

package eu.secse.deliveryManager.federations.gossip.timers;

import java.util.Calendar;
import java.util.Collection;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import eu.secse.deliveryManager.data.FederationEnt;
import eu.secse.deliveryManager.federations.gossip.core.IGossipProxy;
import eu.secse.deliveryManager.federations.gossip.data.DeliveryManagerGossipInfo;
import eu.secse.deliveryManager.federations.gossip.data.FederationSubscriptionStatus;
import eu.secse.deliveryManager.federations.gossip.data.GossipFederationInfo;

@Stateless
public class HeartBeatTimedChecker  implements
		IHeartBeatTimedChecker,TimedObject {

	@PersistenceContext(unitName="deliveryManager")
	private EntityManager manager;
	
	@Resource
	private TimerService timerservice;

	@EJB
	private IGossipProxy proxy;
	
	public void ejbTimeout(Timer arg0) {
		FederationEnt f=manager.find(FederationEnt.class, (String)arg0.getInfo());
		GossipFederationInfo gf=(GossipFederationInfo)f.getExtraInfo();
		Collection<DeliveryManagerGossipInfo> live=gf.getDminfo();
		boolean empty=true;
		for (DeliveryManagerGossipInfo g:live) {
			if (g.isLiveinview()) {
				g.setInview(true);
				g.setLiveinview(false);
				empty=false;
			} else {
				if (!g.isPartialview()) {
					manager.remove(g);
				}
			}
			
		}		
		if (empty) {			
			//Uhm... we are alone... resubscribe!
			gf.setStatus(FederationSubscriptionStatus.ISOLATED.getValue());
			proxy.isolated(f.getId());
		}

		
	}

	/** period: 1 day and 1 hour, instead of 1 day, to take into account possible desynchronizations
	 * 
	 */
	public void startTimer(String federationId) {
		Calendar c=Calendar.getInstance();
		c.add(Calendar.DATE,1);
		c.add(Calendar.HOUR,1);
		timerservice.createTimer(c.getTime(),federationId);		
	}

	@SuppressWarnings("unchecked")
	public void stopTimer(String federationId) {
		Collection<Timer> timers=timerservice.getTimers();
		for (Timer r:timers) {
			if (federationId.equals(r.getInfo())) {
				r.cancel();
			}
		}		
	}

	@SuppressWarnings("unchecked")
	public void clearTimers() {
		for (Timer r: (Collection<Timer>)timerservice.getTimers()) {
			r.cancel();
		}
		
	}
}
