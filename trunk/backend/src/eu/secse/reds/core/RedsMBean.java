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

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Timer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;

import polimi.reds.DispatchingService;
import polimi.reds.LocalDispatchingService;
import polimi.reds.NodeDescriptor;
import polimi.reds.broker.overlay.AlreadyAddedNeighborException;
import polimi.reds.broker.overlay.GenericOverlay;
import polimi.reds.broker.overlay.LocalTransport;
import polimi.reds.broker.overlay.Overlay;
import polimi.reds.broker.overlay.SimpleTopologyManager;
import polimi.reds.broker.overlay.TCPTransport;
import polimi.reds.broker.overlay.Transport;
import polimi.reds.broker.routing.DeferredUnsubscriptionReconfigurator;
import polimi.reds.broker.routing.GenericRouter;
import polimi.reds.broker.routing.GenericTable;
import polimi.reds.broker.routing.HashReplyTable;
import polimi.reds.broker.routing.ImmediateForwardReplyManager;
import polimi.reds.broker.routing.ReplyManager;
import polimi.reds.broker.routing.ReplyTable;
import polimi.reds.broker.routing.SubscriptionForwardingRoutingStrategy;
import polimi.reds.broker.routing.SubscriptionTable;
import eu.secse.deliveryManager.utils.IConfiguration;
import eu.secse.reds.exceptions.AlreadyExistingException;
import eu.secse.reds.exceptions.NotFoundException;
import eu.secse.reds.filters.WakeUpFilter;
import eu.secse.reds.messages.WakeUpMessage;

@Service(objectName="DeliveryManager:service=reds-integration")
public class RedsMBean implements IRedsMBean {
	private static final String REDS_TCP_PORT = "5555";
	
	// step for reconnection: used also by Reconnect
	public static final long STEP = 60 * 1000; 
	private static final Log log;
	
	static {
		log = LogFactory.getLog(IRedsMBean.class);		
	}
	
	private Hashtable<String,LocalDispatchingService> dispatchers;	
	private Hashtable<String, ARedsListener> listeners;
	
	//Does not exist in the original class
	private LocalTransport localTransport;
	
	private Timer keepAlive;
	
	private WakeUpListener wakeuplistener;
	
	private DispatchingService ds;
	private Overlay overlay;
	
	@EJB IKeepAliveMaster keepmaster;
	
	@EJB IReconnect reconnect;
	
	
	@EJB IConfiguration conf;

	@SuppressWarnings("unchecked")
	public Collection<String> getConnectedBrokers() {
		Set<NodeDescriptor> neighbors = overlay.getNeighbors();
		Collection<String> ret = new ArrayList<String>();
		
		for (NodeDescriptor n : neighbors) {
			StringBuilder sb = new StringBuilder("id: ").append(n.getID());
			for (int i = 0; i < n.getUrls().length; i++) {
				if(i == 0) sb.append(" urls: {");
				else sb.append(",");
				
				sb.append(n.getUrls()[i]);
			}
			sb.append("}");
			ret.add(sb.toString());
		}
		
		return ret;
	}

	public boolean addBroker(String url) throws ConnectException, MalformedURLException, AlreadyAddedNeighborException {
		if(overlay.numberOfBrokers() > 0) return false;

		overlay.addNeighbor(url);
		return true;
	}
	
	public boolean isRunning() {
		return this.ds != null && this.ds.isOpened();
	}

	public String getRedsCodebase() {
		return System.getProperties().getProperty("polimi.reds.client.codebase");
	}

	public void setRedsCodebase(String codebase) {
		System.getProperties().setProperty("polimi.reds.client.codebase", codebase);
	}
	
	public void resetRedsCodebase() {
		String rmiCodebase = System.getProperties().getProperty("java.rmi.server.codebase");
		this.setRedsCodebase(rmiCodebase);
	}
	
	/* (non-Javadoc)
	 * @see notification.reds.Reds#start()
	 */
	public void start() {		
		dispatchers=new Hashtable<String,LocalDispatchingService>();		
		listeners=new Hashtable<String, ARedsListener>();
		this.resetRedsCodebase();
		this.startReds();
	}
	
