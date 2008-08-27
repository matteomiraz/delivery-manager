/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.secse.deliveryManager.federations.securepubsub.Messages;

import eu.secse.deliveryManager.federations.securepubsub.data.SecPubSubKey;
import java.io.Serializable;
import java.security.Key;

/**
 *
 * @author Mario
 */
public class SecPubSubFederationKey implements Serializable{
    private String federationId;
    private Key federationKey;
    private long keyVersion;
    
    
    public SecPubSubFederationKey(String federationId, Key federationKey,long keyVersion){
        this.federationId = federationId;
        this.federationKey = federationKey;
        this.keyVersion = keyVersion;
    }
    
    public String getFederationId(){
        return federationId;
    }
    
    public Key getKey(){
        return federationKey;
    }
    
    public Long getKeyVersion(){
        return keyVersion;
    }

}
