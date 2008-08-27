/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.secse.deliveryManager.federations.securepubsub.Messages;

import java.io.Serializable;

/**
 *
 * @author Mario
 */
public enum SecPubSubFederationRequestReason implements Serializable{
    JOIN,LEAVE,WRITE,NOT_WRITE
}
