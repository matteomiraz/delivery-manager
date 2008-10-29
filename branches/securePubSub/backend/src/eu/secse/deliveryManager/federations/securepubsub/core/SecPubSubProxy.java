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

import Certification.CertificationAuthority;
import Support.SignatureVerifier;
import eu.secse.deliveryManager.model.DFederation;
import eu.secse.deliveryManager.model.DirectMessage;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.mx.util.MBeanProxyExt;
import org.jboss.mx.util.MBeanServerLocator;

import eu.secse.deliveryManager.core.ModelManager;
import eu.secse.deliveryManager.data.ElementEnt;
import eu.secse.deliveryManager.data.FacetEnt;
import eu.secse.deliveryManager.data.FacetXmlEnt;
import eu.secse.deliveryManager.data.FederatedElement;
import eu.secse.deliveryManager.data.FederatedFacet;
import eu.secse.deliveryManager.data.FederatedPromotion;
import eu.secse.deliveryManager.data.FederatedService;
import eu.secse.deliveryManager.data.FederationEnt;
import eu.secse.deliveryManager.data.ServiceEnt;
import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.federations.securepubsub.Messages.SecPubSubFederationCertificate;
import eu.secse.deliveryManager.federations.securepubsub.Messages.SecPubSubFederationKey;
import eu.secse.deliveryManager.federations.securepubsub.Messages.SecPubSubFederationRequest;
import eu.secse.deliveryManager.federations.securepubsub.Messages.SecPubSubFederationRequestReason;
import eu.secse.deliveryManager.federations.securepubsub.data.SecPubSubFedElemExtraInfo;
import eu.secse.deliveryManager.federations.securepubsub.data.SecPubSubFederationExtraInfo;
import eu.secse.deliveryManager.federations.securepubsub.data.SecPubSubPromotionExtraInfo;
import eu.secse.deliveryManager.interest.InterestAuthenticMessage;
import eu.secse.deliveryManager.interest.InterestFederation;
import eu.secse.deliveryManager.model.DFederationEncryptedMessage;
import eu.secse.deliveryManager.model.DFederationPlainMessage;
import eu.secse.deliveryManager.model.DFederationReKey;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.model.DirectEncryptedMessage;
import eu.secse.deliveryManager.model.FacetAddInfo;
import eu.secse.deliveryManager.model.FacetSpec;
import eu.secse.deliveryManager.model.FacetSpecXML;
import eu.secse.deliveryManager.model.MetaData;
import eu.secse.deliveryManager.model.DirectPlainMessage;
import eu.secse.deliveryManager.model.MetaDataSignature;
import eu.secse.deliveryManager.reds.InterestEnvelope;
import eu.secse.deliveryManager.reds.InterestEnvelopeWithMetadata;
import eu.secse.deliveryManager.registry.IRegistryProxy;
import eu.secse.deliveryManager.timeout.ILeaseManager;
import eu.secse.deliveryManager.timeout.LeaseExtraInfo;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertPath;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.apache.axis.encoding.Base64;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;
import org.bouncycastle.asn1.x509.GeneralNames;

@Stateless
public class SecPubSubProxy implements ISecPubSubProxy {

    private static final Log log = LogFactory.getLog(ISecPubSubProxy.class);
    @PersistenceContext(unitName = "deliveryManager")
    private EntityManager em;
    @EJB
    private IRegistryProxy registry;
    @EJB
    private ILeaseManager iLease;
    @EJB
    private ModelManager modelManager;

    public void addFacetAddInfo(FederatedPromotion prom, FacetAddInfo facetAddInfo) {
        // publishes the  DFederationMessage with FacetAddInfo
        send(prom, facetAddInfo);

//		set initial renew
        SecPubSubPromotionExtraInfo promExtraInfo = new SecPubSubPromotionExtraInfo();
        promExtraInfo.setTimeout(iLease.getInitialRenew());
        prom.setExtraInfo(promExtraInfo);
        promExtraInfo.setProm(prom);
        em.persist(promExtraInfo);
        em.flush();
    }

    public void addFacetSpec(FederatedPromotion prom, FacetSpec facetSpecification) {

        DService service = modelManager.getServiceData(((ServiceEnt) prom.getElement()).getElemPK().getId());

        this.addService(prom, service);

    }

    public void addFacetXml(FederatedPromotion prom, FacetSpecXML xml, Date dmTimestamp, String facetSchemaId) {
        if (prom.getElement() instanceof FacetEnt) {
            FacetEnt facetEnt = (FacetEnt) prom.getElement();
            FacetAddInfo facetAddInfo = modelManager.getFacetAdditionalData(facetEnt.getService().getElemPK().getId(), facetSchemaId);
            this.addFacetAddInfo(prom, facetAddInfo);
        }
        if (prom.getElement() instanceof ServiceEnt) {
            ServiceEnt serviceEnt = (ServiceEnt) prom.getElement();
            FacetSpec facetSpec = modelManager.getFacetSpecificationData(serviceEnt.getElemPK().getId(), facetSchemaId);
            this.addFacetSpec(prom, facetSpec);

        }
    }

    public void addService(FederatedPromotion prom, DService serv) {

        //send service
        send(prom, serv);

        //set initial renew
        SecPubSubPromotionExtraInfo promExtraInfo = new SecPubSubPromotionExtraInfo();
        promExtraInfo.setTimeout(iLease.getInitialRenew());
        promExtraInfo.setProm(prom);
        prom.setExtraInfo(promExtraInfo);
        em.persist(promExtraInfo);
        em.flush();
    }

