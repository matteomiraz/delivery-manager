package perfEval;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import polimi.reds.broker.overlay.GenericOverlay;
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

public class Broker {
	
	public static void main(String[] args) throws Exception {
		
		if (args.length < 1) {
			System.out.println("Usage: listeningPort [otherBroker]");
			System.exit(1);
		}

		// configuring logging facility
		final Logger logger = Logger.getLogger("polimi.reds");
		ConsoleHandler ch = new ConsoleHandler();
		logger.addHandler(ch);
		logger.setLevel(Level.INFO);
		ch.setLevel(Level.INFO);
//		logger.setLevel(Level.ALL);
//		ch.setLevel(Level.ALL);
		
		Set<Transport> transports = new LinkedHashSet<Transport>();
		transports.add(new TCPTransport(Integer.parseInt(args[0])));

		GenericOverlay overlay = new GenericOverlay(new SimpleTopologyManager(), transports);
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

		if(args.length >=2){
			System.out.println("Connecting to "+args[1]);
	        try {
				overlay.addNeighbor(args[1]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		
		System.out.println("up and running!");

	}
}
