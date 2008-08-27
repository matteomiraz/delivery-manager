/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Support;

import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.model.MetaData;
import java.io.Serializable;
import java.util.Collection;

/**
 * Interface that represent a logical expression.
 * @author Mario Sangiorgio
 * @author Matteo Valoriani
 */
public interface Expression extends Serializable{
    
    /**
     * Evaluates the result of a logical expression about a metadata interest.
     * @param deliverable
     * @param metaData
     * @return
     */
    public boolean result(Deliverable deliverable,Collection<MetaData> metaData);
}