	private void startReds() {
		// --- begin start broker Reds ---
		if(overlay != null) {
			log.error("The ReDS broker is running...");
			return;
		}
		
		Set<Transport> transports = new LinkedHashSet<Transport>();
		String confport=conf.getString("Reds.port");
		int port=Integer.parseInt(REDS_TCP_PORT);
		try {
			port=Integer.parseInt(confport);
		} catch (NumberFormatException nfe) {
			log.warn("Could not parse Reds.port number from config file. Invalid value: " + confport);
		}		
		transports.add(new TCPTransport(port));
		log.info("ReDS listening on port " + port);
		localTransport = new LocalTransport();
		transports.add(localTransport);

		overlay = new GenericOverlay(new SimpleTopologyManager(), transports);
		SubscriptionForwardingRoutingStrategy routingStrategy = new SubscriptionForwardingRoutingStrategy();
		DeferredUnsubscriptionReconfigurator reconf = new DeferredUnsubscriptionReconfigurator();
		GenericRouter router = new GenericRouter(overlay);
		SubscriptionTable subscriptionTable = new GenericTable();

		routingStrategy.setOverlay(overlay);
		reconf.setOverlay(overlay);
		
//		Reply stuff
		ReplyManager replyMgr = new ImmediateForwardReplyManager();
	    ReplyTable replyTbl = new HashReplyTable();
	    replyMgr.setOverlay(overlay);
// 	end reply stuff
	    
		reconf.setRouter(router);
		router.setOverlay(overlay);
		router.setRoutingStrategy(routingStrategy);
		
		//Reply stuff
		router.setReplyManager(replyMgr);
		router.setReplyTable(replyTbl);
		replyMgr.setReplyTable(replyTbl);
		//end reply stuff
		
		router.setSubscriptionTable(subscriptionTable);
		overlay.start();

		// --- end start broker Reds ---

		try {
			String url = conf.getString("RedsMBean.BROKER");
			if (url != null) {
				overlay.addNeighbor(url);
				log.info("ReDS connected to broker " + url);
			}			
		} catch (Throwable e) {
			log.info("Error while connecting to the broker specified in the configuration file: " + e.getMessage() + " due to: " + e.getCause());
		}
		
		// Start Wake Up
		this.ds = new LocalDispatchingService(localTransport);
		

		try {
			this.ds.open();
		} catch (ConnectException e) {
			log.fatal("cannot connect to broker: " + e.getMessage() + "\n caused by: " + e.getCause());
			return;
		}			
		
		this.wakeuplistener = new WakeUpListener();
		this.wakeuplistener.setDispatcher(this.ds);
		this.wakeuplistener.start();
		
		log.info("Connected to the local ReDS broker.");

		ds.subscribe(new WakeUpFilter());
		
		/*The keepAlive value must be true only in the central broker
		 * 
		 */
		String keepAlive = conf.getString("KeepAlive");
		if(new Boolean(keepAlive)) {
			keepmaster.createTimer(5000,60000);				
			log.info("Keepalive activated");
		} else {
			log.debug("Keepalive disabled");

			//Creating reconnect timer
			reconnect.createTimer(STEP * 1/2, STEP);
		}	
	}

	/* (non-Javadoc)
	 * @see notification.reds.Reds#stop()
	 */
	public void stop() {		
		this.stopReds();
	}
	
	private void stopReds() {
		// stop the thread!
		if(this.wakeuplistener != null) this.wakeuplistener.stopListening();
		this.wakeuplistener = null;
		
		for (ARedsListener list:listeners.values()) {
			list.stopListening();
		}
		
		if(keepAlive != null) {
			this.keepAlive.cancel();
			this.keepAlive = null;
		}
				
		if(this.ds != null && this.ds.isOpened()) this.ds.close();
		this.ds = null;
		log.info("Connection with the local ReDS broker closed");
		
		this.overlay.stop();
		this.overlay = null;
		log.info("Local ReDS broker stopped");
	}

	public void registerDispatcher(String id,ARedsListener listener) throws AlreadyExistingException {
		LocalDispatchingService disp=dispatchers.get(id);
		if (disp!=null) throw new AlreadyExistingException();
		else {
			
			disp = new LocalDispatchingService(localTransport);						
			try {
				disp.open();
				dispatchers.put(id,disp);
				if (listener!=null) {
					listener.setDispatcher(disp);
					listener.start();
					listeners.put(id,listener);
				}				
			} catch (ConnectException e) {
				log.fatal("cannot connect to broker: " + e.getMessage() + "\n caused by: " + e.getCause());
				return;
			}			
		}
						
	}

	public LocalDispatchingService getDispatcher(String id) throws NotFoundException {		
		LocalDispatchingService local=dispatchers.get(id);
		if (local==null) throw new NotFoundException();
		return local;		
	}

	public void sendWakeUp() {
		ds.publish(new WakeUpMessage(System.currentTimeMillis()));		
	}

	public WakeUpListener getWakeUpListener() {
		return wakeuplistener;
	}

	public Overlay getOverlay() {
		return overlay;
	}


}