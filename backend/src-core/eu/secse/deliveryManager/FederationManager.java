/* This file is part of Delivery Manager.
 * (c) 2006 Matteo Miraz, Politecnico di Milano
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


package eu.secse.deliveryManager;

import java.util.Collection;

import javax.annotation.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.secse.deliveryManager.db.federations.FederatedElement;
import eu.secse.deliveryManager.db.federations.FederatedFacet;
import eu.secse.deliveryManager.db.federations.FederatedService;
import eu.secse.deliveryManager.db.federations.Federation;
import eu.secse.deliveryManager.db.filters.FederationInterest;
import eu.secse.deliveryManager.db.filters.Interest;
import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.exceptions.NotJoinedException;
import eu.secse.deliveryManager.interest.InterestFederation;
import eu.secse.deliveryManager.model.PromotionDetails;
import eu.secse.deliveryManager.reds.IRedsProxy;
import eu.secse.deliveryManager.reds.InterestEnvelope;
import eu.secse.deliveryManager.registry.IRegistryProxy;

@Stateless
public class FederationManager implements IFederationManager {

	private static final Log log = LogFactory.getLog(IFederationManager.class);

	@PersistenceContext(unitName="deliveryManager")
	protected EntityManager em;
	
	@EJB private IRegistryProxy registry;
	@EJB private IRedsProxy reds;
	
	@EJB private IFederationModelManager iFedModel;

	public void joinFederation(String federationName) {
		
		log.info("Joining the federation " + federationName);
		
		if(this.em.find(Federation.class, federationName) == null) {
			Federation db = new Federation(federationName);
			this.em.persist(db);
			
			FederationInterest idb = new FederationInterest(db);
			this.em.persist(idb);
			
			reds.subscribe(new InterestEnvelope(new InterestFederation(federationName), registry.getRegistryId(), idb.getId()));

			db.setInterestId(idb.getId());
		} else {
			log.warn("Already in the federation " + federationName);
		}
	}

	public boolean isJoinedFederation(String federationName) {
		return this.em.find(Federation.class, federationName) != null;
	}

	public void leaveFederation(String federationName)
		throws NotJoinedException {
	
		log.info("Leaving the federation " + federationName);
		
		Federation federation = this.em.find(Federation.class, federationName);
		if(federation != null) {
			try {
				for(FederatedElement elem : federation.getElements()) {
					log.info("removing promotion " + elem.getPromotionId());
	
					// LAZY mandare un messaggio "scaduto" x far scadere subito l'elemento... ma può creare problemi di coerenza (x ora infatti verrebbe ignorato)
				}
	
				Interest i = this.em.find(Interest.class, federation.getInterestId());

				if(i != null && i instanceof FederationInterest) {
					this.reds.unsubscribe(registry.getRegistryId(), i.getId());
					this.em.remove(i);		
				} else {
					log.warn("Error removing the federation interest: " + (i == null?"interest not found":(i instanceof FederationInterest?" unkonwn problem":"interest  is not a federation interest")));
				}
			} finally {
				this.em.remove(federation);
			}
		} else {
			log.warn("Not in the federation " + federationName);
			throw new NotJoinedException("Not in the federation " + federationName);
		}
	}

	public String[] getJoinedFederations() {
		Collection<Federation> feds = Federation.getAllFederations(this.em);
	
		String[] ret = new String[feds.size()];
		int j = 0;
		for (Federation f : feds)
			ret[j++] = f.getFederationName();
		
		return ret;
	}

	public long promoteServiceSpecifications(String id, String federationName) throws NotJoinedException, NotFoundException {
	
		Federation fed = this.em.find(Federation.class, federationName);
		if(fed == null) throw new NotJoinedException("You are not in " + federationName);
	
		FederatedService felem = iFedModel.createAndSend(fed, id, false);

		return felem.getPromotionId();
	}

	public long promoteServiceFacets(String serviceId, String federationName) throws NotJoinedException, NotFoundException {
		Federation fed = this.em.find(Federation.class, federationName);
		if(fed == null) throw new NotJoinedException("You are not in " + federationName);
	
		FederatedService felem = iFedModel.createAndSend(fed, serviceId, true);

		return felem.getPromotionId();
	}

	public long promoteFacetSpecification(String serviceId, String schemaId, String federationName) throws NotJoinedException, NotFoundException {
	
		Federation fed = this.em.find(Federation.class, federationName);
		if(fed == null) throw new NotJoinedException("You are not in " + federationName);
		
		FederatedService fs = FederatedService.getByServiceFederation(em, fed, serviceId);
		if(fs == null || fs.isShareAllFacets()) throw new NotFoundException("The service with id " + serviceId + " is " + (fs == null?"not shared":"already sharing all its facets")); 
		
		FederatedFacet felem = iFedModel.createAndSend(fed, fs, schemaId);
		
		return felem.getPromotionId();
	}

	public void discardPromotion(long id) throws NotFoundException {

		FederatedElement felem = this.em.find(FederatedElement.class, id);
		if(felem == null || !felem.isOriginal())
			throw new NotFoundException("The promotion id " + id + " is not found");
		
		this.em.remove(felem);
		
		// LAZY mandare un messaggio "scaduto" x far scadere subito l'elemento... ma può creare problemi di coerenza (x ora infatti verrebbe ignorato)
	}

	public long[] getPromotions(String federationName) 
		throws NotJoinedException {
	
		Federation fed = this.em.find(Federation.class, federationName);
		if(fed == null)
			throw new NotJoinedException("You are not in " + federationName);
		
		Collection<FederatedElement> elems = fed.getElements();

		int j = 0;
		for (FederatedElement e : elems) 
			if(e.isOriginal()) j++;
		
		long[] ret = new long[j];
		j = 0;
		for (FederatedElement e : elems) 
			if(e.isOriginal()) ret[j++] = e.getPromotionId();
	
		return ret;
	}

	public PromotionDetails getDetails(long id) {
		FederatedElement elem = this.em.find(FederatedElement.class, id);

		if(elem != null && elem.isOriginal()) {
			if (elem instanceof FederatedService)
				return new PromotionDetails(elem.getPromotionId(), elem.getFederation().getFederationName(), elem.getElementId(), ((FederatedService) elem).isShareAllFacets());
			else if (elem instanceof FederatedFacet)
				return new PromotionDetails(elem.getPromotionId(), elem.getFederation().getFederationName(), ((FederatedFacet) elem).getService().getElementId(), elem.getElementId());
		}
		
		log.warn("Required details for promotion id " + id + ": not found such promotion");
		return null;
	}

	public PromotionDetails getPromotionByFacetSchemaId(String federationName, String serviceId, String facetSchemaId) throws NotJoinedException, NotFoundException {
		Federation fed = this.em.find(Federation.class, federationName);
		if(fed == null) throw new NotJoinedException("You are not in " + federationName);
		
		FederatedFacet ft = FederatedFacet.getByFacetFederation(em, facetSchemaId, fed);
		if(ft == null) throw new NotFoundException("The facet specification schema with id " + facetSchemaId + " is not in the specified federation.");
		
		return new PromotionDetails(ft.getPromotionId(), ft.getFederation().getFederationName(), ((FederatedFacet) ft).getService().getElementId(), ft.getElementId());
	}
	
	public PromotionDetails getPromotionByServiceId(String federationName, String serviceId) throws NotJoinedException, NotFoundException {
		Federation fed = this.em.find(Federation.class, federationName);
		if(fed == null) throw new NotJoinedException("You are not in " + federationName);
		
		FederatedService fs = FederatedService.getByServiceFederation(em, fed, serviceId);
		if(fs == null) throw new NotFoundException("The service with id " + serviceId + " is not in the specified federation.");
		
		return new PromotionDetails(fs.getPromotionId(), fs.getFederation().getFederationName(), fs.getElementId(), ((FederatedService) fs).isShareAllFacets());
	}
}
