/* This file is part of Delivery Manager.
 * (c) 2006 Matteo Miraz, Politecnico di Milano
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


package eu.secse.deliveryManager.reds;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;

import polimi.reds.DispatchingService;
import polimi.reds.LocalDispatchingService;
import polimi.reds.Message;
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
import polimi.reds.broker.routing.SubscriptionForwardingRoutingStrategy;
import polimi.reds.broker.routing.SubscriptionTable;
import eu.secse.deliveryManager.IConfiguration;
import eu.secse.deliveryManager.IInterestManager;

@Service(objectName="DeliveryManager:service=reds")
public class RedsMBean implements IRedsMBean {
	private static final String REDS_TCP_PORT = "5555";

	private static final Log log = LogFactory.getLog(IRedsMBean.class);

	private RedsListener thread;
	private DispatchingService ds;
	private Overlay overlay;

	@EJB private IInterestManager interestManager;
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
		this.resetRedsCodebase();
		this.startReds();
	}
	
	private void startReds() {
		if(this.ds != null && this.ds.isOpened()) return;

		// --- begin start broker Reds ---
		if(overlay != null) {
			log.error("The ReDS broker is running...");
			return;
		}
		
		Set<Transport> transports = new LinkedHashSet<Transport>();
		transports.add(new TCPTransport(Integer.parseInt(REDS_TCP_PORT)));
		LocalTransport localTransport = new LocalTransport();
		transports.add(localTransport);

		overlay = new GenericOverlay(new SimpleTopologyManager(), transports);
		SubscriptionForwardingRoutingStrategy routingStrategy = new SubscriptionForwardingRoutingStrategy();
		DeferredUnsubscriptionReconfigurator reconf = new DeferredUnsubscriptionReconfigurator();
		GenericRouter router = new GenericRouter(overlay);
		SubscriptionTable subscriptionTable = new GenericTable();

		routingStrategy.setOverlay(overlay);
		reconf.setOverlay(overlay);

		reconf.setRouter(router);
		router.setOverlay(overlay);
		router.setRoutingStrategy(routingStrategy);
		router.setSubscriptionTable(subscriptionTable);
		overlay.start();

		// --- end start broker Reds ---

		this.ds = new LocalDispatchingService(localTransport);
		
		try {
			this.ds.open();
		} catch (ConnectException e) {
			log.fatal("cannot connect to broker: " + e.getMessage() + "\n caused by: " + e.getCause());
			return;
		}
		
		this.thread = new RedsListener(this.ds);
		this.thread.start();
		log.info("Connected to the local ReDS broker.");

		try {
			String url = conf.getString("RedsMBean.BROKER");
			if (url != null) {
				overlay.addNeighbor(url);
				log.info("ReDS connected to broker " + url);
			}			
		} catch (Throwable e) {
			log.info("Error while connecting to the broker specified in the configuration file: " + e.getMessage() + " due to: " + e.getCause());
		}
		
		try {
			String keepAlive = conf.getString("KeepAlive");
			if(new Boolean(keepAlive)) {
				ds.subscribe(new WakeUpFilter());
				log.info("Keepalive activated");
			} else {
				log.debug("Keepalive disabled");
			}
		} catch (Throwable t) {
			
		}
		
		// subscribe to all filters
		this.interestManager.subscribeAll();
	}

	/* (non-Javadoc)
	 * @see notification.reds.Reds#stop()
	 */
	public void stop() {
		this.stopReds();
	}
	
	private void stopReds() {
		// stop the thread!
		if(this.thread != null) this.thread.ferma();
		this.thread = null;
		
		if(this.ds != null && this.ds.isOpened()) this.ds.close();
		this.ds = null;
		log.info("Connection with the local ReDS broker closed");
		
		this.overlay.stop();
		this.overlay = null;
		log.info("Local ReDS broker stopped");
	}

	public DispatchingService dispatchingService() {
		return this.ds;
	}

	public void publish(Message msg) {
		this.ds.publish(msg);
	}
}