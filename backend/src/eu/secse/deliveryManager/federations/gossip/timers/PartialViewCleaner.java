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
import java.util.GregorianCalendar;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.mx.util.MBeanProxyExt;
import org.jboss.mx.util.MBeanServerLocator;

import eu.secse.deliveryManager.federations.gossip.data.DeliveryManagerGossipInfo;
import eu.secse.deliveryManager.federations.gossip.messaging.IGossipMessagingManager;

@Stateless
public class PartialViewCleaner implements
		IPartialViewCleaner, TimedObject {
	
	private static final Log log = LogFactory.getLog(IPartialViewCleaner.class);
	
	@PersistenceContext(unitName="deliveryManager")
	private EntityManager manager;
	
	@Resource
	private TimerService timerservice;
	
	private IGossipMessagingManager  gossipmanager;
	
	@SuppressWarnings("unchecked")
	public void ejbTimeout(Timer arg0) {
		String federationid=(String)arg0.getInfo();
		log.debug("Removing expired members from partial view of federation " + federationid);		
		Query q=manager.createNamedQuery(DeliveryManagerGossipInfo.get_expired_contacts);
		q.setParameter("federationid",federationid);
		Collection<DeliveryManagerGossipInfo> info=(Collection<DeliveryManagerGossipInfo>)q.getResultList();
		for (DeliveryManagerGossipInfo d:info) {
			log.debug("Removed member " + d.getAddress() + " from partial view of federation " + federationid);
			if (!d.isInview() && !d.isLiveinview()) {				
				manager.remove(d);				
			} else {
				d.setPartialview(false);
			}
		}
	}

	public void startNewTimer(String federationid) {
		//one week timer, periodical
		Calendar c=Calendar.getInstance();
		c.add(Calendar.WEEK_OF_YEAR,1);
		timerservice.createTimer(c.getTime(),3600*24*7,federationid);
	}

	@SuppressWarnings("unchecked")
	public void clearTimers() {
		for (Timer t: (Collection<Timer>)timerservice.getTimers()) {
			t.cancel();
		}
		
	}

	@PostConstruct
	public void lookupmbean() {		
		 MBeanServer server = MBeanServerLocator.locate();
		try {
			gossipmanager=(IGossipMessagingManager)MBeanProxyExt.create(IGossipMessagingManager.class, "DeliveryManager:service=GossipMessaging", server);			
		} catch (MalformedObjectNameException e) {			
	        log.error(e.getMessage());	      		    
		}	   
	}

	public void scheduleCleaning(DeliveryManagerGossipInfo info) {			
		Calendar c=new GregorianCalendar();
		c.add(Calendar.MILLISECOND,gossipmanager.getPartialViewLeaseTimeout());
		info.setPartial_expire(c.getTime());		
		// +10 to avoid small timing errors to create problems
		timerservice.createTimer(gossipmanager.getPartialViewLeaseTimeout()+10,info.getFederationinfo().getFederation().getId());
	}

	
}
