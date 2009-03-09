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

package eu.secse.deliveryManager.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;

import polimi.reds.LocalDispatchingService;
import eu.secse.deliveryManager.federations.data.DMFederation;
import eu.secse.deliveryManager.federations.directory.webservice.IDirectoryWSInvoker;
import eu.secse.deliveryManager.federations.directoryreds.RedsDirectoryListener;
import eu.secse.deliveryManager.utils.IConfiguration;
import eu.secse.federationDirectory.reds.filters.DirectoryFilter;
import eu.secse.federationDirectory.reds.messages.EndpointRequest;
import eu.secse.reds.core.IRedsConnector;
import eu.secse.reds.exceptions.AlreadyExistingException;
import eu.secse.reds.exceptions.NotFoundException;


@Service(objectName="DeliveryManager:service=federationCoordinator")
public class FederationCoordinationMBean implements
		IFederationCoordinationManager {

	private static final Log log = LogFactory.getLog(IFederationCoordinationManager.class);	
	
	
	public final static String REDSID="deliverymanager-directory";
	
	@EJB
	private IFederationCoordinator coordinator;
	
	//For some obscure reason, Jboss fails to automatically find the bean
	@EJB(beanName="RedsConnector",name="RedsConnector")
	private IRedsConnector redsconnector;
	
	@EJB
	private IDirectoryWSInvoker dirinvoker;
	
	@EJB
	private IConfiguration config;
	
	@PersistenceContext(unitName="deliveryManager")
	protected EntityManager em;
	
	private LocalDispatchingService dispatcher;
	
	
	
	private Vector<String> endpoints;
	
	private Status status;
	
	private HashMap<String,String> proxynames;
	
	public void start() {
		
//		read proxies (gossip | ps)
		readProxies();
		
		//Set status
		status=Status.UNSYNCHRONIZED;
		dispatcher=null;
		endpoints=new Vector<String>();
		
		//start timers
		coordinator.startTimers();
		
		
		//Create reds listener
		RedsDirectoryListener listener=new RedsDirectoryListener();		
		try {
			redsconnector.registerDispatcher(REDSID,listener);
		} catch (AlreadyExistingException e) {
			log.error("Cannot register reds listener: already existing " + e.getMessage());
			return;
		}
		try {
			dispatcher=redsconnector.getDispatcher(REDSID);
		} catch (NotFoundException e) {
			log.error("Cannot retrieve reds dispatcher: not registered" + e.getMessage());
			return;
		}
		//subscription to: FederationInformationMessage EndpointRequest EndpointInfo
		dispatcher.subscribe(new DirectoryFilter());
		//send EndpointRequest message to discover DMFederation Directories
		discoverDirectories();
		
		//initialize FederationProxy
		Set<String> methods = proxynames.keySet();
		Iterator<String> iterator = methods.iterator();
		while(iterator.hasNext()){
			coordinator.getProxy(iterator.next()).initialize();
		}
				
	}

	public void stop() {
	
		//stop timers
		coordinator.stopTimers();		
		status=Status.UNSYNCHRONIZED;		
	}

	public String getStatus() {
		return status.getValue();
	}
	
	public Collection<String> getDirectories() {
		return endpoints; 
	}
	
	public void addDirectoryEndpoint(String endpoint) {
		endpoints.add(endpoint);		
	}
	

	public boolean synchronizeWithDirectory() {
		//synchronized with the first endpoint that replied the Endpoint info message
		if (endpoints.size()>0) {			
			synchronizeWithDirectory(endpoints.get(0));
			return true;
		}
		log.error("Could not synchronize, no known directories");
		return false;
	}
	
	@TransactionAttribute(value=TransactionAttributeType.REQUIRES_NEW)
	public void synchronizeWithDirectory(String endpoint) {
		log.debug("Synchronizing with directory " + endpoint);
		status=Status.REFRESHING;
		clearAcquiredFederations();
									
		Collection<DMFederation> federations=dirinvoker.getFederations(endpoint);
		log.debug("Directory " + endpoint + " contains " + federations.size() + " federations ");	
					
		//Add all federations obtained by the directory, except the existing ones (because of 
		//previous removal, existing are owned)
		for (DMFederation f:federations) {
			DMFederation existing=em.find(DMFederation.class,f.getId());			
			if (existing==null || !existing.isOwnership()) {
				log.debug("Adding federation " + f.getId());
				em.persist(f);
			} 
		}
		em.flush();
		status=Status.SYNCHRONIZED;
		log.debug("Synchronized with directory at " + endpoint);
	}
	 
	
	
	@SuppressWarnings("unchecked")
	@TransactionAttribute(value=TransactionAttributeType.REQUIRES_NEW)
	private void clearAcquiredFederations() {
		Query q=em.createNamedQuery(DMFederation.acquired);	
//		Remove all but owned federations
		Collection<DMFederation> acquiredfederations=(Collection<DMFederation>)q.getResultList();
		for (DMFederation f:acquiredfederations) {
			log.debug("Removing federation " + f.getId());		
			em.remove(f);			
		}
		em.flush();
	}

	public void discoverDirectories() {
		dispatcher.publish(new EndpointRequest());		
	}
	
	@SuppressWarnings("unchecked")
	public Collection<String> getFederations() {
		Query q=em.createNamedQuery(DMFederation.getall);
		Collection<String> fedids=new Vector<String>();
		Collection<DMFederation> feds=(Collection<DMFederation>)q.getResultList();
		for (DMFederation f: feds) {
			fedids.add(f.getId());
		}
		return fedids;
	}
	
	/* This method reads the available proxies' jndi name (gossip,ps) 
	 * and store them in proxynames
	 * 
	 */
	private void readProxies() {
		proxynames=new HashMap<String,String>();
		String s=config.getString("Proxy.keys");
		if (s.startsWith("!")) {
			log.error("Could not read proxy keys from configuration values");
		}
		String[] keys=s.split(",");
		for (String key:keys) {
			String value=config.getString("Proxy.value." + key);
			if (value.startsWith("!")) {
				log.error("Could not read proxy value from configuration values, for proxy " + key);
			}
			else {
				log.debug("Proxy name for " + key + " is " + value);
				proxynames.put(key,value);
			}
		}
	}

	/* returns jndi name of the proxyname*/
	public String getProxyLookupName(String proxyname) {
		return proxynames.get(proxyname); 		
	}
	
	public String[] getMethods() {
		return proxynames.keySet().toArray(new String[0]);
	}

}
