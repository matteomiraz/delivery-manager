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
public class SecPubSubFederationRequest implements Serializable{
    private SecPubSubFederationRequestReason reason;
    private String federationId;
    
    public SecPubSubFederationRequest(String federationId, SecPubSubFederationRequestReason reason){
        this.federationId = federationId;
        this.reason = reason;
    }

    public String getFederationID(){
        return federationId;
    }
    
    public SecPubSubFederationRequestReason getReason(){
        return reason;
    }
}
