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
package eu.secse.deliveryManager.federations.securepubsub.core;

import eu.secse.deliveryManager.model.DFederation;
import eu.secse.deliveryManager.model.DirectMessage;
import eu.secse.deliveryManager.model.MetaData;
import java.util.Collection;
import javax.ejb.Local;

import eu.secse.deliveryManager.core.FederationProxy;
import eu.secse.deliveryManager.data.FederatedPromotion;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.FacetAddInfo;

@Local
public interface ISecPubSubProxy extends FederationProxy {

    public void received(DirectMessage directMessage, Collection<MetaData> metadata);

    public void received(String federationId, DFederation dfed, Collection<MetaData> metadata);

    public void send(FederatedPromotion fedPromotion, DService dService);

    public void send(FederatedPromotion fedPromotion, FacetAddInfo dFacet);

    public void received(String federationId, DService federatedService);

    public void received(String federationId, FacetAddInfo federatedFacetSpec);

    /** This user is joined the federation. Send him the information he needs. */
    public void allowReadingPermission(SecureFederationUser user);

    /** This user now can send messages in the federation. Send him the information he needs. */
    public void allowWritingPermission(SecureFederationUser user);

    /** This user cannot send messages in the federation anymore. */
    public void removeWritingPermission(SecureFederationUser user);

    /** This user is no more part of the federation. Notice that he can have writing permissions  */
    public void removeReadingPermission(SecureFederationUser user);
}