    public void delete(FederatedPromotion prom, ElementEnt elementoCancellato) {
        if (elementoCancellato instanceof ServiceEnt) {
            log.info("ServiceEnt " + ((ServiceEnt) elementoCancellato).getElemPK().getId() + " deleted");
        }
        if (elementoCancellato instanceof FacetEnt) {
            log.info("FacetEnt " + ((FacetEnt) elementoCancellato).getElemPK().getId() + " deleted");
        }
        if (elementoCancellato instanceof FacetXmlEnt) {
            log.info("FacetXmlEnt " + ((FacetXmlEnt) elementoCancellato).getElemPK().getId() + " deleted");
        }
    }

    public void dismissFederation(FederationEnt federation) {
        leave(federation);
    }

    public Map<String, String> getFederationCreationOptions(String federationid) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            //Getting my key from the file
            MBeanServer server = MBeanServerLocator.locate();
            ISecPubSubProxyMBean secPubSubMBean = (ISecPubSubProxyMBean) MBeanProxyExt.create(ISecPubSubProxyMBean.class, "DeliveryManager:service=secPubSubFederationProxy", server);
            PrivateKey myPrivateKey = secPubSubMBean.getPrivateKey();
            PublicKey myPublicKey = secPubSubMBean.getPublicKey();
            String hashAlgorithm = secPubSubMBean.getHashAlgorithm();

