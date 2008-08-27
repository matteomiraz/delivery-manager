/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.secse.deliveryManager.interest;

import Support.SignatureVerifier;
import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.model.MetaData;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Vector;

/**
 * Class that checks if a message is authentic verifying its signature metadata.
 * @author Mario Sangiorgio
 * @author Matteo Valoriani
 */
public class InterestAuthenticMessage implements InterestOnMetadata {

    private Collection<X509Certificate> rootCertificates;
    private boolean authenticityCheckEnabled;

    /**
     * Constructor of a interest that verifies if the message is authentic.
     * @param rootCertificates A collection of trusted Certification Authority certificates.
     */
    public InterestAuthenticMessage(Collection<X509Certificate> rootCertificates) {
        this.rootCertificates = rootCertificates;
    }
    
    /**
     * Constructor of a interest that verifies if the message is authentic.
     * @param rootCertificate A trusted Certification Authority certificate.
     */
    public InterestAuthenticMessage(X509Certificate rootCertificate){
        rootCertificates = new Vector<X509Certificate>();
        rootCertificates.add(rootCertificate);
    }

    public boolean matches(Deliverable deliverable, Collection<MetaData> metaData) {
        if(authenticityCheckEnabled == false)
            return true;
        return SignatureVerifier.verifySignature(rootCertificates, deliverable, metaData);
    }
    
    public boolean isCoveredBy(InterestOnMetadata m) {
        if(!(m instanceof InterestAuthenticMessage))
            return false;
        InterestAuthenticMessage mAuth = (InterestAuthenticMessage) m;
        return mAuth.rootCertificates.containsAll(rootCertificates);
    }
    
    public void  setAuthenticCheckEnabled(boolean isEnabled){
        this.authenticityCheckEnabled = isEnabled;
    }
}
