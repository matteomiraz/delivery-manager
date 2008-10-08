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
package eu.secse.deliveryManager.federations.securepubsub.core;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPath;
import java.security.cert.CertificateException;
import java.util.Date;

import java.util.Enumeration;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.Timer;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;

import polimi.reds.ComparableFilter;
import polimi.reds.LocalDispatchingService;
import eu.secse.deliveryManager.federations.securepubsub.data.SecPubSubFederationExtraInfo;
import eu.secse.deliveryManager.federations.securepubsub.reds.RedsSecPubSubFederationListener;
import eu.secse.deliveryManager.federations.securepubsub.timer.IFedSecPsExpire;
import eu.secse.deliveryManager.federations.securepubsub.timer.IFedSecPsRenew;
import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.model.MetaData;
import eu.secse.deliveryManager.reds.Envelope;
import eu.secse.deliveryManager.reds.EnvelopeWithMetadata;
import eu.secse.deliveryManager.registry.IRegistryProxy;
import eu.secse.deliveryManager.utils.IConfiguration;
import eu.secse.reds.core.IRedsConnector;
import eu.secse.reds.exceptions.AlreadyExistingException;
import eu.secse.reds.exceptions.NotFoundException;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Vector;

@Service(objectName = "DeliveryManager:service=secPubSubFederationProxy")
public class SecPubSubProxyMBean implements ISecPubSubProxyMBean {

    private static final Log log = LogFactory.getLog(ISecPubSubProxyMBean.class);
    public final static String REDSID = "deliverymanager-federation-securepublishsubscribe";
    @EJB(beanName = "RedsConnector", name = "RedsConnector")
    private IRedsConnector redsconnector;

    //timers
    @EJB
    IFedSecPsExpire expireBean;
    @EJB
    IFedSecPsRenew renewBean;
    @EJB
    IRegistryProxy registry;
    //config utility, it reads the config file
    @EJB
    IConfiguration conf;

//	interval between two events (renew or expire)
    private long STEP;

//	first timer event
    private long INITIAL_RENEW;
    private long INITIAL_EXPIRE;

//	renew and expire timers
    private Timer renew,  expire;
    //DeliveryManager key and certificate read form file
    private PrivateKey privateKey;
    private Certificate[] certChain;
    private Collection<X509Certificate> trustedCA;
    private String hashAlgorithm;
    private String simmetricAlgorithm;
    private String crlDistPointBaseURL;
    private int federationCertificateValidityDays;
    private int simmetricKeySize;

    public Date getNextExpire() throws IllegalStateException, NoSuchObjectLocalException, EJBException {
        return this.expire.getNextTimeout();
    }

    public Date getNextRenew() throws IllegalStateException, NoSuchObjectLocalException, EJBException {
        return this.renew.getNextTimeout();
    }
    @PersistenceContext(unitName = "deliveryManager")
    protected EntityManager em;
    private LocalDispatchingService dispatcher;

    public void start() {
        loadConfig();
        log.info("Inizializing lease and renew timers for the PubSubFederation");
        initializeTimers();
        dispatcher = null;
        // create Reds listener
        RedsSecPubSubFederationListener listener = new RedsSecPubSubFederationListener();

        try {
            redsconnector.registerDispatcher(REDSID, listener);
        } catch (AlreadyExistingException e) {
            log.error("Cannot register reds listener: already existing " + e.getMessage());
            return;
        }

        try {
            dispatcher = redsconnector.getDispatcher(REDSID);
        } catch (NotFoundException e) {
            log.error("Cannot retrieve reds dispatcher: not registered" + e.getMessage());
            return;
        }

        log.info("subscribing all federations");
        subscribeAll();

    }

    public void subscribe(ComparableFilter filter) {
        dispatcher.subscribe(filter);
    }

    public void unsubscribe(ComparableFilter filter) {
        dispatcher.unsubscribe(filter);
    }

    public LocalDispatchingService getDispatcher() {
        return dispatcher;
    }

    public void stop() {
        log.info("Stopping service pubSubFederationProxy");
        // Stopping timers
        this.renew.cancel();
        this.renew = null;

        this.expire.cancel();
        this.expire = null;

    }

    public void publish(Deliverable elem, MetaData metadata) {
        log.info("publishing" + elem.getClass().getSimpleName() + ":" + elem + " with metadata");
        dispatcher.publish(new EnvelopeWithMetadata(elem, metadata));
    }

    public void publish(Deliverable elem, Collection<MetaData> metadata) {
        log.info("publishing" + elem.getClass().getSimpleName() + ":" + elem + " with metadata");
        dispatcher.publish(new EnvelopeWithMetadata(elem, metadata));
    }

    public void publish(Deliverable elem) {
        log.info("publishing " + elem.getClass().getSimpleName() + ":" + elem);
        dispatcher.publish(new Envelope(elem));
    }

    private void initializeTimers() {
        try {
            String step = conf.getString("FederationPS.STEP");
            log.info("Read \"" + step + "\" from configuration file");
            this.STEP = Long.parseLong(step);
        } catch (Throwable e) {
            log.warn("Cannot read from the configuration file: " + e.getMessage() + "; using the default value");
            STEP = 60 * 60 * 1000; // 1 hour

        }
        log.info("interval between two events (renew or expire): " + STEP + " milliseconds");
        INITIAL_EXPIRE = STEP * 3 / 4;
        INITIAL_RENEW = STEP * 1 / 4;

        this.renew = this.renewBean.createTimer(INITIAL_RENEW, STEP);
        log.info("Renew timer started: first renew check will occur on " + this.renew.getNextTimeout());

        this.expire = this.expireBean.createTimer(INITIAL_EXPIRE, STEP);
        log.info("Expire timer started: first expiration check will occur on " + this.expire.getNextTimeout());

    }

