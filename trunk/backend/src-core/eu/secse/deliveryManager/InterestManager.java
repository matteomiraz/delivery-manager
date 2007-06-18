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

import eu.secse.deliveryManager.db.filters.FacetInterest;
import eu.secse.deliveryManager.db.filters.FederationInterest;
import eu.secse.deliveryManager.db.filters.Interest;
import eu.secse.deliveryManager.db.filters.ServiceInterest;
import eu.secse.deliveryManager.db.filters.FacetInterest.FacetInterestType;
import eu.secse.deliveryManager.exceptions.NotSubscribedException;
import eu.secse.deliveryManager.interest.InterestAdditionalInformation;
import eu.secse.deliveryManager.interest.InterestAdditionalInformationId;
import eu.secse.deliveryManager.interest.InterestFederation;
import eu.secse.deliveryManager.interest.InterestService;
import eu.secse.deliveryManager.interest.InterestSpecificationFacet;
import eu.secse.deliveryManager.reds.IRedsProxy;
import eu.secse.deliveryManager.reds.InterestEnvelope;
import eu.secse.deliveryManager.registry.IRegistryProxy;

@Stateless
public class InterestManager implements IInterestManager {
	private static final Log log = LogFactory.getLog(IInterestManager.class);

	@PersistenceContext(unitName="deliveryManager")
	protected EntityManager em;

	@EJB private IRegistryProxy registry;

	@EJB private IRedsProxy reds;

	public long[] getAllInterestIds() {
		Collection<Interest> interests = Interest.getAllInterests(em);
		int j = 0;
		for (Interest i : interests)
			if(!(i instanceof FederationInterest)) j++;
		
		long[] ret = new long[j];
		j = 0;
		for (Interest i : interests)
			if(!(i instanceof FederationInterest)) ret[j++] = i.getId();

		return ret;
	}

	public GenericInterest getInterestById(long id) throws NotSubscribedException {
		Interest i = this.em.find(Interest.class, id);
		
		if(i == null) throw new NotSubscribedException("The interest id " + id + " is not found");
		if(i instanceof FederationInterest) throw new NotSubscribedException("Cannot ask more information on this filter.");
		
		if (i instanceof ServiceInterest) {
			ServiceInterest is = (ServiceInterest) i;
			return new GenericInterest(is.getServiceID(), null, null, null, false);
		} if (i instanceof FacetInterest) {
			FacetInterest ft = (FacetInterest) i;
			return new GenericInterest(ft.getServiceId(), ft.getFacetSchemaId(), ft.getFacetSchema(), ft.getXPath(), ft.isAdditionalInformation());
		} else {
			log.warn("The interest id " + id + " has an unrecognizable type: " + i.getClass().getCanonicalName());
			throw new NotSubscribedException("Interest type unknown");
		}
	}

	public long subscribeAdditionalInformationFacet(String serviceId, String facetSchema, String xpath) throws NotSubscribedException {
		if(serviceId == null && facetSchema == null && xpath == null) throw new NotSubscribedException("At least one parameter must be not-null");
		
		InterestAdditionalInformation i;
		try {
			i = new InterestAdditionalInformation(serviceId, facetSchema, xpath);
		} catch (Exception e) {
			throw new NotSubscribedException("Cannot subscribe: " + e.getMessage() + " due to: " + e.getCause());
		} 
		
		FacetInterest db = new FacetInterest(FacetInterestType.additionalInformationFacet, serviceId, null, facetSchema, xpath);
		this.em.persist(db);
		
		this.reds.subscribe(new InterestEnvelope(i, registry.getRegistryId(), db.getId()));
		
		return db.getId();
	}

	public long subscribeAdditionalInformationFacetById(String serviceId, String facetSchemaId) throws NotSubscribedException {
		if(facetSchemaId == null) throw new NotSubscribedException("The facetSchemaId cannot be null");
		
		InterestAdditionalInformationId i = new InterestAdditionalInformationId(serviceId, facetSchemaId);
		
		FacetInterest db = new FacetInterest(FacetInterestType.additionalInformationFacetById, serviceId, facetSchemaId, null, null);
		this.em.persist(db);
		
		this.reds.subscribe(new InterestEnvelope(i, registry.getRegistryId(), db.getId()));
		
		return db.getId();
	}

	public long subscribeService(String serviceId) {
		InterestService i = new InterestService(serviceId);
		
		ServiceInterest db = new ServiceInterest(serviceId);
		this.em.persist(db);
		
		this.reds.subscribe(new InterestEnvelope(i, registry.getRegistryId(), db.getId()));
		
		return db.getId();
	}

	public long subscribeSpecificationFacet(String facetSchema, String xpath) throws NotSubscribedException {
		InterestSpecificationFacet i;
		try {
			i = new InterestSpecificationFacet(facetSchema, xpath);
		} catch (Exception e) {
			throw new NotSubscribedException("Cannot subscribe: " + e.getMessage() + " due to: " + e.getCause());
		} 
		
		FacetInterest db = new FacetInterest(FacetInterestType.specificationFacet, null, null, facetSchema, xpath);
		this.em.persist(db);
		
		this.reds.subscribe(new InterestEnvelope(i, registry.getRegistryId(), db.getId()));
		
		return db.getId();
	}

	public void unsubscribe(long id) throws NotSubscribedException {
		Interest i = this.em.find(Interest.class, id);

		if(i == null || i instanceof FederationInterest) throw new NotSubscribedException("Not subscribed to interest " + id);

		this.reds.unsubscribe(registry.getRegistryId(), i.getId());
		this.em.remove(i);		
	}
	
	public void subscribeAll() {
		for (Interest i : Interest.getAllInterests(em)) {
			if (i instanceof ServiceInterest) {
				ServiceInterest s = (ServiceInterest) i;
				this.reds.subscribe(new InterestEnvelope(new InterestService(s.getServiceID()), registry.getRegistryId(), i.getId()));
			} else if (i instanceof FacetInterest) {
				FacetInterest f = (FacetInterest) i;
				switch (f.getType()) {
				case additionalInformationFacet:
					try {
						reds.subscribe(new InterestEnvelope(new InterestAdditionalInformation(f.getServiceId(), f.getFacetSchema(), f.getXPath()), registry.getRegistryId(), f.getId()));
					} catch (Exception e) {
						log.warn("cannot re-subscribe to the interest id: " + f.getId());
					}
					break;
				case additionalInformationFacetById:
					reds.subscribe(new InterestEnvelope(new InterestAdditionalInformationId(f.getServiceId(), f.getFacetSchemaId()), registry.getRegistryId(), f.getId()));
					break;
				case specificationFacet:
					try {
						this.reds.subscribe(new InterestEnvelope(new InterestSpecificationFacet(f.getFacetSchema(), f.getXPath()), registry.getRegistryId(), f.getId()));
					} catch (Exception e) {
						log.warn("cannot re-subscribe to the interest id: " + f.getId());
					}
					break;
				}
			} else if (i instanceof FederationInterest) {
				FederationInterest fed = (FederationInterest) i;
				this.reds.subscribe(new InterestEnvelope(new InterestFederation(fed.getFederation().getFederationName()), registry.getRegistryId(), fed.getId()));
			}
		}
	}
	
//	public long subscribe(Federation federation) {
//		FederationInterest db = new FederationInterest(federation);
//		this.em.persist(db);
//		
//		this.reds.subscribe(new InterestEnvelope(new InterestFederation(federation.getFederationName()), this.nodeManager.getIdentifier(), db.getId()));
//
//		return db.getId();
//	}
}