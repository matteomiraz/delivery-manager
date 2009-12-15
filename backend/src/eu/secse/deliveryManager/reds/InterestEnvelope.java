/* This file is part of Delivery Manager.
 * (c) 2007 Matteo Miraz et al., Politecnico di Milano
 *
 * Delivery Manager is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation; either version 2 of the License, or 
 * (at your option) any later version.
 *
 * Delivery Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Delivery Manager; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package eu.secse.deliveryManager.reds;

import perfEval.DeleteTablesInterest;
import perfEval.FlushLogInterest;
import perfEval.JobDoneInterest;
import perfEval.WarmUpIinterest;
import polimi.reds.ComparableFilter;
import polimi.reds.Message;
import eu.secse.RedsPerformanceLogger;
import eu.secse.deliveryManager.interest.Interest;
import eu.secse.deliveryManager.model.DFederation;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.model.FacetAddInfo;

public class InterestEnvelope implements ComparableFilter {

	private static final long serialVersionUID = -3626899393365144959L;

	/** Interest wrapped */
	private Interest interest;
	
	/** Subscriber node */
	private String node;
	
	public InterestEnvelope(Interest interest, String node) {
		this.interest = interest;
		this.node = node;
	}
	
	/**
	 * Only for testing purposes!
	 * @param suffix
	 * @return
	 * @deprecated
	 */
	@Deprecated
	public InterestEnvelope getCustomNode(String suffix) {
		return new InterestEnvelope(interest, node + suffix);
	}
	
	public boolean isCoveredBy(ComparableFilter filter) {
		return filter instanceof InterestEnvelope &&
			this.interest.isCoveredBy(((InterestEnvelope)filter).interest);
	}

	public boolean matches(Message msg) {

		if(!(msg instanceof Envelope)) return false;

		Envelope envelope = (Envelope)msg;
		Deliverable deliverable = envelope.getObject();

		if(RedsPerformanceLogger.LOG_MESSAGES)
			if(envelope.newMesasge == null) {
				envelope.newMesasge = RedsPerformanceLogger.LOG_MESSAGES_OBJ;

				String elementId;
				if(deliverable instanceof DFederation) elementId = ((DFederation)deliverable).getFederationId();
				else if(deliverable instanceof DService) elementId = ((DService)deliverable).getServiceID();
				else if(deliverable instanceof FacetAddInfo) elementId = ((FacetAddInfo)deliverable).getServiceID() + "@" + ((FacetAddInfo)deliverable).getSchemaID();
				else elementId = deliverable.getClass().getCanonicalName();
				
				RedsPerformanceLogger.getSingleton().logMessages(System.currentTimeMillis(), msg.getID().toString(), deliverable.getType(), elementId );
			}

		
		long start;
		if(RedsPerformanceLogger.LOG_MATCH_TIME) start = System.nanoTime();
		
		boolean matches;
		try {
			matches = this.interest.matches(deliverable);
		} catch (Throwable e) {
			System.err.println("ERROR " + deliverable.getClass().getCanonicalName() + " / " + interest.getClass().getCanonicalName() + ": " + e);
			return false;
		}

		if((RedsPerformanceLogger.LOG_MATCH_TIME) 
			&& deliverable instanceof DService && 
			(!(interest instanceof DeleteTablesInterest || interest instanceof FlushLogInterest || interest instanceof JobDoneInterest || interest instanceof WarmUpIinterest))
//			&&  // avoiding useless loggings: filters matches predefines types of messages!
//				((deliverable instanceof DService && this.interest instanceof MultipleInterestSpecificationFacet) || 
//				 (deliverable instanceof FacetAddInfo && this.interest instanceof InterestAdditionalInformation) ||
//				 (deliverable instanceof DFederation && this.interest instanceof InterestFederation)))
			){
			long stop = System.nanoTime();
			RedsPerformanceLogger.getSingleton().logMatchTime(stop, msg.getID().toString(), deliverable.getType(), interest.getClass().getName(), node, matches, stop-start);
		}

		return matches;
	}
	
	@Override
	public String toString() {
		return "[InterestEnvelope - " + this.node + ":"  + " - " + (this.interest==null?"null":this.interest.toString()) + "]";
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((interest == null) ? 0 : interest.hashCode());
		result = PRIME * result + ((node == null) ? 0 : node.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final InterestEnvelope other = (InterestEnvelope) obj;
		if (interest == null) {
			if (other.interest != null)
				return false;
		} else if (!interest.equals(other.interest))
			return false;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		return true;
	}


}
