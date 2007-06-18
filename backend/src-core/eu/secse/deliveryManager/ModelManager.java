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

import eu.secse.deliveryManager.db.data.FacetEnt;
import eu.secse.deliveryManager.db.data.ServiceEnt;
import eu.secse.deliveryManager.db.filters.FacetInterest;
import eu.secse.deliveryManager.db.filters.ServiceInterest;
import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.model.DFacetSpecificationSchema;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.registry.IRegistryProxy;

@Stateless
public class ModelManager implements IModelManager {

	private static final Log log = LogFactory.getLog(IModelManager.class);
	
	@PersistenceContext(unitName="deliveryManager")
	protected EntityManager em;
	
	@EJB private IRegistryProxy iReg;
	@EJB private IInterestManager interest;
			
	public void storeService(DService srv) {
		log.debug("storeService(" + srv.getServiceID() + ")");
		
		try {
			try {
				ServiceEnt srvEnt = ServiceEnt.searchByID(this.em, srv.getServiceID());
				log.debug("Renewing the lease of " + srvEnt.getServiceID());
				srvEnt.setLease(srv.getLease());

				// check if the set specification facets is changed 
				this.iReg.deployService(srv);
			} catch (NotFoundException e) {
				log.debug("Received a new service");
				
				// Deploy the service and the specification facets 
				this.iReg.deployService(srv);

				ServiceEnt srvEnt = new ServiceEnt(srv.getServiceID(), srv.getLease(), srv.isAllowAdditionalInformation());
				this.em.persist(srvEnt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void storeFacet(DFacetSpecificationSchema facetSchema) {
		log.debug("storeFacet()");

		try {
			// 1. search the service
			ServiceEnt srvEnt;
			try {
				srvEnt = ServiceEnt.searchByID(this.em, facetSchema.getServiceID());
			} catch (NotFoundException e) {
				log.warn("Cannot store the additional information facet " + facetSchema.getSchemaID() + ": the service " + facetSchema.getServiceID() + " is not received.");
				
				// The delivery manager must create a filter for that service?
				try {
					Collection<FacetInterest> facetInterests = FacetInterest.getFacetInterestBySid(em, facetSchema.getServiceID());
					// there is at least one filter that get the desidered serviceID
					if(facetInterests.size() > 0) return;
					
					Collection<ServiceInterest> serviceInterests = ServiceInterest.getServiceInterestByServiceID(em, facetSchema.getServiceID());
					// there is at least one filter that get the desidered serviceID
					if(serviceInterests.size() > 0) return;
				} catch (Throwable e1) {
					// Not found
				}

				// Create a filter for the service
				long id = interest.subscribeService(facetSchema.getServiceID());
				log.info("Create the filter id : " + id + " for retriving the service id " + facetSchema.getServiceID());
				return;
			}
			
			// 2. store the facet
			FacetEnt fEnt;
			try {
				fEnt = FacetEnt.searchByID(this.em, facetSchema.getSchemaID());
				fEnt.setLease(facetSchema.getLease());
				
				// check if the xml is up-to-date
				iReg.storeFacetSpecification(facetSchema);
			} catch (NotFoundException e) {
				iReg.storeFacetSpecification(facetSchema);

				fEnt = new FacetEnt(srvEnt, facetSchema.getSchemaID(), false, facetSchema.getLease());
				this.em.persist(fEnt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public void delete(ServiceEnt s) {
		log.debug("ModelManager.deleteService()");
		
		
		try {
			this.iReg.undeployService(s.getServiceID());
			this.em.remove(s);
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("Cannot remove service with id: " + s.getServiceID() + " due to " + e.getMessage());
		}
	}
	
	public void delete(FacetEnt ft) {
		log.debug("ModelManager.deleteFacet()");
		
		try {
			this.iReg.removeFacetSpecification(ft.getFacetSchemaID());
			this.em.remove(ft);
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("Cannot remove facet schema with id: " + ft.getFacetSchemaID() + " due to " + e.getMessage());
		}
	}
}