/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Support;

import eu.secse.deliveryManager.interest.InterestOnMetadata;
import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.model.MetaData;
import java.util.Collection;

/**
 *
 * @author Mario
 */
public class SimpleExpression implements Expression{
    private InterestOnMetadata metaInterest;
    
    public SimpleExpression(InterestOnMetadata metaInterest){
        this.metaInterest  = metaInterest;        
    }

    public boolean result(Deliverable deliverable, Collection<MetaData> metaData) {
        return metaInterest.matches(deliverable, metaData);
    }
    
    public InterestOnMetadata getInterestOnMetadata(){
        return metaInterest;
    }

}
