/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Support;

import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.model.MetaData;
import java.util.Collection;
import java.util.Vector;

/**
 *
 * @author Mario
 */
public class ANDExpression implements Expression{
    Collection<Expression> andTerms;    
    
    public ANDExpression(){
        andTerms = new Vector<Expression>();
    }
    
    public void addANDTerm(Expression e){
        andTerms.add(e);
    }

    public boolean result(Deliverable deliverable, Collection<MetaData> metaData) {
        for(Expression e : andTerms){
            if(e.result(deliverable, metaData)==false)
                return false;
        }
        return true;
    }

}
