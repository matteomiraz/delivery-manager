/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.secse.deliveryManager.reds;

import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.model.MetaData;
import java.util.Collection;
import java.util.Vector;

/**
 *
 * Class that extends Envelope to provide support for message with metadata.
 * 
 * @author Mario Sangiorgio
 * @author Matteo Valoriani
 */
public class EnvelopeWithMetadata extends Envelope{
    private Collection<MetaData> metaData;
    
    /**
     * Constructor for EnvelopeWithMetadata.
     * @param deliverable Delivery Manager message to be sent on REDS
     * @param metaData Metadata for the message.
     * 
     */
    public EnvelopeWithMetadata(Deliverable deliverable,Collection<MetaData> metaData){
        super(deliverable);
        this.metaData = metaData;
    }

    public EnvelopeWithMetadata(Deliverable deliverable, MetaData metadata) {
        super(deliverable);
        this.metaData = new Vector<MetaData>();
        this.metaData.add(metadata);
    }
    
    public Collection<MetaData> getMetaData(){
        return metaData;
    }

}
