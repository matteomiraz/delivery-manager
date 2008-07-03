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

package eu.secse.deliveryManager.federations.gossip.core;

import java.util.Collection;
import java.util.Set;

import javax.ejb.Local;

import eu.secse.deliveryManager.core.FederationProxy;
import eu.secse.deliveryManager.data.FederationEnt;
import eu.secse.deliveryManager.federations.gossip.data.DeliveryManagerGossipInfo;
import eu.secse.deliveryManager.federations.gossip.messaging.Message;

@Local
public interface IGossipProxy extends FederationProxy {
	
		/** This method is called when the delivery manager becomes isolated. 
		 *  The GossipProxy should then restart the subscription process 
		 *  
		 * @param federationid
		 */
		public void isolated(String federationid);
		
		//members from which receive information 
		public Collection<DeliveryManagerGossipInfo> getInView(FederationEnt fed);
		
		/* members to which send information in broadcast */
		public Collection<DeliveryManagerGossipInfo> getPartialView(FederationEnt fed);
		
		/* auxiliary list of the members that sent the heartbeat message */
		public Collection<DeliveryManagerGossipInfo> getLiveInView(FederationEnt fed);
		
		public DeliveryManagerGossipInfo getDM(String address, String federationid);
		
		/** Called when the delivery manager has to resubscribe because of timeout expiration 
		 * 
		 * @param federationid
		 */
		public void periodicalResubscribe(String federationid);
		
		/** This metod tries to "reset" the subscription of the delivery manager. It is called, for example,
		 * when the delivery manager becomes isolated
		 * 
		 * @param federationid
		 */
		public void isolationResubscribe(String federationid);
		
		/** This method is called when the delivery manager is restarted and federations must be rejoined.
		 * 
		 * @param federationid
		 */
		public void restartSubscribe(String federationid);
		
		/** Called if no answer has been received for the subscription before the timeout expired
		 * 
		 * @param federationid
		 */		
		public void subscriptionNotAnswered(String federationid);
		
		/** Called if our subscription is answered 
		 * 
		 */
		public void successfulSubscription(String federationid);
		
		
		/** Compute the hop counter to use in subscription forwarding, 
		 * it is equal to the estimated size of the network 
		 * 
		 * @return
		 */
		public int computeHopCounter(String federationid);
		
		/** Choose a random member from partial view.
		 * 
		 * @param exclude
		 * @return
		 */
		public String chooseRandomMember(String federationid,Set<DeliveryManagerGossipInfo> exclude);

		/** Signals that someone has arrived. 
		 * This method is only used if this delivery manager owns the federation.
		 * 
		 * @param federationid
		 */
		public void coordinatorIsNotAlone(String federationid);
		
		/** Signals that the coordinator is alone
		 * This method is only used if this delivery manager owns the federation.
		 * 
		 * @param federationid
		 */
		public void coordinatorIsAlone(String federationid);
								
		/** Broadcast a message in the federation (send to members of partial view)
		 * 
		 * @param m
		 * @param federationid
		 */
		public void broadcastMessage(Message m, String federationid);
}
