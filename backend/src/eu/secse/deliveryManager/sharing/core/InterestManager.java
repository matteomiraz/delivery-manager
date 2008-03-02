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

package eu.secse.deliveryManager.sharing.core;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.secse.deliveryManager.core.FacetInterest;
import eu.secse.deliveryManager.exceptions.NotSubscribedException;
import eu.secse.deliveryManager.interest.Interest;
import eu.secse.deliveryManager.interest.InterestAdditionalInformation;
import eu.secse.deliveryManager.interest.InterestAdditionalInformationId;
import eu.secse.deliveryManager.interest.InterestService;
import eu.secse.deliveryManager.interest.MultipleInterestSpecificationFacet;
import eu.secse.deliveryManager.reds.InterestEnvelope;
import eu.secse.deliveryManager.registry.IRegistryProxy;
import eu.secse.deliveryManager.sharing.data.filters.InterestEnt;
import eu.secse.deliveryManager.sharing.data.filters.InterestFacetEnt;
import eu.secse.deliveryManager.sharing.data.filters.InterestServiceEnt;
import eu.secse.deliveryManager.sharing.data.filters.InterestFacetEnt.FacetInterestType;

@Stateless
public class InterestManager implements IInterestManager {

	private static final Log log = LogFactory.getLog(IInterestManager.class);

	@EJB private IShareManagerMBean redsManager;

	@EJB private IRegistryProxy registry;

	@PersistenceContext(unitName="deliveryManager")
	protected EntityManager em;

	public void subscribeAdditionalInformationFacet(String serviceId, FacetInterest interest, String description) throws NotSubscribedException {
		InterestAdditionalInformation i;
		try {
			i = new InterestAdditionalInformation(serviceId, interest.getFacetSchema(), interest.getXpath());
		} catch (Exception e) {
			throw new NotSubscribedException("Cannot subscribe: " + e.getMessage() + " due to: " + e.getCause());
		} 
		redsManager.subscribe(new InterestEnvelope(i, registry.getRegistryId()));
		em.persist(new InterestFacetEnt(FacetInterestType.additionalInformationFacet, serviceId, null, new FacetInterest[] {interest}, description));
	}

	public void subscribeAdditionalInformationFacetById(String serviceId, String facetSchemaId, String description) throws NotSubscribedException {
		InterestAdditionalInformationId i = new InterestAdditionalInformationId(serviceId, facetSchemaId);
		redsManager.subscribe(new InterestEnvelope(i, registry.getRegistryId()));
		em.persist(new InterestFacetEnt(FacetInterestType.additionalInformationFacetById, serviceId, facetSchemaId, null, description));
	}

	public void subscribeAll(String description) {
		log.info("Resubscribing to Service and facet filters");
		Collection<InterestEnt> interests = InterestEnt.getAllInterests(em);
		for(InterestEnt i: interests){
			if (i instanceof InterestServiceEnt) 
				subscribeService(((InterestServiceEnt)i).getServiceID(), description);
			else {
				if(i instanceof InterestFacetEnt) { 
					InterestFacetEnt intFacetEnt = (InterestFacetEnt)i;
					if(intFacetEnt.getType().equals(FacetInterestType.additionalInformationFacet))
						try{
							subscribeAdditionalInformationFacet(((InterestFacetEnt)i).getServiceId(),((InterestFacetEnt)i).getFacetInterest()[0], description);
						}catch(NotSubscribedException ex){
							log.error(ex.getMessage());
						}
						else {
							if(intFacetEnt.getType().equals(FacetInterestType.additionalInformationFacetById))
								try{

									subscribeAdditionalInformationFacetById(((InterestFacetEnt)i).getServiceId(),((InterestFacetEnt)i).getFacetSchemaId(), description);
								}catch(NotSubscribedException ex){
									log.error(ex.getMessage());
								}	
						}
				} else log.warn("Filter class unknown");
			}
		}
	}

	public void subscribeService(String serviceId, String description) {

		InterestService interestService = new InterestService(serviceId);
		redsManager.subscribe(new InterestEnvelope(interestService, registry.getRegistryId()));
		em.persist(new InterestServiceEnt(serviceId, description ));

	}

	public void subscribeSpecificationFacet(FacetInterest []interest, String description) throws NotSubscribedException {
		MultipleInterestSpecificationFacet i;
		try {
			i = new MultipleInterestSpecificationFacet(interest);
		} catch (Exception e) {
			throw new NotSubscribedException("Cannot subscribe: " + e.getMessage() + " due to: " + e.getCause());
		} 
		redsManager.subscribe(new InterestEnvelope(i, registry.getRegistryId()));
		em.persist(new InterestFacetEnt(FacetInterestType.specificationFacet, null, null, interest, description));
	}
	
	public void unsubscribe(Interest interest)  {
		redsManager.unsubscribe(new InterestEnvelope(interest, registry.getRegistryId()));
	}
}
