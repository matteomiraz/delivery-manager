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

package eu.secse.deliveryManager.logger;

import javax.annotation.PostConstruct;
import javax.ejb.PostActivate;
import javax.ejb.Stateless;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.mx.util.MBeanProxyExt;
import org.jboss.mx.util.MBeanServerLocator;

@Stateless
public class PerformanceLogger implements IPerformanceLogger {
	private static final Log log = LogFactory.getLog(IPerformanceLogger.class);
	
	private static final String SEPARATOR = "#";
	
	private IPerformanceLoggerMBean pLogger = null; 
	
	@PostConstruct
	@PostActivate
	public void init() {
		try {
			MBeanServer server = MBeanServerLocator.locate();
			pLogger = (IPerformanceLoggerMBean) MBeanProxyExt.create(IPerformanceLoggerMBean.class, "DeliveryManager:service=performanceLogger", server);
		} catch (MalformedObjectNameException e) {
			log.error("Error in PerformanceLogger: " + e.getMessage() + " due to: " + e.getCause());
		}					

	}
	
	public void log(long timestamp, String property, String elementId) {
		if(pLogger == null || !pLogger.isLoggingEnabled()) return; 
		
		log.info("Logging " + property);
		
		StringBuilder sb = new StringBuilder();
		sb.append(timestamp).append(SEPARATOR).append(property).append(SEPARATOR).append(elementId).append('\n');
		pLogger.log(sb.toString());
	}
	
	public boolean isEnabled() {
		return pLogger.isLoggingEnabled();
	}
}
