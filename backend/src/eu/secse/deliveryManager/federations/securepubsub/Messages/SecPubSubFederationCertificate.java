/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.secse.deliveryManager.federations.securepubsub.Messages;

import java.io.Serializable;
import java.security.cert.X509Certificate;

/**
 *
 * @author Mario
 */
public class SecPubSubFederationCertificate implements Serializable{
    private String federationId;
    private X509Certificate certificate;
    
    public SecPubSubFederationCertificate(String federationId, X509Certificate certificate){
        this.federationId = federationId;
        this.certificate = certificate;
    }
    
    public String getFederationId(){
        return federationId;
    }
    
    public X509Certificate getCertificate(){
        return certificate;
    }

}
