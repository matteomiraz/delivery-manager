/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.secse.deliveryManager.interest;

import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.model.DirectMessage;

/**
 *
 * @author Mario
 */
public class InterestDirectMessage implements Interest {
    private String receiverID;
    
    public InterestDirectMessage(String receiverID){
        this.receiverID  = receiverID;
    }

    public boolean isCoveredBy(Interest other) {
        if(other instanceof InterestDirectMessage){
            return ((InterestDirectMessage)other).receiverID.equals(receiverID);
        }
        return false;
    }

    public boolean matches(Deliverable msg) {
        if (msg instanceof DirectMessage){
            return ((DirectMessage) msg).getReceiverID().equals(receiverID);
        }
        return false;
    }

}
