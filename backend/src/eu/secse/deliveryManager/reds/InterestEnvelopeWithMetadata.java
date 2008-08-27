/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.secse.deliveryManager.reds;

import Support.Expression;
import Support.SimpleExpression;
import eu.secse.RedsPerformanceLogger;
import eu.secse.deliveryManager.interest.Interest;
import eu.secse.deliveryManager.interest.InterestAdditionalInformation;
import eu.secse.deliveryManager.interest.InterestFederation;
import eu.secse.deliveryManager.interest.InterestOnMetadata;
import eu.secse.deliveryManager.interest.InterestService;
import eu.secse.deliveryManager.interest.MultipleInterestSpecificationFacet;
import eu.secse.deliveryManager.model.DFederationPlainMessage;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.model.FacetAddInfo;
import eu.secse.deliveryManager.model.MetaData;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import polimi.reds.ComparableFilter;
import polimi.reds.Message;

/**
 *
 * Interest that supports match on data and metadata.
 * @author Mario Sangiorgio
 * @author Matteo Valoriani
 */
public class InterestEnvelopeWithMetadata extends InterestEnvelope {

    private Expression metaInterests;

    /*
     * Constructors that wraps a single interest on metadata in a simple expression
     * without reuqiring that the user does this operation explicitely.
     */
    public InterestEnvelopeWithMetadata(Interest interest, InterestOnMetadata interestOnMetadata, String node) {
        this(interest, new SimpleExpression(interestOnMetadata), node);
    }

    /**
     * Constructor for an InterestEnvelopeWithMetadata object.
     * @param interest Interest to match data
     * @param exp Expression that represents the logical expression for metadata
     * interests matching.
     * @param node Subscriber node.
     */
    public InterestEnvelopeWithMetadata(Interest interest,
            Expression exp, String node) {
        super(interest, node);
        this.metaInterests = exp;
    }

    /**
     * Overrides the matches method in Envelope class.
     * This method checks if the message matches with the interest provided.
     * @param msg The message to be checked.
     * @return True if the message matches, false elsewhere.
     */
    @Override
    public boolean matches(Message msg) {
        //Getting data and metadata
        if (!(msg instanceof EnvelopeWithMetadata)) {
            return false;
        }
        EnvelopeWithMetadata envelope = (EnvelopeWithMetadata) msg;
        Deliverable deliverable = envelope.getObject();//Data

        Collection<MetaData> metaData = envelope.getMetaData();

        //LOGGING
        if (RedsPerformanceLogger.LOG_MESSAGES) {
            if (envelope.newMesasge == null) {
                envelope.newMesasge = RedsPerformanceLogger.LOG_MESSAGES_OBJ;

                String elementId;
                if (deliverable instanceof DFederationPlainMessage) {
                    elementId = ((DFederationPlainMessage) deliverable).getFederationId();
                } else if (deliverable instanceof DService) {
                    elementId = ((DService) deliverable).getServiceID();
                } else if (deliverable instanceof FacetAddInfo) {
                    elementId = ((FacetAddInfo) deliverable).getServiceID() + "@" + ((FacetAddInfo) deliverable).getSchemaID();
                } else {
                    elementId = deliverable.getClass().getCanonicalName();
                }
                RedsPerformanceLogger.getSingleton().logMessages(System.currentTimeMillis(), msg.getID().toString(), deliverable.getType(), elementId);
            }
        }
        long start;
        if (RedsPerformanceLogger.LOG_MATCH_TIME) {
            start = System.nanoTime();
            
        //Verifying metadata and then data 
        }
        boolean matches = false;
        if (metaInterests.result(deliverable, metaData)) {
            matches =  this.interest.matches(deliverable);
        }
        if((RedsPerformanceLogger.LOG_MATCH_TIME)/* && ( // avoiding useless loggings: filters matches predefines types of messages!
				(deliverable instanceof DService && this.interest instanceof MultipleInterestSpecificationFacet) || 
				(deliverable instanceof DService && this.interest instanceof InterestService) ||
                                (deliverable instanceof FacetAddInfo && this.interest instanceof InterestAdditionalInformation) ||
				(deliverable instanceof DFederationPlainMessage && this.interest instanceof InterestFederation))*/){
			long stop = System.nanoTime();
			RedsPerformanceLogger.getSingleton().logMatchTime(stop, msg.getID().toString(), deliverable.getType(), interest.getClass().getName(), matches, stop-start);
		}
	
		return matches;
    }

    @Override
    public boolean isCoveredBy(ComparableFilter filter) {
        if (filter instanceof InterestEnvelope) {
            return false;
        }
        if (filter instanceof InterestEnvelopeWithMetadata) {
            InterestEnvelopeWithMetadata f = (InterestEnvelopeWithMetadata) filter;
            if (metaInterests instanceof SimpleExpression && f.metaInterests instanceof SimpleExpression) {
                return super.isCoveredBy(filter) &&
                        ((SimpleExpression) metaInterests).getInterestOnMetadata().isCoveredBy(
                        ((SimpleExpression) f.metaInterests).getInterestOnMetadata());
            }
        }
        return false;
    }
}
