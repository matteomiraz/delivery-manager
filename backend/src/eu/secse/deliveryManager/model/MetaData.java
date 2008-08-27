/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.secse.deliveryManager.model;

import java.io.Serializable;

/**
 * Interface that represents a generic metadata.
 * @author Mario
 */
public interface MetaData extends Serializable{
    
    String getType();
    
}
