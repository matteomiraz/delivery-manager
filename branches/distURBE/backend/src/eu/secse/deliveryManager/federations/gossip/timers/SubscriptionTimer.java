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

import eu.secse.deliveryManager.federations.gossip.core.IGossipProxy;

@Stateless
public class SubscriptionTimer implements TimedObject, ISubscriptionTimer {

	@EJB
	private IGossipProxy proxy;
	
	
	public void ejbTimeout(Timer arg0) {
			// TODO resubscribe
		String federationdid=(String)arg0.getInfo();
		proxy.periodicalResubscribe(federationdid);		
	}

	@Resource
	private TimerService timerservice;

		
	public void startTimer(String federationId) {
		Calendar c=Calendar.getInstance();
		c.add(Calendar.DATE,1);		
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
