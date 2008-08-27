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

import eu.secse.CryptoPerformanceLogger;
import eu.secse.deliveryManager.model.DFederationEncryptedMessage;
import eu.secse.deliveryManager.model.DFederationPlainMessage;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.model.MetaData;
import eu.secse.deliveryManager.model.MetaDataSignature;
import eu.secse.deliveryManager.reds.Envelope;
import eu.secse.deliveryManager.reds.EnvelopeWithMetadata;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import polimi.reds.LocalDispatchingService;
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
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertPath;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Calendar;
import javax.crypto.spec.SecretKeySpec;

public class Sender {

    private static final int REDS_TCP_PORT = 5555;
    private static final long STEP = 1000;

    /**
     * Usage: java eu.secse.reds.test.Sender otherBroker listeningPort<br>
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
                    //Lettura dei files
                    //Lettura certificato
                    InputStream input = null;
                    try {
                        input = new FileInputStream("aliceCert.cer");
                        CertificateFactory cf = CertificateFactory.getInstance("X.509");
                        X509Certificate aliceCert = (X509Certificate) cf.generateCertificate(input);
                        input.close();

                        // loading Key
                        input = new FileInputStream("alicePrivateKey.key");
                        KeyFactory kf = KeyFactory.getInstance("RSA");
                        byte[] key = new byte[input.available()];
                        input.read(key);
                        PKCS8EncodedKeySpec keysp = new PKCS8EncodedKeySpec(key);
                        PrivateKey privateKey = kf.generatePrivate(keysp);
                        input.close();
                        
                        //Generazione certification path
                        ArrayList<X509Certificate> certificates = new ArrayList<X509Certificate>();
                        try {
                            cf = CertificateFactory.getInstance("X.509");
                        } catch (CertificateException ex) {
                            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        certificates.add(aliceCert);
                        CertPath certificationPath = null;
                        try {
                            certificationPath = cf.generateCertPath(certificates);
                        } catch (CertificateException ex) {
                            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        //loading federation key
                        input = new FileInputStream("federationKey.key");
                        key = new byte[input.available()];
                        input.read(key);
                        input.close();
                        SecretKeySpec federationKey = new SecretKeySpec(key, "AES");

                        ds.open();

                        while (true) {
                            // create a message and log it
                            //WakeUpMessage w = new WakeUpMessage(System.currentTimeMillis());

                            Deliverable d = new DService("1", "Test", "0.99", "0.01", false, Calendar.getInstance().getTime().toString(), Calendar.getInstance().getTime().toString(), null);
                            MetaData m = null;
                            try {
                                m = new MetaDataSignature(d, privateKey, "SHA512WithRSA", certificationPath);
                            } catch (NoSuchAlgorithmException ex) {
                                Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (InvalidKeyException ex) {
                                Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                            DFederationPlainMessage federationMessage = 
                                    new DFederationPlainMessage("1", d,m);
                            
                            long start;
                            if(CryptoPerformanceLogger.LOG_ENCRYPT_TIME){
                                start = System.nanoTime();                               
                            }
                            
                            DFederationEncryptedMessage encMessage = federationMessage.encrypt(federationKey);
                            
                            
                            if(CryptoPerformanceLogger.LOG_ENCRYPT_TIME){
                                long stop = System.nanoTime();
                                CryptoPerformanceLogger.getSingleton().logEncryptionTime(encMessage.getType(), stop-start);
                            }
                            
                            MetaData signature = null;
                            try {
                                if(CryptoPerformanceLogger.LOG_SIGN_TIME){
                                    start = System.nanoTime();
                                }
                                signature = new MetaDataSignature(encMessage, privateKey, "SHA512WithRSA", certificationPath);
                                if(CryptoPerformanceLogger.LOG_SIGN_TIME){
                                    long stop = System.nanoTime();
                                    CryptoPerformanceLogger.getSingleton().logSignTime(encMessage.getType(), stop-start);
                                }
                            } catch (NoSuchAlgorithmException ex) {
                                Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (InvalidKeyException ex) {
                                Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                            Envelope messageMatching = new EnvelopeWithMetadata(encMessage,signature);
                            logger.fine("Sending test: " + messageMatching.toString());

                            // send the message
                            ds.publish(messageMatching);

                            Thread.sleep(STEP);
                        }
                    } catch (InvalidKeySpecException ex) {
                        Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NoSuchAlgorithmException ex) {
                        Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (CertificateException ex) {
                        Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InterruptedException e) {
                    } finally {
                        try {
                            input.close();
                        } catch (IOException ex) {
                            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });

        wakeup.setDaemon(true);
        wakeup.start();
    }
}
