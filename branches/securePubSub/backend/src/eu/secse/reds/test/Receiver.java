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

import Support.SimpleExpression;
import eu.secse.CryptoPerformanceLogger;
import eu.secse.deliveryManager.interest.Interest;
import eu.secse.deliveryManager.interest.InterestAuthenticMessage;
import eu.secse.deliveryManager.interest.InterestFederation;
import eu.secse.deliveryManager.interest.InterestOnMetadata;
import eu.secse.deliveryManager.model.DFederationEncryptedMessage;
import eu.secse.deliveryManager.model.DFederationPlainMessage;
import eu.secse.deliveryManager.reds.Envelope;
import eu.secse.deliveryManager.reds.InterestEnvelope;
import eu.secse.deliveryManager.reds.InterestEnvelopeWithMetadata;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;
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
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.crypto.spec.SecretKeySpec;

public class Receiver {

    private static final int REDS_TCP_PORT = 5555;

    /**
     * Usage: java eu.secse.reds.test.Receiver otherBroker listeningPort<br>
     * where <b>otherBroker</b> is the ReDS host of the other broker (it will connect to the 5555 port (the default)), and 
     * <b>listeningPort</b> is the incoming TCP port of this broker. 
     * @param args
     */
    public static void main(String[] args) {

        int porta = REDS_TCP_PORT;

        if (args.length >= 2) {
            try {
                porta = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Porta non valida: " + e.getMessage() + "\n Uso la porta di default: " + REDS_TCP_PORT);
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
        if (args.length >= 1) {
            neighbour = args[0];
            System.out.println("Connecting to " + neighbour);
            try {
                overlay.addNeighbor("reds-tcp:" + neighbour + ":" + REDS_TCP_PORT);
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

        Thread wakeup = new Thread(new Runnable() {

            public void run() {
                {
                    //reading CA cert form file
                    InputStream input = null;
                    try {
                        ds.open();
                        //Loading federation Key
                        input = new FileInputStream("federationKey.key");
                        byte[] key = new byte[input.available()];
                        input.read(key);
                        input.close();

                        SecretKeySpec federationKey = new SecretKeySpec(key, "AES");

                        //Loading trusted certificate
                        input = new FileInputStream("valeryCert.cer");
                        CertificateFactory cf = CertificateFactory.getInstance("X.509");
                        X509Certificate trustedCert = (X509Certificate) cf.generateCertificate(input);
                        input.close();
                        // creating a interest for signed message
                        
                        Interest interest = new InterestFederation("1");
                        InterestAuthenticMessage metaInterest = new InterestAuthenticMessage(trustedCert);
                        metaInterest.setAuthenticCheckEnabled(false);

                        ds.subscribe(new InterestEnvelopeWithMetadata(interest,
                                new SimpleExpression(metaInterest), ""));
                        
                         logger.fine("Sottoscrizione");

                        // create the interest and perform the subscription
                        //ds.subscribe(new WakeUpFilter());
                        while (true) {
                            // wait for the next message
                            Message m = ds.getNextMessage();
                            logger.fine("Received: " + m);

                            if (m instanceof Envelope) {
                                Envelope e = (Envelope) m;
                                if (e.getObject() instanceof DFederationEncryptedMessage) {
                                    long start;
                                    //Decyphering received message
                                    if (CryptoPerformanceLogger.LOG_DECRYPT_TIME) {
                                        start = System.nanoTime();
                                    }
                                    
                                    DFederationPlainMessage decryptedMessage= ((DFederationEncryptedMessage) e.getObject()).decrypt(federationKey);                              
                                    
                                    if(CryptoPerformanceLogger.LOG_DECRYPT_TIME){
                                        long stop = System.nanoTime();
                                        CryptoPerformanceLogger.getSingleton().logDecryptionTime(decryptedMessage.getType(), stop-start);
                                    }
                                }
                            }

                        }
                    } catch (InvalidKeyException ex) {
                        Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (CertificateException ex) {
                        Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        try {
                            input.close();
                        } catch (IOException ex) {
                            Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });

        wakeup.setDaemon(true);
        wakeup.start();
    }
}
