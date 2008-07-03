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


import javax.ejb.Stateless;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Depends;
import org.jboss.mx.util.MBeanProxyExt;
import org.jboss.mx.util.MBeanServerLocator;

import polimi.reds.LocalDispatchingService;
import eu.secse.reds.exceptions.AlreadyExistingException;
import eu.secse.reds.exceptions.NotFoundException;

@Stateless
@Depends ({"DeliveryManager:service=reds-integration"})
public class RedsConnector implements IRedsConnector {
	
	private static final Log log = LogFactory.getLog(RedsConnector.class);
	
	public void registerDispatcher(String id, ARedsListener listener)
		throws AlreadyExistingException {		
		        MBeanServer server = MBeanServerLocator.locate();
		        IRedsMBean reds;
				try {
					reds = (IRedsMBean) MBeanProxyExt.create(IRedsMBean.class, "DeliveryManager:service=reds-integration", server);
					reds.registerDispatcher(id,listener);
				} catch (MalformedObjectNameException e) {			
			        log.warn(e.getMessage());	      		    
				}	        	    
	}

	public LocalDispatchingService getDispatcher(String id)
			throws NotFoundException {
        MBeanServer server = MBeanServerLocator.locate();
        IRedsMBean reds;
		try {
			reds = (IRedsMBean) MBeanProxyExt.create(IRedsMBean.class, "DeliveryManager:service=reds-integration", server);
			return reds.getDispatcher(id);
		} catch (MalformedObjectNameException e) {			
	        log.warn(e.getMessage());	      		    
		}	       
		throw new NotFoundException("Could not lookup Reds MBean");
	}

}