            //Generating the federation certificate
            CertificationAuthority ca = new CertificationAuthority("Federation" + federationid, "http://localhost/federationDistPoint", myPublicKey, myPrivateKey, hashAlgorithm);
            X509Certificate certificate = ca.getCertificate();
            byte[] derByteEncoded = certificate.getEncoded();
            map.put("FederationCertificate", Base64.encode(derByteEncoded));

        } catch (MalformedObjectNameException ex) {
            log.error("URL of the CRL dist point isn't valid. " + ex);
        } catch (CertificateEncodingException ex) {
            log.error("Wrong certificate encoding. " + ex);
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public void initialize() {

        // Called when booting the server. Must implement resubscription actions , listener intialization, and so on
        //Listener initialization


        // Lookup federations joined with the gossip method
        Query q = em.createNamedQuery(FederationEnt.FIND_PUBSUB);
        Collection<FederationEnt> pubsub_federations = q.getResultList();
        //Resubscribe
        for (FederationEnt fed : pubsub_federations) {
            restartSubscribe(fed.getId());
        }

    }

    public void join(FederationEnt federationEnt, Map<String, String> options) {
        log.debug("Joining federation " + federationEnt.getId());

        MBeanServer server = MBeanServerLocator.locate();
        try {
            ISecPubSubProxyMBean secPubSubMBean = (ISecPubSubProxyMBean) MBeanProxyExt.create(ISecPubSubProxyMBean.class, "DeliveryManager:service=secPubSubFederationProxy", server);

            SecPubSubFederationExtraInfo secPubSubFederationExtraInfo = new SecPubSubFederationExtraInfo();
            //Adding federation certificate
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            String base64certificate = options.get("FederationCertificate");
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.decode(base64certificate));
            X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(byteArrayInputStream);
            secPubSubFederationExtraInfo.setCertificate(certificate);

            //Subscribing to federation filter
            InterestFederation interestFederation = new InterestFederation(federationEnt.getId());
            InterestAuthenticMessage interestAuthenticMessage;
            interestAuthenticMessage = new InterestAuthenticMessage(certificate);
            InterestEnvelopeWithMetadata federationFilter;
            federationFilter = new InterestEnvelopeWithMetadata(interestFederation, interestAuthenticMessage, registry.getRegistryId());

            secPubSubMBean.subscribe(federationFilter);

            //Adding filter to FedExtraInfo
            secPubSubFederationExtraInfo.setFederationFilter(federationFilter);

            //Persisting data
            secPubSubFederationExtraInfo.setFederation(federationEnt);
            em.persist(secPubSubFederationExtraInfo);

            federationEnt.setExtraInfo(secPubSubFederationExtraInfo);

            //Sending a JOIN request to the federationEnt leader
            //Getting data of the federationEnt leader
            SecPubSubFederationExtraInfo fedExtraInfo = (SecPubSubFederationExtraInfo) federationEnt.getExtraInfo();
            SecPubSubFederationRequest joinRequest =
                    new SecPubSubFederationRequest(federationEnt.getId(), SecPubSubFederationRequestReason.JOIN);
            DirectEncryptedMessage joinMessage =
                    new DirectEncryptedMessage(fedExtraInfo.getCertificate().getPublicKey(), secPubSubMBean.getSimmetricAlgorithm(), secPubSubMBean.getSimmetricKeySize(), joinRequest);
            MetaDataSignature joinMessageSignature =
                    new MetaDataSignature(joinMessage, secPubSubMBean.getPrivateKey(),
                    secPubSubMBean.getHashAlgorithm() + "With" + secPubSubMBean.getPrivateKey().getAlgorithm(),
                    secPubSubMBean.getCertificationPath());
            secPubSubMBean.publish(joinMessage, joinMessageSignature);

        } catch (NoSuchProviderException ex) {
            log.error("No such provider exception " + ex.getMessage());
        } catch (CertificateException ex) {
            log.error("Certificate Exception - Error in reading FederationCertificate " + ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            log.error("Error in encrypting message - Can't find algorithm " + ex.getMessage());
        } catch (NoSuchPaddingException ex) {
            log.error("Error in encrypting message - No such padding " + ex.getMessage());
        } catch (InvalidKeyException ex) {
            log.error("Error in encrypting message - Invalid key " + ex.getMessage());
        } catch (IllegalBlockSizeException ex) {
            log.error("Error in encrypting message - Illegal bock size exception " + ex.getMessage());
        } catch (BadPaddingException ex) {
            log.error("Error in encrypting message - Bad padding exception " + ex.getMessage());
        } catch (MalformedObjectNameException e) {
            log.error(e.getMessage());
        }
    }

    public void leave(FederationEnt federation) {
        MBeanServer server = MBeanServerLocator.locate();
        try {
            //Unsubscribing form filter
            ISecPubSubProxyMBean pubSubMBean = (ISecPubSubProxyMBean) MBeanProxyExt.create(ISecPubSubProxyMBean.class, "DeliveryManager:service=secPubSubFederationProxy", server);
            pubSubMBean.unsubscribe(((SecPubSubFederationExtraInfo) federation.getExtraInfo()).getFederationFilter());

            //Sending a leave message to fed leader
            SecPubSubFederationExtraInfo fedExtraInfo = (SecPubSubFederationExtraInfo) federation.getExtraInfo();
            SecPubSubFederationRequest joinRequest =
                    new SecPubSubFederationRequest(federation.getId(), SecPubSubFederationRequestReason.JOIN);
            DirectEncryptedMessage leaveMessage =
                    new DirectEncryptedMessage(fedExtraInfo.getCertificate().getPublicKey(), pubSubMBean.getSimmetricAlgorithm(), pubSubMBean.getSimmetricKeySize(), joinRequest);
            MetaDataSignature leaveMessageSignature =
                    new MetaDataSignature(leaveMessage, pubSubMBean.getPrivateKey(),
                    pubSubMBean.getHashAlgorithm() + "With" + pubSubMBean.getPrivateKey().getAlgorithm(),
                    pubSubMBean.getCertificationPath());
            pubSubMBean.publish(leaveMessage, leaveMessageSignature);
        } catch (NoSuchProviderException ex) {
            log.error("Error in encrypting message - No such provider exception " + ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            log.error("Error in encrypting message - Can't find algorithm");
        } catch (NoSuchPaddingException ex) {
            log.error("Error in encrypting message - No such padding");
        } catch (InvalidKeyException ex) {
            log.error("Error in encrypting message - Invalid key");
        } catch (IllegalBlockSizeException ex) {
            log.error("Error in encrypting message - Illegal bock size exception");
        } catch (BadPaddingException ex) {
            log.error("Error in encrypting message - Bad padding exception");
        } catch (MalformedObjectNameException e) {
            log.error(e.getMessage());
        }

    }

    public void stop() {
        /* Nothing to do */
    }

    public void send(FederatedPromotion fedPromotion, FacetAddInfo facetAddInfo) {
        SecPubSubPromotionExtraInfo promExtraInfo = new SecPubSubPromotionExtraInfo();
        promExtraInfo.setTimeout(iLease.getStandardRenew());
        promExtraInfo.setProm(fedPromotion);
        fedPromotion.setExtraInfo(promExtraInfo);
        em.persist(promExtraInfo);
        em.flush();

        facetAddInfo.setInfo(iLease.getLease(fedPromotion.getElement()));

        // lookup federationEnt coordination manager and refresh
        MBeanServer server = MBeanServerLocator.locate();
        try {
            ISecPubSubProxyMBean pubSubMBean = (ISecPubSubProxyMBean) MBeanProxyExt.create(ISecPubSubProxyMBean.class, "DeliveryManager:service=secPubSubFederationProxy", server);
            //secPubSubMBean.publish(new DFederationMessage(fedPromotion.getFederation().getId(), facetAddInfo));
            DFederationEncryptedMessage message =
                    new DFederationEncryptedMessage(fedPromotion.getFederation().getId(),
                    facetAddInfo, null,//Insert metadata
                    ((SecPubSubFederationExtraInfo) fedPromotion.getFederation().getExtraInfo()).getLastKey(),
                    ((SecPubSubFederationExtraInfo) fedPromotion.getFederation().getExtraInfo()).getLastKeyVersion());
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(SecPubSubProxy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(SecPubSubProxy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(SecPubSubProxy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(SecPubSubProxy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(SecPubSubProxy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedObjectNameException e) {
            log.error(e.getMessage());
        }

    }

    public void send(FederatedPromotion fedPromotion, DService dService) {

        //set standard renew
        SecPubSubPromotionExtraInfo promExtraInfo = new SecPubSubPromotionExtraInfo();
        promExtraInfo.setTimeout(iLease.getStandardRenew());
        promExtraInfo.setProm(fedPromotion);
        fedPromotion.setExtraInfo(promExtraInfo);
        em.persist(promExtraInfo);
        em.flush();

        dService.setInfo(iLease.getLease(fedPromotion.getElement()));

//		lookup federationEnt coordination manager and refresh
        MBeanServer server = MBeanServerLocator.locate();

        try {
            ISecPubSubProxyMBean secPubSubMBean = (ISecPubSubProxyMBean) MBeanProxyExt.create(ISecPubSubProxyMBean.class, "DeliveryManager:service=secPubSubFederationProxy", server);
            //Getting federation ent
            FederationEnt federationEnt = fedPromotion.getFederation();
            //Getting last federation key
            Key federationKey = ((SecPubSubFederationExtraInfo) federationEnt.getExtraInfo()).getLastKey();
            long federationKeyVersion = ((SecPubSubFederationExtraInfo) federationEnt.getExtraInfo()).getLastKeyVersion();
            //Getting private key and other settings
            PrivateKey privateKey = secPubSubMBean.getPrivateKey();
            String signatureAlgorithm = secPubSubMBean.getHashAlgorithm() + "With" + privateKey.getAlgorithm();
            CertPath certificationPath = secPubSubMBean.getCertificationPath();
            //Generating the message
            DFederationPlainMessage plainMessage = new DFederationPlainMessage(fedPromotion.getFederation().getId(), dService);
            DFederationEncryptedMessage encryptedMessage = plainMessage.encrypt(federationKey, federationKeyVersion);
            //Generating metadata
            MetaDataSignature signature = new MetaDataSignature(encryptedMessage, privateKey, signatureAlgorithm, certificationPath);
            secPubSubMBean.publish(encryptedMessage, signature);
            if (fedPromotion.isShareAll()) {
//				add additional facets
                log.info("Sending Additional Facet for service " + dService.getServiceID());
                Collection<FacetAddInfo> facetAddInfo = modelManager.getFacetAdditionalInfo(dService.getServiceID());
                if (facetAddInfo != null) {
                    for (FacetAddInfo s : facetAddInfo) {
                        s.setInfo(iLease.getLease(fedPromotion.getElement()));
                        DFederationEncryptedMessage message =
                                new DFederationEncryptedMessage(fedPromotion.getFederation().getId(),
                                dService, null,//Insert metadata
                                ((SecPubSubFederationExtraInfo) fedPromotion.getFederation().getExtraInfo()).getLastKey(),
                                ((SecPubSubFederationExtraInfo) fedPromotion.getFederation().getExtraInfo()).getLastKeyVersion());
                    }
                }
            }
        } catch (NoSuchAlgorithmException ex) {
            log.error("The chosen algorithm cannot be found. " + ex);
        } catch (NoSuchPaddingException ex) {
            log.error(ex);
        } catch (InvalidKeyException ex) {
            log.error("Invalid key. " + ex);
        } catch (IllegalBlockSizeException ex) {
            log.error(ex);
        } catch (BadPaddingException ex) {
            log.error(ex);
        } catch (MalformedObjectNameException e) {
            log.error(e);
        }
    }

    private void received(String federationId, DService dService, Collection<MetaData> metadata) {
        received(federationId, dService);
    }

    /*
     * Process the message that contains the promotion of a service without its additional facets
     * PRECONDITIOND:
     * FederationEnt exists && FederatedPromotion doesn't exist
     * 
     * (non-Javadoc)
     * 
     * @see eu.secse.deliveryManager.federations.securepubsubscribe.core.IPubSubProxy#received(java.lang.String, eu.secse.deliveryManager.model.DService)
     */
    public void received(String federationId, DService federatedService) {

        //controls if it can find FederationEnt
        FederationEnt fed = this.em.find(FederationEnt.class, federationId);
        if (fed == null) {
            log.warn("Not in the federation " + federationId);
            return;
        }

        //controls if it can't find FederatedPromotion

        if (!modelManager.isPromoted(federatedService.getServiceID(), federationId)) {
            FederatedService fedService = modelManager.add(fed, federatedService);
            addExtraInfo(fedService, (Date) federatedService.getInfo());


            Collection<FacetSpec> facetSpecs = federatedService.getSpecType();
            if (facetSpecs != null) {
                for (FacetSpec f : facetSpecs) {
                    ServiceEnt se = null;
                    try {
                        se = ServiceEnt.searchByID(em, federatedService.getServiceID());
                    } catch (NotFoundException e) {
                        log.error("ServiceEnt " + federatedService.getServiceID() + " not added");
                    }
                    FederatedFacet fedFacet = modelManager.add(fed, f, se);
                    addExtraInfo(fedFacet, (Date) federatedService.getInfo());
                }
            }

        } else {
            log.warn("The service " + federatedService.getServiceID() + " has been promoted by this FederationProxy");
        }
    }
    /*
     * Process the message that contains the promotion of a service without its additional facets
     * PRECONDITIOND:
     * FederationEnt exists && FederatedPromotion must not exist
     * 
     * (non-Javadoc)
     * 
     * @see eu.secse.deliveryManager.federations.securepubsubscribe.core.IPubSubProxy#received(java.lang.String, eu.secse.deliveryManager.model.DService)
     */

    public void received(String federationId, FacetAddInfo federatedFacetSpec) {
        //controls if it can find FederationEnt
        FederationEnt fed = this.em.find(FederationEnt.class, federationId);
        if (fed == null) {
            log.warn("Not in the federation " + federationId);
            return;
        }
        //Controls if it can find FederatedService
        FederatedService fedService = modelManager.lookup(federatedFacetSpec.getServiceID(), fed);
        if (fedService != null) {
            FederatedFacet fedFacet = modelManager.add(fed, federatedFacetSpec);
            addExtraInfo(fedFacet, (Date) federatedFacetSpec.getInfo());
        } else {
            log.warn("Cannot find FederatedService of the service " + federatedFacetSpec.getServiceID() + " in the Fedearation " + federationId);
        }

    }

    private void received(String federationId, DFederationReKey reKeyMessage) {
        //Getting DeliveryManager keys
        MBeanServer server = MBeanServerLocator.locate();
        PublicKey publicKey = null;
        PrivateKey privateKey = null;
        try {
            ISecPubSubProxyMBean pubSubMBean =
                    (ISecPubSubProxyMBean) MBeanProxyExt.create(ISecPubSubProxyMBean.class, "DeliveryManager:service=secPubSubFederationProxy", server);
            privateKey = pubSubMBean.getPrivateKey();
            publicKey = pubSubMBean.getPublicKey();
        } catch (MalformedObjectNameException e) {
            log.error(e.getMessage());
            return;
        }

        //Getting federationEnt information
        FederationEnt fed = this.em.find(FederationEnt.class, federationId);
        if (fed == null) {
            log.warn("Received a message form " + federationId + " but I'm not member of such federation");
            return;
        }

        //Adding the key to my FederationExtraInfo
        SecPubSubFederationExtraInfo fedExtraInfo = (SecPubSubFederationExtraInfo) fed.getExtraInfo();
        fedExtraInfo.addKey(em,
                reKeyMessage.getFederationKey(publicKey, privateKey),
                reKeyMessage.getKeyVersion());
    }

    private void received(String federationId, FacetAddInfo facetAddInfo, Collection<MetaData> metadata) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void restartSubscribe(String federationId) {
        InterestEnvelope federationFilter = new InterestEnvelope(new InterestFederation(federationId), registry.getRegistryId());
        //lookup federationEnt coordination manager and refresh
        MBeanServer server = MBeanServerLocator.locate();

        try {
            ISecPubSubProxyMBean pubSubMBean = (ISecPubSubProxyMBean) MBeanProxyExt.create(ISecPubSubProxyMBean.class, "DeliveryManager:service=secPubSubFederationProxy", server);
            pubSubMBean.subscribe(federationFilter);
        } catch (MalformedObjectNameException e) {
            log.error(e.getMessage());
        }

    }

    private void addExtraInfo(FederatedElement fedElem, Date lease) {
        SecPubSubFedElemExtraInfo fedElemExtraInfo = (SecPubSubFedElemExtraInfo) fedElem.getExtraInfo();
        if (fedElemExtraInfo == null) {
            fedElemExtraInfo = new SecPubSubFedElemExtraInfo();
            fedElemExtraInfo.setTimeout(lease);
            fedElem.setExtraInfo(fedElemExtraInfo);
            fedElemExtraInfo.setElem(fedElem);
            em.persist(fedElemExtraInfo);
        } else {
            if (fedElemExtraInfo.getTimeout().before(lease)) {
                fedElemExtraInfo.setTimeout(lease);
            }
        }

        LeaseExtraInfo leaseExtraInfo = (LeaseExtraInfo) fedElem.getElement().getExtraInfo().get(LeaseExtraInfo.INFO_TYPE);
        if (leaseExtraInfo == null) {
            leaseExtraInfo = new LeaseExtraInfo(fedElem.getElement(), lease);
            fedElem.getElement().getExtraInfo().put(LeaseExtraInfo.INFO_TYPE, leaseExtraInfo);
            em.persist(leaseExtraInfo);
            em.flush();
        } else {
            leaseExtraInfo.setLease(lease);
            em.flush();
        }

        em.flush();
    }

    public void removeReadingPermission(SecureFederationUser user) {
        //We have to do a rekey
        MBeanServer server = MBeanServerLocator.locate();
        try {
            ISecPubSubProxyMBean pubSubMBean =
                    (ISecPubSubProxyMBean) MBeanProxyExt.create(ISecPubSubProxyMBean.class, "DeliveryManager:service=secPubSubFederationProxy", server);

            //Generating the key
            KeyGenerator kg = KeyGenerator.getInstance(pubSubMBean.getSimmetricAlgorithm());
            Key newKey = kg.generateKey();

            //Adding key to FedExtraInfo
            FederationEnt fed = this.em.find(FederationEnt.class, user.getFederation());
            if (fed == null) {
                log.error("Can't send a reKey to " + user.getFederation() + " because I'm not member of such federation");
                return;
            }
            SecPubSubFederationExtraInfo fedExtraInfo =
                    (SecPubSubFederationExtraInfo) fed.getExtraInfo();
            long newKeyVersion = fedExtraInfo.getLastKeyVersion() + 1;
            fedExtraInfo.addKey(em, newKey, newKeyVersion);

            //Sending the reKey message
            DFederationReKey reKeyMessage = null;
            /*
            new DFederationReKey(user.getFederation(), newKey, newKeyVersion,
            SecureFederationUser.getAll(em, user.getFederation(), true, true, true, null, null, false)
             */
            MetaDataSignature signature =
                    new MetaDataSignature(reKeyMessage, pubSubMBean.getPrivateKey(),
                    pubSubMBean.getHashAlgorithm() + "With" + pubSubMBean.getPrivateKey().getAlgorithm(),
                    pubSubMBean.getCertificationPath());

            pubSubMBean.publish(reKeyMessage, signature);
        } catch (InvalidKeyException ex) {
            log.error("Signing the message - Invalid private key");
        } catch (NoSuchAlgorithmException ex) {
            log.error("Generating a new federation key - No such algorithm");
        } catch (MalformedObjectNameException e) {
            log.error(e.getMessage());
        }
    }

    public void removeWritingPermission(SecureFederationUser user) {
    }

    public void allowWritingPermission(SecureFederationUser user) {
        MBeanServer server = MBeanServerLocator.locate();
        try {
            ISecPubSubProxyMBean pubSubMBean =
                    (ISecPubSubProxyMBean) MBeanProxyExt.create(ISecPubSubProxyMBean.class, "DeliveryManager:service=secPubSubFederationProxy", server);

            //Getting private key and certificate of the federationLeader
            PrivateKey privateKey = pubSubMBean.getPrivateKey();
            //Getting federationEnt information
            FederationEnt fed = this.em.find(FederationEnt.class, user.getFederation());
            if (fed == null) {
                log.warn("Not in the federation " + user.getFederation());
                return;
            }
            SecPubSubFederationExtraInfo fedExtraInfo = (SecPubSubFederationExtraInfo) fed.getExtraInfo();
            X509Certificate fedLeaderCertificate = fedExtraInfo.getCertificate();

            //Generating the Federation certificate for the user
            //Setting up the generator
            X509V3CertificateGenerator generator = new X509V3CertificateGenerator();
            generator.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
            generator.setIssuerDN(fedLeaderCertificate.getSubjectX500Principal());
            Calendar date = Calendar.getInstance();
            generator.setNotBefore(date.getTime());
            date.add(Calendar.DAY_OF_YEAR, pubSubMBean.getCertificateValidityDays());
            generator.setNotAfter(date.getTime());
            generator.setSubjectDN(user.getCertificate().getSubjectX500Principal());
            generator.setPublicKey(user.getUserKey());
            generator.setSignatureAlgorithm(pubSubMBean.getHashAlgorithm() + "With" + privateKey.getAlgorithm());

            try {
                generator.addExtension(X509Extensions.AuthorityKeyIdentifier, false, new AuthorityKeyIdentifierStructure(fedLeaderCertificate));
                generator.addExtension(X509Extensions.SubjectKeyIdentifier, false, new SubjectKeyIdentifierStructure(user.getUserKey()));
            } catch (CertificateParsingException ex) {
                log.error("Error in generating Federation Certificate");
            }

            //CRL Dist Point
            GeneralName gn = new GeneralName(GeneralName.uniformResourceIdentifier,
                    new DERIA5String(pubSubMBean.getCRLDistPointBaseURL() + user.getFederation() + ".crl"));
            GeneralNames gns = new GeneralNames(new DERSequence(gn));
            DistributionPointName dpn = new DistributionPointName(0, gns);
            DistributionPoint distp = new DistributionPoint(dpn, null, null);
            generator.addExtension(X509Extensions.CRLDistributionPoints, false, new DERSequence(distp));

            //Generating the certificate
            X509Certificate certificate = null;
            try {
                certificate = generator.generate(privateKey, "BC");
            } catch (CertificateEncodingException ex) {
                log.error("Error in generating certificate - Certificate encoding");
                return;
            } catch (IllegalStateException ex) {
                log.error("Error in generating certificate - Illegal state");
                return;
            } catch (NoSuchProviderException ex) {
                log.error("Error in generating certificate - Missing provider, check to have installed BouncyCastle");
                return;
            } catch (NoSuchAlgorithmException ex) {
                log.error("Error in generating certificate - Unsupported algorithm");
                return;
            } catch (SignatureException ex) {
                log.error("Error in generating certificate - Signature exception");
                return;
            } catch (InvalidKeyException ex) {
                log.error("Error in generating certificate - Invalid Key");
                return;
            }

            //Generating a direct message with the certificate
            SecPubSubFederationCertificate fedCertificateMsg =
                    new SecPubSubFederationCertificate(user.getFederation(), certificate);
            try {
                DirectEncryptedMessage message = new DirectEncryptedMessage(user.getUserKey(), pubSubMBean.getSimmetricAlgorithm(), pubSubMBean.getSimmetricKeySize(), fedCertificateMsg);
                MetaDataSignature signature = new MetaDataSignature(message, privateKey,
                        pubSubMBean.getHashAlgorithm() + "With" + privateKey.getAlgorithm(),
                        pubSubMBean.getCertificationPath());

                pubSubMBean.publish(message, signature);
            } catch (NoSuchProviderException ex) {
                log.error("No such provider exception " + ex.getMessage());
            } catch (NoSuchAlgorithmException ex) {
                log.error("Error in cipher - No such algorithm");
            } catch (NoSuchPaddingException ex) {
                log.error("Error in cipher - Padding");
            } catch (InvalidKeyException ex) {
                log.error("Error in cipher - Invalid key");
            } catch (IllegalBlockSizeException ex) {
                log.error("Error in cipher - Illegal block size");
            } catch (BadPaddingException ex) {
                log.error("Error in cipher - Bad padding");
            }

        } catch (MalformedObjectNameException e) {
            log.error(e.getMessage());
        }
    }

    public void allowReadingPermission(SecureFederationUser user) {
        //Sending the message with the federationEnt key to the user

        //Getting federationEnt extra info
        FederationEnt fed = this.em.find(FederationEnt.class, user.getFederation());
        if (fed == null) {
            log.warn("Can't allow the user to the federation because I'm not in the federation");
            return;
        }
        SecPubSubFederationExtraInfo fedExtraInfo = (SecPubSubFederationExtraInfo) fed.getExtraInfo();

        MBeanServer server = MBeanServerLocator.locate();
        try {
            ISecPubSubProxyMBean secPubSubMBean =
                    (ISecPubSubProxyMBean) MBeanProxyExt.create(ISecPubSubProxyMBean.class, "DeliveryManager:service=secPubSubFederationProxy", server);

            //Getting my keys and certification path
            PrivateKey privateKey = secPubSubMBean.getPrivateKey();
            CertPath certificationPath = secPubSubMBean.getCertificationPath();
            String hashAlgorithm = secPubSubMBean.getHashAlgorithm();

            //If I don't have any federation key

            if (fedExtraInfo.getLastKeyVersion() == 0) {
                //Generating the first federation key
                String simmetricAlgorithm = secPubSubMBean.getSimmetricAlgorithm();
                int simmetricKeySize = secPubSubMBean.getSimmetricKeySize();

                KeyGenerator keyGenerator = KeyGenerator.getInstance(simmetricAlgorithm);
                keyGenerator.init(simmetricKeySize);
                SecretKey simmetricKey = keyGenerator.generateKey();

                fedExtraInfo.addKey(em, simmetricKey, fedExtraInfo.getLastKeyVersion() + 1);
                em.persist(fedExtraInfo);
            }
            //Preparing and signing the message
            SecPubSubFederationKey fedKey = new SecPubSubFederationKey(user.getFederation(), fedExtraInfo.getLastKey(), fedExtraInfo.getLastKeyVersion());
            DirectEncryptedMessage federationKeyMessage =
                    new DirectEncryptedMessage(user.getCertificate().getPublicKey(), secPubSubMBean.getSimmetricAlgorithm(), secPubSubMBean.getSimmetricKeySize(), fedKey);
            MetaDataSignature signature =
                    new MetaDataSignature(federationKeyMessage, privateKey, hashAlgorithm + "With" + privateKey.getAlgorithm(), certificationPath);

            //Publishing message
            secPubSubMBean.publish(federationKeyMessage, signature);
        } catch (NoSuchProviderException ex) {
            log.error("No such provider exception " + ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            log.error("Error in cipher - Missing algorithm");
            return;
        } catch (NoSuchPaddingException ex) {
            log.error("Error in cipher - No such padding");
            return;
        } catch (InvalidKeyException ex) {
            log.error("Error in cipher - Invalid key");
            return;
        } catch (IllegalBlockSizeException ex) {
            log.error("Error in cipher - IllegalBlockSize");
            return;
        } catch (BadPaddingException ex) {
            log.error("Error in cipher - Bad padding");
            return;
        } catch (MalformedObjectNameException e) {
            log.error(e.getMessage());
            return;
        }
    }

    public void received(String federationId, DFederation federationMessage, Collection<MetaData> metadata) {
        //Retrivig federationEnt information
        FederationEnt fed = this.em.find(FederationEnt.class, federationId);
        if (fed == null) {
            log.warn("Received a message form " + federationId + " but I'm not member of such federation");
            return;
        }
        SecPubSubFederationExtraInfo fedExtraInfo = (SecPubSubFederationExtraInfo) fed.getExtraInfo();
        X509Certificate federationCertificate = fedExtraInfo.getCertificate();

        //Checking the signature with the federationEnt leader certificate
        boolean signatureCheck = signatureCheck(federationMessage, metadata);
        if (signatureCheck) {
            log.info("Received a valid secure federation message");

            //Checking if the message is a rekey
            if (federationMessage instanceof DFederationReKey) {
                received(federationId, (DFederationReKey) federationMessage);
                return;
            }
            //The message isn't a rekey so it is a federationEnt data message
            DFederationPlainMessage dfed = null;
            //Getting the DFederationPlainMessage
            if (federationMessage instanceof DFederationPlainMessage) {
                dfed = (DFederationPlainMessage) federationMessage;
            } else {
                if (federationMessage instanceof DFederationEncryptedMessage) {
                    try {
                        long keyVersion = ((DFederationEncryptedMessage) federationMessage).getKeyVersion();

                        dfed = ((DFederationEncryptedMessage) federationMessage).decrypt(fedExtraInfo.getKey(keyVersion));
                    } catch (InvalidKeyException ex) {
                        log.error("Cipher error - Invalid key");
                    }
                } else {
                    log.warn("Can't decrypt the received message");
                }
            }

            //Passing the plain federationEnt message to the right receiver
            if (dfed != null) {
                if (dfed.getObject() instanceof DService) {
                    received(dfed.getFederationId(), (DService) dfed.getObject(), dfed.getMetadata());
                } else if (dfed.getObject() instanceof FacetAddInfo) {
                    received(dfed.getFederationId(), (FacetAddInfo) dfed.getObject(), dfed.getMetadata());
                } else {
                    log.warn("Warning: cannot process type " + dfed.getObject().getClass().getCanonicalName() + " received from the federation " + dfed.getFederationId());
                }
            }
        } else {
            log.warn("Received message wasn't signed correctly");
        }
    }

    private boolean signatureCheck(Deliverable message, Collection<MetaData> metadata) {
        try {
            MBeanServer server = MBeanServerLocator.locate();
            ISecPubSubProxyMBean secPubSubProxyMBean = (ISecPubSubProxyMBean) MBeanProxyExt.create(ISecPubSubProxyMBean.class, "DeliveryManager:service=secPubSubFederationProxy", server);
            Collection<X509Certificate> trustedCA = secPubSubProxyMBean.getTrustedCA();
            if (trustedCA.isEmpty()) {
                log.error("TrustedCA list is empty. Please check the keystore. This message will be processed without signature check");
                return true;
            } else {
                return SignatureVerifier.verifySignature(trustedCA, message, metadata);
            }
        } catch (MalformedObjectNameException ex) {
            Logger.getLogger(SecPubSubProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void received(DirectMessage directMessage, Collection<MetaData> metadata) {
        //Getting the TrustedCA collection
        MBeanServer server = MBeanServerLocator.locate();
        PrivateKey privateKey = null;
        PublicKey publicKey = null;
        ISecPubSubProxyMBean secPubSubMBean = null;
        try {
            secPubSubMBean =
                    (ISecPubSubProxyMBean) MBeanProxyExt.create(ISecPubSubProxyMBean.class, "DeliveryManager:service=secPubSubFederationProxy", server);
            privateKey = secPubSubMBean.getPrivateKey();
            publicKey = secPubSubMBean.getPublicKey();
        } catch (MalformedObjectNameException e) {
            log.error(e.getMessage());
        }

        //Checking the signature
        boolean signatureCheck = signatureCheck(directMessage, metadata);
        if (signatureCheck) {
            //Getting the control message from the direct message
            DirectPlainMessage message = null;
            if (directMessage instanceof DirectPlainMessage) {
                message = (DirectPlainMessage) directMessage;
            } else {
                if (directMessage instanceof DirectEncryptedMessage) {
                    try {
                        message = ((DirectEncryptedMessage) directMessage).decrypt(privateKey);
                    } catch (InvalidKeyException ex) {
                        log.error("Cipher error - Invalid key");
                    }
                } else {
                    log.warn("Unknow type of direct message");
                }
            }

            //Passing direct message to the appropriate 
            if (message.getMessage() instanceof SecPubSubFederationRequest) {
                SecPubSubFederationRequest request = (SecPubSubFederationRequest) message.getMessage();

                //Looking for the certificate in the metadata signature
                X509Certificate userCertificate = null;
                for (MetaData m : metadata) {
                    if (m instanceof MetaDataSignature) {
                        userCertificate =
                                (X509Certificate) ((MetaDataSignature) m).getCertPath().getCertificates().get(0);
                        break;
                    }
                }

                //Checking if I'm part of the federationEnt
                FederationEnt fed = em.find(FederationEnt.class, request.getFederationID());
                if (fed == null) {
                    log.warn("Not in the federation " + request.getFederationID());
                    return;
                }

                switch (request.getReason()) {
                    case JOIN:
                        receivedReadRequest(request.getFederationID(),
                                userCertificate.getPublicKey(),
                                userCertificate.getSubjectDN().getName(),
                                userCertificate);
                        break;
                    case LEAVE:
                        discardReadPermissions(request.getFederationID(),
                                userCertificate.getPublicKey());
                        break;
                    case WRITE:
                        receivedWriteRequest(request.getFederationID(),
                                userCertificate.getPublicKey(),
                                userCertificate.getSubjectDN().getName(),
                                userCertificate);
                        break;
                    case NOT_WRITE:
                        discardWritePermissions(request.getFederationID(),
                                userCertificate.getPublicKey());
                        break;

                }
            }
            if (message.getMessage() instanceof DFederationReKey) {
                //I got a new key for the federation
                DFederationReKey reKeyMessage = (DFederationReKey) message.getMessage();
                FederationEnt fed = this.em.find(FederationEnt.class, reKeyMessage.federationId);
                if (fed == null) {
                    log.warn("Received a message form " + reKeyMessage.federationId + " but I'm not member of such federation");
                    return;
                }
                //Adding key information to federation extra info
                SecPubSubFederationExtraInfo extraInfo = (SecPubSubFederationExtraInfo) fed.getExtraInfo();
                extraInfo.addKey(em, reKeyMessage.getFederationKey(publicKey, privateKey), reKeyMessage.getKeyVersion());
                return;
            }
            if (message.getMessage() instanceof SecPubSubFederationKey) {
                //I got a new key for the federation
                SecPubSubFederationKey keyMessage = (SecPubSubFederationKey) message.getMessage();
                FederationEnt fed = this.em.find(FederationEnt.class, keyMessage.getFederationId());
                if (fed == null) {
                    log.warn("Received a message form " + keyMessage.getFederationId() + " but I'm not member of such federation");
                    return;
                }
                //Adding key information to federation extra info
                SecPubSubFederationExtraInfo extraInfo = (SecPubSubFederationExtraInfo) fed.getExtraInfo();
                extraInfo.addKey(em, keyMessage.getKey(), keyMessage.getKeyVersion());
                return;
            }
        }
    }

    public void receivedReadRequest(String federationId, PublicKey userKey, String name, X509Certificate certificate) {
        SecureFederationUser user = SecureFederationUser.findUser(em, federationId, userKey);
        if (user != null) {
            if (user.isBanned()) {
                return;
            }
            if (user.isCanRead()) {
                allowReadingPermission(user);
                return;
            }
        } else {
            user = new SecureFederationUser(name, federationId, userKey, certificate);
            em.persist(user);
        }

        user.setWantsRead(true);
    }

    /** The user doesn't wants to receive the federationEnt's messages anymore... notice that is the user that wants to quit (he is not banned!) */
    public void discardReadPermissions(String federationId, PublicKey userKey) {
        SecureFederationUser user = SecureFederationUser.findUser(em, federationId, userKey);
        if (user != null) {
            if (user.isCanWrite()) {
                user.setCanWrite(false);
                removeWritingPermission(user);
            }

            if (user.isCanRead()) {
                user.setCanRead(false);
                removeReadingPermission(user);
            }
        }
    }

    public void receivedWriteRequest(String federationId, PublicKey userKey, String name, X509Certificate certificate) {
        SecureFederationUser user = SecureFederationUser.findUser(em, federationId, userKey);
        if (user != null) {
            if (user.isBanned()) {
                return;
            }
            if (user.isCanWrite()) {
                allowWritingPermission(user);
                return;
            }
        } else {
            user = new SecureFederationUser(name, federationId, userKey, certificate);
            user.setWantsRead(true);
            em.persist(user);
        }

        user.setWantsWrite(true);
    }

    /** The user doesn't wants to send messages to this federationEnt... */
    public void discardWritePermissions(String federationId, PublicKey userKey) {
        SecureFederationUser user = SecureFederationUser.findUser(em, federationId, userKey);
        if (user != null) {
            if (user.isCanWrite()) {
                user.setCanWrite(false);
                removeWritingPermission(user);
            }
        }
    }

    public void discardFederation(String federationId) {
        Collection<SecureFederationUser> users = SecureFederationUser.getAll(em, federationId, null, null, null, null, null, null);

        for (SecureFederationUser u : users) {
            em.remove(u);
        }
    }
}
