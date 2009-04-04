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

package eu.secse.reds.core;

import java.util.Date;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.mx.util.MBeanProxyExt;
import org.jboss.mx.util.MBeanServerLocator;

import polimi.reds.NodeDescriptor;
import polimi.reds.broker.overlay.Overlay;
import eu.secse.deliveryManager.utils.IConfiguration;

@Stateless
public class Reconnect implements IReconnect{

	private static final Log log = LogFactory.getLog(IReconnect.class);

	@EJB
	private IConfiguration conf;
	
	@Resource
	TimerService timerService;	
	
	public Timer createTimer(long init, long step) {
		return this.timerService.createTimer(init, step, "IReconnect");
	}
	
	@Timeout
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void timeoutHandler(Timer timer) {
		
		
		MBeanServer server = MBeanServerLocator.locate();
		try {
			 IRedsMBean redsMBean =(IRedsMBean)MBeanProxyExt.create(IRedsMBean.class, "DeliveryManager:service=reds-integration", server);			
			 WakeUpListener wakeupListener = redsMBean.getWakeUpListener();
			 long last = wakeupListener.getLastMessage();
			 long current = System.currentTimeMillis();
			 long delta = current - last;
			 
			 if(delta < RedsMBean.STEP * 1.1)  
				 log.debug("Everything seems OK: currentTime: "+ new Date(current) +" lastMessageTime: "+ new Date(last) + "; delta: " + delta/1000.0f + " seconds");
			 else if (delta < RedsMBean.STEP * 3 ) 
				 log.info("Connection warning: currentTime: "+ new Date(current) +" lastMessageTime: "+ new Date(last) + "; delta: " + delta/1000.0f + " seconds");
			 else  {
				 log.warn("Connection Lost: currentTime: "+ new Date(current) +" lastMessageTime: "+ new Date(last) + "; delta: " + delta/1000.0f + " seconds. Trying to reconnect");
				 reconnect(redsMBean.getOverlay());
			}
		} catch (MalformedObjectNameException e) {			
			log.error(e.getMessage());	      		    
		}	  
	
	}

	
	@SuppressWarnings("unchecked")
	private void reconnect(Overlay overlay){
		try {
			String url = conf.getString("RedsMBean.BROKER");
			if (url != null) {
				// controlla se prima mi devo scollegare dal broker
				NodeDescriptor old = findNodeDescriptor(url, overlay.getNeighbors());
				if(old != null) overlay.removeNeighbor(old);
				NodeDescriptor id = overlay.addNeighbor(url);

				StringBuilder sb = new StringBuilder();
				for (String s : id.getUrls())
					sb.append(" ").append(s);
				
				log.info("ReDS connected to broker " + sb.toString());
			
				// Reset the timers 
				try {
					MBeanServer server = MBeanServerLocator.locate();
					IRedsMBean redsMBean = (IRedsMBean) MBeanProxyExt.create( IRedsMBean.class, "DeliveryManager:service=reds-integration", server);
					redsMBean.getWakeUpListener().reset();
				} catch (Throwable e) {
					// Ignore all errors
				}

			} else log.fatal("Unable to reconnect");
		} catch (Throwable e) {
			log.info("Error while connecting to the broker specified in the configuration file - " + e.getClass().getCanonicalName() + ": " + e.getMessage() + " due to: " + e.getCause());
		}
	}

	private NodeDescriptor findNodeDescriptor(String url, Set<NodeDescriptor> vicini) {
		for (NodeDescriptor d : vicini) {
			for (String u : d.getUrls()) {
				if(url.equals(u)) return d;
			}
		}
		return null;
	}


}
