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

package eu.secse.deliveryManager.core;

import java.util.Date;
import java.util.Map;

import eu.secse.deliveryManager.data.ElementEnt;
import eu.secse.deliveryManager.data.FederatedPromotion;
import eu.secse.deliveryManager.data.FederationEnt;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.FacetAddInfo;
import eu.secse.deliveryManager.model.FacetSpec;
import eu.secse.deliveryManager.model.FacetSpecXML;

public interface FederationProxy {
	
	/**
	 * 
	 * @param serv
	 * @param service
	 */
	public void addService(FederatedPromotion prom, DService serv);
	public void addFacetSpec(FederatedPromotion prom, FacetSpec facetSpecification);
	public void addFacetAddInfo(FederatedPromotion prom, FacetAddInfo facetAddInfo);
	public void addFacetXml(FederatedPromotion prom, FacetSpecXML xml, Date dmTimestamp, String facetSchemaId);
	
//	/** TODO: change the model 
//	 * Semantics: the given element must be deleted. The collection of promotions contains all the promotions
//	 * that must be dismissed because of the 
//	 * 
//	 * @param elementoCancellato
//	 * @param prom
//	 */
//	public void delete(ElementEnt elementoCancellato, Collection<FederatedPromotion> prom );
	
	public void delete(FederatedPromotion prom,ElementEnt elementoCancellato);
	
	public void join(FederationEnt federation, Map<String,String> options);
	public void leave(FederationEnt federation);
	
	
	public Map<String,String> getFederationCreationOptions(String federationid);
	
	/** Perform any action which is needed to dismiss the federation.
	 * 
	 * @param federation
	 */
	public void dismissFederation(FederationEnt federation);
	
	
		
	public void initialize();
	public void stop();
}
