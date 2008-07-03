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

import java.util.Collection;
import java.util.Date;

import javax.ejb.Local;

import eu.secse.deliveryManager.data.ElementEnt;
import eu.secse.deliveryManager.data.ElementExtraInfo;
import eu.secse.deliveryManager.data.FacetEnt;
import eu.secse.deliveryManager.data.FacetXmlEnt;
import eu.secse.deliveryManager.data.FederatedFacet;
import eu.secse.deliveryManager.data.FederatedPromotion;
import eu.secse.deliveryManager.data.FederatedService;
import eu.secse.deliveryManager.data.FederatedXml;
import eu.secse.deliveryManager.data.FederationEnt;
import eu.secse.deliveryManager.data.ServiceEnt;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.FacetAddInfo;
import eu.secse.deliveryManager.model.FacetSpec;
import eu.secse.deliveryManager.model.FacetSpecXML;

@Local
public interface ModelManager {

	/* simile alla "getInstance" di un singleton (se non c'e', lo crea) */
	/* Segnala la presenza di elementi, che sono già presenti nel registro locale */
	public ServiceEnt add(String serviceId);
	public FacetEnt add(String serviceId, String facetId);
	public FacetXmlEnt add(String serviceId, String facetId, String facetXmlId);
	
	/**
	 * Segnala la ricezione di un servizio (vengono ignorate le facet attaccate!) che vanno inseriti nel registro locale
	 */
	public ServiceEnt add(DService service);
	
	/**
	 * Permette di segnalare la ricezione di una facet addizionale al delivery manager. 
	 * Se la facet contiene un embeddable XML, il metodo lo gestisce correttamente.  
	 * @param facet la facet ricevuta
	 * @return la facetEnt. Se conteneva un documento XML, è impostato il collegamento a facetXmlEnt
	 */
	public FacetEnt add(FacetAddInfo facet);
	
	/**
	 * Permette di segnalare la ricezione di una facet di specifica al delivery manager. 
	 * Se la facet contiene un embeddable XML, il metodo lo gestisce correttamente.  
	 * @param facet la facet ricevuta
	 * @return la facetEnt. Se conteneva un documento XML, è impostato il collegamento a facetXmlEnt
	 */
	public FacetEnt add(FacetSpec facet, ServiceEnt service);	
	
	public FacetXmlEnt add(FacetSpecXML xml, FacetEnt facet, String facetType, Date facetTimestamp);

	/* Segnala la ricezione da una federazione di elementi che vanno inseriti nel registro locale */
	public FederatedService add(FederationEnt federation, DService service);
	public FederatedFacet add(FederationEnt federation, FacetAddInfo facet);
	public FederatedFacet add(FederationEnt federation, FacetSpec facet, ServiceEnt service);
	public FederatedXml add(FederationEnt federation, FacetSpecXML xml, FacetEnt facet, String facetType, Date facetTimestamp);
	
	/* Segnala la presenza di un elemento all'interno della federazione (senza averne ricevuto i dettagli, ad es. nel gossip) 
	 * NOTA: l'elemento deve essere gia' presente nel registro */
	public FederatedService add(FederationEnt federation, String serviceid);
	public FederatedFacet add(FederationEnt federation, String serviceid, String facetId, boolean addInfo);
	public FederatedXml add(FederationEnt federation, String serviceid, String facetId, String facetXmlId);
	
	
	/* Dato l'id di un elemento permette di recuperare l'entità corrispondente. Se l'ent non c'e', restituisce null  */
	public ServiceEnt lookup(String serviceId);
	public FacetEnt lookup(String serviceId, String facetId);
	public FacetXmlEnt lookup(String serviceId, String facetId, String facetXmlId);

	/* Dato l'id di un elemento e una federazione permette di recuperare il federatedElement corrispondente 
	 * (se non c'e', restituisce null) */
	public FederatedService lookup(String serviceId, FederationEnt federation);
	public FederatedFacet lookup(String serviceId, String facetId, FederationEnt federation);
	public FederatedXml lookup(String serviceId, String facetId, String facetXmlId, FederationEnt federation);
	
	public FederatedPromotion lookup(ElementEnt element, FederationEnt federation);
	
	/* Metodi aggiunti da Antonio - non c'era nulla per ottenere il federated service al momento della promozione 
	 * Se non c'e', lo crea (serve a centralizzare la creazione) */
	/** Returns the existing FederatedService or creates a new one */
	public FederatedService retrieve(String serviceId, FederationEnt federation, boolean received);
	/** Returns the existing FederatedFacet or creates a new one */
	public FederatedFacet retrieve(String serviceId, String facetId, FederationEnt federation, boolean received);
	/** Returns the existing FederatedXml or creates a new one */
	public FederatedXml retrieve(String serviceId, String facetId, String facetXmlId, FederationEnt federation);
	
	public void deleteFromPlugin(ElementEnt serviceEnt);
	
	public void deleteFromUpdate(ServiceEnt serviceEnt);
	public void deleteFromUpdate(FacetEnt facetEnt);
	public void deleteFromUpdate(FacetXmlEnt facetXmlEnt);
	
	/* Permette ad un handler di segnalare che per lui un elemento può essere rimosso. 
	 * Prima di invocare questi metodi bisogna rimuovere le extraInfo create dall'handler. */
	public void delete(ServiceEnt service);
	public void delete(FacetEnt facet);
	public void delete(FacetXmlEnt facet);	

	/* Permette ad un handler di una federazione di segnalare che per lui un elemento può essere rimosso. 
	 * Dopo l'invocazione di questo metodo, il federatedElement verrà rimosso dal database */
	public void delete(FederatedService fedService);
	public void delete(FederatedFacet fedFacet);
	public void delete(FederatedXml fedXml);
	
	public boolean isPromoted(String serviceid,String federationid);
	public boolean isPromotedWithoutFacets(String serviceid,String federationid);	
	public boolean isPromotedWithFacets(String serviceid,String federationid);
	
	/** Returns true iff the facet has been promoted by this delivery manager in the specified Federation  */
	public boolean isFacetPromoted(String serviceid,String facetid, boolean addInfo, FederationEnt fed);	
	
	/** Restituisce null se il servizio non e' nel registro  */
	public DService getServiceData(String serviceid);
	
	public FacetSpec getFacetSpecificationData(String serviceid, String facetschemaid);
	public FacetAddInfo getFacetAdditionalData(String serviceid, String facetschemaid);
	public Collection<FacetAddInfo> getFacetAdditionalInfo(String serviceid);
	public FacetSpecXML getFacetXmlData(String serviceid, String facetschemaid);
	
	public void postInsert(FacetEnt facet);
	public void postInsert(FacetXmlEnt xml);

	public GenericProxy getGenericProxy(ElementExtraInfo e);
}