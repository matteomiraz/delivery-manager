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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.mx.util.MBeanProxyExt;
import org.jboss.mx.util.MBeanServerLocator;

import eu.secse.deliveryManager.data.FederationEnt;
import eu.secse.deliveryManager.federations.gossip.core.IGossipProxy;
import eu.secse.deliveryManager.federations.gossip.data.DeliveryManagerGossipInfo;
import eu.secse.deliveryManager.federations.gossip.data.FederationSubscriptionStatus;
import eu.secse.deliveryManager.federations.gossip.data.GossipFederationInfo;
import eu.secse.deliveryManager.federations.gossip.messaging.IGossipMessagingManager;
import eu.secse.deliveryManager.federations.gossip.messaging.IMessageSender;
import eu.secse.deliveryManager.federations.gossip.messaging.messages.HeartBeatMessage;


@Stateless
public class HeartBeatTimer implements TimedObject, IHeartBeatTimer {
	
	private static int period_in_days=1; 
	
	private static final Log log = LogFactory.getLog(IHeartBeatTimer.class);
	
	@PersistenceContext(unitName="deliveryManager")
	private EntityManager manager;
	
	@Resource
	private TimerService timerservice;
	
	@EJB
	private IMessageSender sender;
	

	private IGossipMessagingManager gossipmanager;
	
	@PostConstruct
	public void lookupmbean() {		
		 MBeanServer server = MBeanServerLocator.locate();
		try {
			gossipmanager=(IGossipMessagingManager)MBeanProxyExt.create(IGossipMessagingManager.class, "DeliveryManager:service=GossipMessaging", server);			
		} catch (MalformedObjectNameException e) {			
	        log.error(e.getMessage());	      		    
		}	   
	}
	
	@EJB
	private IGossipProxy proxy;
	
	public void ejbTimeout(Timer timer) {
		String federationid=(String)timer.getInfo();		
		FederationEnt f=(FederationEnt)manager.find(FederationEnt.class, federationid);
		if (f==null) return;
		GossipFederationInfo gossipextra=(GossipFederationInfo)f.getExtraInfo();
		
		String status=gossipextra.getStatus();
		String myaddress=gossipmanager.getAddress();
		if (status.equals(FederationSubscriptionStatus.FEDERATED.getValue())) {
			//Get partial view
			for (DeliveryManagerGossipInfo dm_info:proxy.getPartialView(f)) {				
				HeartBeatMessage message=new HeartBeatMessage(myaddress,dm_info.getAddress(),federationid);
				sender.send(message);
			}
		}
				
	}

	public void startTimer(String federationId) {
		Calendar c=Calendar.getInstance();
		c.add(Calendar.DATE,period_in_days);		
		timerservice.createTimer(c.getTime(),period_in_days*1000*3600*24,federationId);		
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
