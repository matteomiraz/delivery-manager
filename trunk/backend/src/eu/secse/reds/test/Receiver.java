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

package eu.secse.reds.test;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import polimi.reds.LocalDispatchingService;
import polimi.reds.Message;
import polimi.reds.broker.overlay.AlreadyAddedNeighborException;
import polimi.reds.broker.overlay.GenericOverlay;
import polimi.reds.broker.overlay.LocalTransport;
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
import eu.secse.reds.LoggingRouter;
import eu.secse.reds.filters.WakeUpFilter;
import eu.secse.reds.messages.WakeUpMessage;

public class Receiver {

	private static final int REDS_TCP_PORT = 5555;
	private static final long STEP = 20 * 30 * 1000;

	/**
	 * Usage: java eu.secse.reds.Broker otherBroker listeningPort<br>
	 * where <b>otherBroker</b> is the ReDS url of the other broker (reds-tcp:host:port), and 
	 * <b>listeningPort</b> is the incoming TCP port of this broker. 
	 * @param args
	 */
	public static void main(String[] args) {

		int porta = REDS_TCP_PORT;

		if (args.length >= 2) {
			try {
				porta = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				System.err.println("Porta non valida: " + e.getMessage()
						+ "\n Uso la porta di default: " + REDS_TCP_PORT);
			}
		}

		// configuring logging facility
		final Logger logger = Logger.getLogger("polimi.reds");
		ConsoleHandler ch = new ConsoleHandler();
		logger.addHandler(ch);
		logger.setLevel(Level.ALL);
		ch.setLevel(Level.ALL);

		Set<Transport> transports = new LinkedHashSet<Transport>();
		transports.add(new TCPTransport(porta));
		LocalTransport localTransport = new LocalTransport();
		transports.add(localTransport);

		logger.info("ReDS listening on port " + porta);

		GenericOverlay overlay = new GenericOverlay(new SimpleTopologyManager(), transports);
		SubscriptionForwardingRoutingStrategy routingStrategy = new SubscriptionForwardingRoutingStrategy();
		DeferredUnsubscriptionReconfigurator reconf = new DeferredUnsubscriptionReconfigurator();
		GenericRouter genericRouter = new GenericRouter(overlay);
		LoggingRouter router = new LoggingRouter(logger, genericRouter);
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
		
		String neighbour = null;
		if(args.length >=1){
			neighbour = args[0];
			System.out.println("Connecting to "+neighbour);
	        try {
				overlay.addNeighbor("reds-tcp:"+neighbour+":"+REDS_TCP_PORT);
			} catch (ConnectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AlreadyAddedNeighborException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		final LocalDispatchingService ds = new LocalDispatchingService(localTransport);
		
		// create the interest and perform the subscription
		ds.subscribe(new WakeUpFilter());
		
		Thread wakeup = new Thread(new Runnable() {
			public void run() {
				try {
					ds.open();
					
					while (true) {
						// wait for the next message
						Message m = ds.getNextMessage();
						System.out.println("Received " + m);
					}
				} catch (ConnectException e) {
					System.err.println("Connection problem: " + e.getMessage() + "\n   due to: " + e.getCause());
				}
			}
		});
		
		wakeup.setDaemon(true);
		wakeup.start();
	}
}
