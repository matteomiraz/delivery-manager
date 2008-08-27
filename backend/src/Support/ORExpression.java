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
public class ORExpression implements Expression{
    Collection<Expression> orTerms;    
    
    public ORExpression(){
        orTerms = new Vector<Expression>();
    }
    
    public void addANDTerm(Expression e){
        orTerms.add(e);
    }

    public boolean result(Deliverable deliverable, Collection<MetaData> metaData) {
        for(Expression e : orTerms){
            if(e.result(deliverable, metaData)==true)
                return true;
        }
        return false;
    }
}
