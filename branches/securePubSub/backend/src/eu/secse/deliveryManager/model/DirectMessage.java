/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.secse.deliveryManager.model;

/**
 *
 * @author Mario
 */
public interface DirectMessage extends Deliverable{
    
    public String getReceiverID();
    public Object getMessage();

}
