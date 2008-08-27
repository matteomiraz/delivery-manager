/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Support;

import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.model.MetaData;
import eu.secse.deliveryManager.model.MetaDataSignature;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.asn1.DERString;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.x509.extension.X509ExtensionUtil;

/**
 * Class that checks if a message is authentic verifying its signature metadata.
 * @author Mario Sangiorgio
 * @author Matteo Valoriani
 */
public class SignatureVerifier {

    private static X509CRL downloadCRL(String url) throws MalformedURLException, IOException {
        try {
            URL crlLocation = new URL(url);
            /*PROXY
            SocketAddress proxyAddress = new InetSocketAddress("proxy.polimi.it",8080);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, proxyAddress);
            URLConnection connection = url.openConnection(proxy);
             */
            URLConnection connection = crlLocation.openConnection();
            InputStream in = connection.getInputStream();
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509CRL crl = (X509CRL) cf.generateCRL(in);
            return crl;
        } catch (CRLException ex) {
            Logger.getLogger(SignatureVerifier.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(SignatureVerifier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static CertStore getCRL(X509Certificate certificate) {
        try {
            List<CRL> crls = new Vector<CRL>();
            try {
                //Getting DistPoint from certificate and adding to crls
                byte[] extension = certificate.getExtensionValue(X509Extensions.CRLDistributionPoints.getId());
                CRLDistPoint crlDistPoint = null;
                crlDistPoint = CRLDistPoint.getInstance(X509ExtensionUtil.fromExtensionValue(extension));
                DistributionPoint[] points = crlDistPoint.getDistributionPoints();

                for (DistributionPoint dp : points) {
                    DistributionPointName dpn = dp.getDistributionPoint();
                    GeneralNames gns = (GeneralNames) dp.getDistributionPoint().getName();
                    GeneralName[] vgn = gns.getNames();
                    for (int j = 0; j < vgn.length; j++) {
                        if (vgn[j].getTagNo() == GeneralName.uniformResourceIdentifier) {
                            DERString s = (DERString) vgn[j].getName();
                            crls.add(downloadCRL(s.getString()));
                        }
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(SignatureVerifier.class.getName()).log(Level.SEVERE, null, ex);
            }
            CollectionCertStoreParameters certStoreParams = new CollectionCertStoreParameters(crls);
            return CertStore.getInstance("Collection", certStoreParams);
        } catch (InvalidAlgorithmParameterException ex) {
            Logger.getLogger(SignatureVerifier.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(SignatureVerifier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static boolean verifySignature(X509Certificate rootCertificate,
            Deliverable deliverable, Collection<MetaData> metaData){
        Vector<X509Certificate> rootCertificates = new Vector<X509Certificate>();
        rootCertificates.add(rootCertificate);
        return verifySignature(rootCertificates,
                deliverable, metaData);
    }
    public static boolean verifySignature(Collection<X509Certificate> rootCertificates,
            Deliverable deliverable, Collection<MetaData> metaData) {
        if (metaData == null) {
            return false;
        }
        Collection<MetaDataSignature> signatures = new Vector<MetaDataSignature>();
        for (MetaData m : metaData) {
            if (m instanceof MetaDataSignature) {
                signatures.add((MetaDataSignature) m);
            }
        }
        //Verifying the presence of signatures
        if (signatures.isEmpty()) {
            return false;
        }
        for (MetaDataSignature s : signatures) {
            //Verifying the certificate
            CertPathValidator certPathValidator = null;
            try {
                certPathValidator = CertPathValidator.getInstance("PKIX", "BC");
            } catch (NoSuchProviderException ex) {
                Logger.getLogger(SignatureVerifier.class.getName()).log(Level.SEVERE, null, ex);
                return true;
            /* If there are configuration problems the choice is to match
             * the message to avoid message loss.
             */
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(SignatureVerifier.class.getName()).log(Level.SEVERE, null, ex);
                return true;
            /* If there are configuration problems the choice is to match
             * the message to avoid message loss.
             */
            }
            try {
                //Validation parameters
                PKIXParameters params = null;
                try {
                    Set<TrustAnchor> trustedCA = new HashSet<TrustAnchor>();
                    for( X509Certificate c : rootCertificates){
                        trustedCA.add(new TrustAnchor(c, null));
                    }
                    params = new PKIXParameters(trustedCA);
                } catch (InvalidAlgorithmParameterException ex) {
                    Logger.getLogger(SignatureVerifier.class.getName()).log(Level.SEVERE, null, ex);
                }
                params.setDate(Calendar.getInstance().getTime());
                CertPath certPath = s.getCertPath();
                for (X509Certificate c : (List<X509Certificate>) certPath.getCertificates()) {
                    params.addCertStore(getCRL(c));
                }
                
                CertPathValidatorResult validationResult = certPathValidator.validate(certPath, params);
                //Certificate is valid
                //Computing digest of the deliverable message bytes and verifying the signature
                Signature signatureProvider = null;
                try {
                    signatureProvider = Signature.getInstance(s.getSignatureAlgorithm(), "BC");
                    signatureProvider.initVerify(s.getCertPath().getCertificates().get(0));
                    signatureProvider.update(Support.objectToBytes(deliverable));
                    if (signatureProvider.verify(s.getSignature())) {
                        return true;
                    }
                } catch (NoSuchProviderException ex) {
                    Logger.getLogger(SignatureVerifier.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SignatureException ex) {
                    Logger.getLogger(SignatureVerifier.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidKeyException ex) {
                    Logger.getLogger(SignatureVerifier.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(SignatureVerifier.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (CertPathValidatorException ex) {
                Logger.getLogger(SignatureVerifier.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidAlgorithmParameterException ex) {
                Logger.getLogger(SignatureVerifier.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
}

