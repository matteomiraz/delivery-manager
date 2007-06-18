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

import java.rmi.RemoteException;

import javax.annotation.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.secse.deliveryManager.db.data.FacetEnt;
import eu.secse.deliveryManager.db.data.ServiceEnt;
import eu.secse.deliveryManager.exceptions.CredentialsNotValidException;
import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.model.DFacetSpecificationSchema;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.reds.IRedsProxy;
import eu.secse.deliveryManager.registry.IRegistryProxy;

@Stateless
public class ShareManager implements IShareManager {
	
	private static final Log log = LogFactory.getLog(IShareManager.class);
	

	@PersistenceContext(unitName="deliveryManager")
	protected EntityManager em;

	@EJB private IRegistryProxy iReg;
	@EJB private IRedsProxy iReds;
	@EJB private ILeaseManager iLease;

	public void shareServiceSpecifications(String serviceId) throws NotFoundException {
		log.info("Sharing service " + serviceId); 

		// make sure that I can share the service: it is created originally in the corrent registry
		try {
			if(iReg.getServiceProviderId(serviceId) == iReg.getDeliveryManagerId())
				throw new NotFoundException("You cannot share a service created by another registry");
		} catch (RemoteException e) {
			throw new NotFoundException("Cannot retrive the desidered service:" + e.getMessage() + " due to: " + e.getCause());
		} catch (CredentialsNotValidException e) {
			throw new NotFoundException("Cannot retrive the desidered service:" + e.getMessage() + " due to: " + e.getCause());
		}
		
		ServiceEnt srv;
		try {
			srv = ServiceEnt.searchByID(em, serviceId);
			srv.setLease(this.iLease.getLease());
			srv.setRenew(this.iLease.getInitialRenew());
			
			srv.setShareFacets(false);
		} catch (NotFoundException e) {
			srv = new ServiceEnt(serviceId, iLease.getLease(), iLease.getInitialRenew(), false);
			this.em.persist(srv);
		}

		try {
			DService dService = this.iReg.getService(serviceId);
			dService.setLease(iLease.getLease());
			this.iReds.publish(dService);
		} catch (RemoteException e1) {
			throw new NotFoundException("Cannot retrive the service id " + serviceId);
		}
		
	}

	public void shareAllServiceAdditionalInformations(String serviceId) throws NotFoundException {
		log.info("Sharing service " + serviceId + " and all its facets"); 

		// make sure that I can share the service: it is created originally in the corrent registry
		try {
			if(iReg.getServiceProviderId(serviceId) == iReg.getDeliveryManagerId())
				throw new NotFoundException("You cannot share a service created by another registry");
		} catch (RemoteException e) {
			throw new NotFoundException("Cannot retrive the desidered service:" + e.getMessage() + " due to: " + e.getCause());
		} catch (CredentialsNotValidException e) {
			throw new NotFoundException("Cannot retrive the desidered service:" + e.getMessage() + " due to: " + e.getCause());
		}
		
		ServiceEnt srv;
		try {
			srv = ServiceEnt.searchByID(em, serviceId);
			srv.setLease(this.iLease.getLease());
			srv.setRenew(this.iLease.getInitialRenew());
			
			srv.setShareFacets(true);
			
			for (FacetEnt f : srv.getFacets()) 
				this.em.remove(f);
		} catch (NotFoundException e) {
			srv = new ServiceEnt(serviceId, iLease.getLease(), iLease.getInitialRenew(), true);
			this.em.persist(srv);
		}

		try {
			DService dService = this.iReg.getService(serviceId);
			dService.setLease(iLease.getLease());
			this.iReds.publish(dService);

			for (DFacetSpecificationSchema dft : iReg.getAdditionalInformationFacets(srv.getServiceID())) {
				dft.setLease(srv.getLease());
				this.iReds.publish(dft);
			}
		} catch (RemoteException e) {
			throw new NotFoundException("Cannot retrive the service id " + serviceId);
		}
	}

	public void shareServiceAdditionalInformation(String serviceId, String facetSchemaId) throws NotFoundException {
		log.info("Sharing additional information facet with schema id "  + facetSchemaId + " (service " + serviceId + ")"); 

		// make sure that I can share the facet (part 1): it is created originally in the corrent registry
		try {
			if(iReg.getFacetSchemaProviderId(facetSchemaId) == iReg.getDeliveryManagerId())
				throw new NotFoundException("You cannot share a service created by another registry");
		} catch (RemoteException e) {
			throw new NotFoundException("Cannot retrive the desidered facet:" + e.getMessage() + " due to: " + e.getCause());
		} catch (CredentialsNotValidException e) {
			throw new NotFoundException("Cannot retrive the desidered facet:" + e.getMessage() + " due to: " + e.getCause());
		}
		
		// make sure that I can share the facet (part 2): the service is 
		//  * local and not share all its facets
		//  * remote and addinfo is true
		ServiceEnt srv = ServiceEnt.searchByID(em, serviceId);

		if(srv.isOwnership() && srv.isShareFacets())
			throw new NotFoundException("The service already shares all its facets");

		if(!srv.isOwnership() && !srv.isAllowedAddInfo())
			throw new NotFoundException("The publisher does not allows to add additional facets to this service: you cannot share them to the other registries");

		
		// store in the db the sharing of the facet
		FacetEnt facet;
		try {
			facet = FacetEnt.searchByID(em, facetSchemaId);
			facet.setLease(this.iLease.getLease());
			facet.setRenew(this.iLease.getInitialRenew());
			
		} catch (NotFoundException e) {
			facet = new FacetEnt(srv, facetSchemaId, true, iLease.getInitialRenew());
			this.em.persist(srv);
		}
		
		// share the facet
		try {
			DFacetSpecificationSchema dfss = iReg.getAdditionalInformationFacet(facetSchemaId);
			dfss.setLease(iLease.getLease());
			this.iReds.publish(dfss);
		} catch (RemoteException e1) {
			throw new NotFoundException("Cannot retrive the facet with schema id " + facetSchemaId);
		}
	}	
}
