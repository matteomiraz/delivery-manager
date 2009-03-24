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

package eu.secse.federationDirectory;

import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;

import polimi.reds.LocalDispatchingService;
import polimi.reds.Message;
import eu.secse.deliveryManager.utils.IConfiguration;
import eu.secse.federationDirectory.adapters.IFederationDataConverter;
import eu.secse.federationDirectory.db.FederationData;
import eu.secse.federationDirectory.db.FederationProperty;
import eu.secse.federationDirectory.reds.filters.DirectoryFilter;
import eu.secse.federationDirectory.reds.messages.EndpointRequest;
import eu.secse.federationDirectory.reds.types.RedsFederationData;
import eu.secse.federationDirectory.redsintegration.RedsListener;
import eu.secse.federationDirectory.wsclient.IDirectoryProxy;
import eu.secse.reds.core.IRedsConnector;
import eu.secse.reds.exceptions.AlreadyExistingException;
import eu.secse.reds.exceptions.NotFoundException;

@Service(objectName="FederationDirectory:service=fedlist")
public class DirectoryMBean implements IDirectoryMBean {
	//TODO: controllare se viene fatta la sottoscrizione al "topic" delle directory
	
	private static final Log log = LogFactory.getLog(DirectoryMBean.class);
	
	private final static String REDSID="directory-reds";
	
	private Vector<String> otherdirectories;
	
	private boolean mustsync;
	
	@EJB 
	private IDirectoryModelManager feddirectory;	
	
	@EJB
	private IDirectoryProxy proxy;
	
	@EJB
	private IRedsConnector redsproxy;
	
	@EJB 
	private IFederationDataConverter converter;
	
	@PersistenceContext(unitName="federationdirectory")
	private EntityManager manager;
	
	@EJB
	private IConfiguration configuration;	
	
	private LocalDispatchingService dispatcher;
	
	private RedsListener listener;
	
	
	
	public String[] getFederationList() {		
		FederationData[] feddata=feddirectory.getAllFederations();
		
		if (feddata!=null && feddata.length!=0) {
			String[] list=new String[feddata.length];
			int i=0;
			for (FederationData fd: feddata) {
				if (fd!=null) {
					list[i]=fd.getId().getId();
					i++;
				}
			}
			return list;
		}
		return null;
	}
	
	public String[] getDirectoryLists() {
		return otherdirectories.toArray(new String[1]);
	}
	
	public void start() {
		otherdirectories=new Vector<String>();		
		mustsync=false;
		registerRedsDispatcher();
		synchronizeList();		
		
		feddirectory.startLeaseTimer();
	}
	
	public void synchronizeWithOtherDirectory(String endpointurl) {
		feddirectory.removeAllFederations();
		Collection<RedsFederationData> data=proxy.getAllFederations(endpointurl);		
		if (data!=null) {			
			for (RedsFederationData d:data) {								
				feddirectory.addFederationData(converter.convertData(d));
			}
		}
	}

	public void addOtherDirectoryEndpoint(String endpoint) {
		otherdirectories.add(endpoint);
	}
	
	public void stop() {	
		feddirectory.stopLeaseTimer();
	}

	private void synchronizeList() {
		sendMessage(new EndpointRequest());
	}
	
	private void sendMessage(Message m) {
		dispatcher.publish(m);		
	}
	
	public void addFederation(String uid) {
		FederationData fed=new FederationData(uid);
		fed.setName("Test Federation");
		fed.setMethod("Gossip");
		Collection<FederationProperty> props=new Vector<FederationProperty>();
		FederationProperty prop=new FederationProperty("testname","testvalue");
		FederationProperty prop2=new FederationProperty("testname2","testvalue2");
		props.add(prop);
		props.add(prop2);
		fed.setProperties(props);
		GregorianCalendar gc=(GregorianCalendar)GregorianCalendar.getInstance();
		gc.add(GregorianCalendar.HOUR,24*7);
		Date d=gc.getTime();
		fed.setLeaseExpiration(d.getTime());
		
		manager.persist(fed);		
	}

	public boolean mustSynchronize() {
		return mustsync;
	}

	public void setSynchronized(boolean sync) {
		this.mustsync=!sync;
		
	}

	public void discoverDirectories() {
		synchronizeList();		
	}
	
	private void registerRedsDispatcher() {
		listener = new RedsListener();
		String endpoint=configuration.getString("Directory.WSEndpoint");
		listener.setEndpoint(endpoint);
		try {
			redsproxy.registerDispatcher(REDSID,listener);
		} catch (AlreadyExistingException e) {
			log.error("Reds listener already registered");
		}
		
		dispatcher=null;
		try {
			dispatcher = redsproxy.getDispatcher(REDSID);			
		} catch (NotFoundException e) {
			log.error("Could not get dispatcher");
		}
		dispatcher.subscribe(new DirectoryFilter());
	}

}
