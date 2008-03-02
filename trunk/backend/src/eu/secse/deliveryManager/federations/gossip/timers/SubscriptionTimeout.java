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

import java.util.Collection;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.secse.deliveryManager.federations.gossip.core.IGossipProxy;
import eu.secse.deliveryManager.utils.IConfiguration;

@Stateless
public class SubscriptionTimeout implements TimedObject,
		ISubscriptionTimeout {
	
	private static final Log log = LogFactory.getLog(ISubscriptionTimeout.class);
	private static final int timeout_seconds=600;
	
	
	
	@EJB 
	private IConfiguration config;
	 
	@Resource	
	private TimerService timerservice;
	
		
	
	@EJB 
	private IGossipProxy proxy;
	
	public void ejbTimeout(Timer arg0) {
		//No one has answered. 
		proxy.subscriptionNotAnswered((String)arg0.getInfo());
	}

//	public void joinAndWait(String federationid)  {		
//		try {
//			subscribeToContact(federationid);
//		} catch (NotJoinedException e) {
//			log.error("Could not join federation " + federationid);
//		}		
//	}
//	
//	private void subscribeToContact(String federationid) throws NotJoinedException{
//		FederationEnt f=manager.find(FederationEnt.class,federationid);		
//		if (f==null) {
//			log.error("Could not get federation information:");
//			return;
//		}
//		GossipFederationInfo gf=(GossipFederationInfo)f.getExtraInfo();
//		String contact=gf.getInitialcontact();
//		Subscription s=new Subscription(gossipmanager.getAddress(),contact,federationid);
//		sender.send(s);
//	}

	public void startTimer(String federationid) {
		//To avoid DB problems
		cancelTimer(federationid);
		try {
			String s=config.getString("Gossip.subscription_timeout");
			long timeout=Long.parseLong(s);
			log.info("Creating subscription timeout with value: " + timeout + " seconds");
			timerservice.createTimer(timeout*1000,federationid);
			return;
		} catch (NumberFormatException nfe) {
			log.warn("Could not read Gossip.subscription_timeout configuration property. using default: " + timeout_seconds + " seconds");
		}	catch (NullPointerException npe) {
			log.warn("Could not read Gossip.subscription_timeout configuration property. using default: " + timeout_seconds + " seconds");
		}
		timerservice.createTimer(timeout_seconds*1000,federationid);
		
		
	}

	@SuppressWarnings("unchecked")
	public void cancelTimer(String federationid) {
		for (Timer t:(Collection<Timer>)timerservice.getTimers()) {
			if (t.getInfo().equals(federationid)) {
				t.cancel();
			}
		}		
	}

}
