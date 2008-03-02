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

import it.s2.logger.EndEventNotAddedException;
import it.s2.logger.EventNotAddedException;
import it.s2.logger.EventNotDeletedException;
import it.s2.logger.SeCSELogger;
import it.s2.logger.SeCSELoggerServiceLocator;

import java.net.URL;
import java.rmi.RemoteException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.ejb.Stateless;
import javax.xml.rpc.ServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.secse.deliveryManager.registry.IRegistryProxy;
import eu.secse.deliveryManager.utils.IConfiguration;

@Stateless
public class SecseLoggerProxy implements ISecseLoggerProxy{
	private static final Log log = LogFactory.getLog(ISecseLoggerProxy.class);
	
	/** Is the Logger enabled?*/
	private boolean enabled;
	
	private SeCSELogger logger;
	
	@EJB IConfiguration config;
	@EJB IRegistryProxy registry;
	
	private static final String CONFIG_APPLICATION_ID = "LoggerProxy.applicationId";
	private static final String CONFIG_APPLICATION_NAME = "LoggerProxy.applicationName";
	private static final String CONFIG_URL = "LoggerProxy.url";
	private static final String CONFIG_ENABLED = "LoggerProxy.enable";
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#passivate()
	 */
	@PostConstruct
	@PostActivate
	public void init() {
		log.debug("Initializing SeCSE Logger");

		try {
			String en = config.getString(CONFIG_ENABLED);
			enabled = Boolean.parseBoolean(en);
		} catch (Throwable e) {
			log.warn("Unable to read logging status: disabling by default");
			enabled = false;
		}
		
		if(!enabled) return;
		
		URL loggerUrl;
		try {
			String str = config.getString(CONFIG_URL);
			loggerUrl = new URL(str);
		} catch (Throwable e) {
			log.warn("Unable to read logging endpoint: disabling logging");
			this.enabled = false;
			return;
		}

		// In order to replace the way the delivery manager contact the 
		// SeCSE Logger, modify the next lines:
		SeCSELoggerServiceLocator slsl = new SeCSELoggerServiceLocator();
		try{
			logger = slsl.getSeCSELogger(loggerUrl);
		}catch(ServiceException ex){
			log.error(ex.getMessage());
		}
		// END modify
			
		
	}
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.registry.IRegistryProxy#passivate()
	 */
	@PrePassivate
	public void passivate() {
		log.debug("passivating SeCSE Logger");
		this.logger = null;
	}


	public void deleteEvent(String applicationId, long eventId){
		if(!enabled) return;
		
		log.debug("Logging delete event "+ eventId+" applicationId "+applicationId);
		try {
			logger.deleteEvent(applicationId, eventId);
		} catch (EventNotDeletedException e) {
			log.warn(e.getMessage());
		} catch (RemoteException e) {
			log.warn(e.getMessage());
		}
	}

	public void endEvent(String applicationId, long eventId) {
		if(!enabled) return;

		log.debug("Logging end event "+ eventId+" applicationId "+applicationId);
		try {
			logger.endEvent(applicationId, eventId);
		} catch (EndEventNotAddedException e) {
			log.warn(e.getMessage());
		} catch (RemoteException e) {
			log.warn(e.getMessage());
		}
	}


	public String generateId(){
		if(!enabled) return "DISABLED";

		log.debug("Generating id");
		String id = null;
		try{
			id = logger.generateId();
		} catch(RemoteException ex){
			log.warn(ex.getMessage());
		}
		return id;
	}

	public String getVersion(){
		if(!enabled) return "SeCSE Logger DISABLED";

		log.debug("Getting version");
		String version = null;
		try {
			version = logger.getVersion();
			
		} catch (RemoteException e) {
			log.warn(e.getMessage());
		}
		return version;
	}

	public long startEvent(String event) {
		if(!enabled) return -1;

		log.debug("Logging start event "+event);
		long eventId = 0;
		try {
			eventId = logger.startEvent(registry.getRegistryId(), CONFIG_APPLICATION_ID, CONFIG_APPLICATION_NAME, null, null, event);
		} catch (EventNotAddedException e) {
			log.warn(e.getMessage());
		} catch (RemoteException e) {
			e.getMessage();
		}
		return eventId;
	}

	public long event(String event) {
		if(!enabled) return -1;

		log.debug("Logging event "+event);
		long eventId = 0;
		try {
			
			eventId = logger.event(registry.getRegistryId(), CONFIG_APPLICATION_ID,CONFIG_APPLICATION_NAME, null, null, event);
		} catch (EventNotAddedException e) {
			log.warn(e.getMessage());
		} catch (RemoteException e) {
			log.warn(e.getMessage());
		}
		return eventId;
	}
}