    private void subscribeAll() {
        for (SecPubSubFederationExtraInfo intFedEnt : SecPubSubFederationExtraInfo.getAll(em)) {
            dispatcher.subscribe(intFedEnt.getFederationFilter());
        }
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return certChain[0].getPublicKey();
    }

    public X509Certificate getCertificate() {
        return (X509Certificate) certChain[0];
    }

    public String getCRLDistPointBaseURL() {
        return crlDistPointBaseURL;
    }

    public int getCertificateValidityDays() {
        return federationCertificateValidityDays;
    }

    public CertPath getCertificationPath() {
        try {
            //Generating certification path form certification chain
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            Vector<Certificate> certificates = new Vector();
            for (Certificate c : certChain) {
                certificates.add(c);
            }
            return certificateFactory.generateCertPath(certificates);
        } catch (CertificateException ex) {
            log.error("Cannot generate a X509 certification path");
        }
        return null;
    }

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public String getSimmetricAlgorithm() {
        return simmetricAlgorithm;
    }

    public Collection<X509Certificate> getTrustedCA() {
        return trustedCA;
    }

    private void loadConfig() {
        //Loading keys
        String path = conf.getString("SecPubSubProxy.keystoreLocation");
        FileInputStream file = null;
        if (path.equals("!SecPubSubProxy.keystoreLocation!")) {
            log.error("Cannot find the keystore location in config file.");
        } else {
            try {
                //Got a valid path
                file = new FileInputStream(path);
                //Getting keystore password
                String ksPassword = conf.getString("SecPubSubProxy.keystorePassword");
                if (ksPassword.equals("!SecPubSubProxy.keystorePassword!")) {
                    log.error("Cannot read the password from configuration file");
                } else {
                    KeyStore keyStore = KeyStore.getInstance("JKS");
                    keyStore.load(file, ksPassword.toCharArray());
                    file.close();
                    if (keyStore.containsAlias("deliveryManager")) {
                        //Loading data of this delivery manager
                        this.privateKey = (PrivateKey) keyStore.getKey("DeliveryManager", ksPassword.toCharArray());
                        this.certChain = keyStore.getCertificateChain("DeliveryManager");

                        //Setting the trustedCA
                        this.trustedCA = new Vector<X509Certificate>();
                        for (Enumeration<String> e = keyStore.aliases();
                                e.hasMoreElements();) {
                            String alias = e.nextElement();
                            if (!alias.equals("DeliveryManager")) {
                                this.trustedCA.add((X509Certificate) keyStore.getCertificate(alias));
                            }
                        }
                    } else {
                        log.error("The specified keysore doesn't contain the DeliveryManager keys");
                    }
                }
            } catch (UnrecoverableKeyException ex) {
                log.error("Error in reading file from keystore");
            } catch (IOException ex) {
                log.error("Cannot read the keystore.");
            } catch (NoSuchAlgorithmException ex) {
                log.error("Cannot load the keystore - No such algorithm exception");
            } catch (CertificateException ex) {
                log.error("Cannot load the keystore - Certificate exception");
            } catch (KeyStoreException ex) {
                log.error("Cannot load the keystore");
            }
        }
        //Reading cypher settings
        this.hashAlgorithm = conf.getString("SecPubSubProxy.hashAlgorithm");
        if (hashAlgorithm.equals("!SecPubSubProxy.hashAlgorithm!")) {
            log.error("Unable to find the hash algorithm to use in config file");
        }
        this.simmetricAlgorithm = conf.getString("SecPubSubProxy.simmetricAlgorithm");
        if (simmetricAlgorithm.equals("!SecPubSubProxy.simmetricAlgorithm!")) {
            log.error("Unable to find the simmetric algorithm to use in config file");
        }
        this.crlDistPointBaseURL = conf.getString("SecPubSubProxy.CRLdistribuitionPointBaseURL");
        if (crlDistPointBaseURL.equals("!SecPubSubProxy.CRLdistribuitionPointBaseURL!")) {
            log.error("Unable to find the CRL dist point in config file");
        }
        String federationCertificateValidityDaysStr = conf.getString("SecPubSubProxy.FederationCertificateValidityDays");
        if (federationCertificateValidityDaysStr.equals("!SecPubSubProxy.FederationCertificateValidityDays!")) {
            log.error("Unable to find the CRL dist point in config file");
        }
        else{
            federationCertificateValidityDays = Integer.parseInt(federationCertificateValidityDaysStr);
        }
        String simmetricKeySizeStr = conf.getString("SecPubSubProxy.simmetricKeySize");
        if (simmetricKeySizeStr.equals("!SecPubSubProxy.simmetricKeySize!")) {
            simmetricKeySize = 256;
            log.error("Unable to find the CRL dist point in config file");
        }
        else{
            simmetricKeySize = Integer.parseInt(simmetricKeySizeStr);
        }

    }

    public int getSimmetricKeySize() {
        return simmetricKeySize;
    }
}