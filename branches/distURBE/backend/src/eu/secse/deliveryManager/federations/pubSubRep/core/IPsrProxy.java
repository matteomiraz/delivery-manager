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

package eu.secse.deliveryManager.federations.pubSubRep.core;

import javax.ejb.Local;

import polimi.reds.MessageID;
import eu.secse.deliveryManager.core.FederationProxy;
import eu.secse.deliveryManager.data.FederatedPromotion;
import eu.secse.deliveryManager.federations.pubSubRep.reds.PSRDeletion;
import eu.secse.deliveryManager.federations.pubSubRep.reds.PSRResponse;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.FacetAddInfo;

@Local
public interface IPsrProxy extends FederationProxy{

	public void send(FederatedPromotion fedPromotion, DService dService);
	
	public void received(String federationId, DService federatedService);
	public void received(String federationId, FacetAddInfo federatedFacetSpec);

	public void receivedDeletion(String federationId, PSRDeletion deliverable);
	
	public void receivedQuery(String federationId, MessageID messageID);
	public void receivedResponse(String federationId, PSRResponse response);
}
