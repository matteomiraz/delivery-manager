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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
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

import polimi.reds.LocalDispatchingService;
import eu.secse.reds.exceptions.AlreadyExistingException;
import eu.secse.reds.exceptions.NotFoundException;
import eu.secse.reds.messages.WakeUpMessage;

@Stateless
public class KeepAliveMaster implements IKeepAliveMaster {
	private static final Log log = LogFactory.getLog(IKeepAliveMaster.class);
	private static String keepalivedispatcher="reds-keep-alive";
	
	@Resource
	TimerService timerService;	
	
	private IRedsMBean reds;
	
	@PostConstruct
	public void lookupMBean() {
		 MBeanServer server = MBeanServerLocator.locate();	        
			try {
				reds = (IRedsMBean) MBeanProxyExt.create(IRedsMBean.class, "DeliveryManager:service=reds-integration", server);			
			} catch (MalformedObjectNameException e) {			
		        log.warn(e.getMessage());	      		    
			}	        	  
	} 
	
	@PreDestroy
	public void nullify() {
		reds=null;
	}
	
	public Timer createTimer(long init, long step) {
		try {
			reds.registerDispatcher(keepalivedispatcher,null);
		} catch (AlreadyExistingException e) {
			log.error("Could not create keepalive dispatcher");
		}
		return this.timerService.createTimer(init, step, "WakeUP");
		
	}
	
	@Timeout
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void timeoutHandler(Timer timer) {
		log.debug("received timer event: " + timer.getInfo());
		LocalDispatchingService disp;
		try {
			disp = reds.getDispatcher(keepalivedispatcher);
			disp.publish(new WakeUpMessage(System.currentTimeMillis()));
		} catch (NotFoundException e) {
			log.error("Could not lookup keepalive dispatcher");
		}
	
	}

}
