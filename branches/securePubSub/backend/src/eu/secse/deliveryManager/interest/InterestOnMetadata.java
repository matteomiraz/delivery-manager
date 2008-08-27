/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.secse.deliveryManager.interest;

import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.model.MetaData;
import java.io.Serializable;
import java.util.Collection;

/**
 * Interface of a metadata interest.
 * @author Mario Sangiorgio
 * @author Matteo Valoriani
 */
public interface  InterestOnMetadata extends Serializable{
    
    //public boolean isCoveredBy(InterestOnMetadata metaFilter);
    
    /**
     * Method to check if metadata of a message matches with the interest.
     * @param deliverable A Delivery Manager message.
     * @param metaData Metadata of the message.
     * @return True if metadata matches with the interest, false elsewhere.
     */
    public boolean matches(Deliverable deliverable,
            Collection<MetaData> metaData);
    
    public boolean isCoveredBy(InterestOnMetadata m);
    
}
