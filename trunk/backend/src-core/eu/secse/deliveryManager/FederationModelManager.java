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
import eu.secse.deliveryManager.db.federations.FederatedFacet;
import eu.secse.deliveryManager.db.federations.FederatedService;
import eu.secse.deliveryManager.db.federations.Federation;
import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.model.DFacetSpecificationSchema;
import eu.secse.deliveryManager.model.DFederation;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.reds.IRedsProxy;
import eu.secse.deliveryManager.registry.IRegistryProxy;

@Stateless
public class FederationModelManager implements IFederationModelManager {
	
	private static final Log log = LogFactory.getLog(IFederationModelManager.class);
	
	@PersistenceContext(unitName="deliveryManager")
	protected EntityManager em;

	@EJB private IRegistryProxy iReg;
	@EJB private IRedsProxy iReds;	
	@EJB private ILeaseManager iLease;

	public void send(FederatedService fSrv) throws NotFoundException {
		try {
			if(!fSrv.isOriginal()) throw new NotFoundException("The service with id " + fSrv.getElementId() + " is propagated to the federation " + fSrv.getFederation().getFederationName() + " by another delivery manager");
			
			fSrv.setTimeout(this.iLease.getStandardRenew());
			
			DService srv = this.iReg.getService(fSrv.getElementId());
			srv.setLease(iLease.getLease());
			if(srv == null) throw new NotFoundException("Cannot get the service " + fSrv.getElementId() + " from the registry");
			
			this.iReds.publish(new DFederation(fSrv.getFederation().getFederationName(), srv));
			
			if(fSrv.isShareAllFacets())  {
				Collection<DFacetSpecificationSchema> facets = this.iReg.getAdditionalInformationFacets(fSrv.getElementId());
				for (DFacetSpecificationSchema s : facets) {
					s.setLease(iLease.getLease());
					this.iReds.publish(new DFederation(fSrv.getFederation().getFederationName(), s));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new NotFoundException(e.getMessage());
		}
	}
	
	public void send(FederatedFacet felem) throws NotFoundException {
		try {
			if(!felem.isOriginal()) throw new NotFoundException("The facet type with id " + felem.getElementId() + " is propagated to the federation " + felem.getFederation().getFederationName() + " by another delivery manager");
			
			felem.setTimeout(this.iLease.getStandardRenew());
			
			DFacetSpecificationSchema facet = this.iReg.getAdditionalInformationFacet(felem.getElementId());
			facet.setLease(iLease.getLease());
			if(facet == null) throw new NotFoundException("Cannot get the facet " + felem.getElementId() + " from the registry");
			
			this.iReds.publish(new DFederation(felem.getFederation().getFederationName(), facet));
		} catch (Exception e) {
			e.printStackTrace();
			throw new NotFoundException(e.getMessage());
		}
	}
	
	public FederatedFacet createAndSend(Federation fed, FederatedService service, String schemaId) throws NotFoundException {
		// Search if the service is already promoted in the federation
		FederatedFacet ret = null;
		try {
			ret = FederatedFacet.getByFacetFederation(em, schemaId, fed);
		} catch (NotFoundException e) {
		}
		
		// if it is already promoted, throw a NotFoundException
		if(ret != null)
			throw new NotFoundException("The element is already promoted in the federation " + (ret.isOriginal()?"by me":"by another delivery manager"));
		
		if(service.isShareAllFacets()) 
			throw new NotFoundException("The service share all its facets, so you cannot share this facet.");
		
		ret = new FederatedFacet(fed, service, schemaId, true);
		send(ret);

		ret.setTimeout(iLease.getInitialRenew());
		this.em.persist(ret);
		return ret;
	}
	
	public FederatedService createAndSend(Federation fed, String serviceId, boolean shareAddInfo) throws NotFoundException {
		// Search if the service is already promoted in the federation
		FederatedService ret = null;
		try {
			ret = FederatedService.getByServiceFederation(em, fed, serviceId);
		} catch (NotFoundException e) {
		}
		
		// if it is already promoted, throw a NotFoundException
		if(ret != null) {
			throw new NotFoundException("The element is already promoted in the federation " + (ret.isOriginal()?"by me":"by another delivery manager"));
		} 
		
		ret = new FederatedService(fed, serviceId, shareAddInfo, true);

		send(ret);
		
		ret.setTimeout(iLease.getInitialRenew());
		this.em.persist(ret);
		return ret;
	}
	
	public void received(String federationName, DService srv) {
		Federation fed = this.em.find(Federation.class, federationName);
		
		if(fed == null) {
			log.warn("Not in the federation " + federationName);
			return;
		}
		
		ServiceEnt se = null;
		try {
			se = ServiceEnt.searchByID(em, srv.getServiceID());
			
			if(srv.getLease().after(se.getLease())) se.setLease(srv.getLease());
		} catch (NotFoundException e1) {
			se = new ServiceEnt(srv.getServiceID(), srv.getLease(), srv.isAllowAdditionalInformation());
			this.em.persist(se);
		}

		FederatedService fs = null;
		try {
			fs = FederatedService.getByServiceFederation(em, fed, srv.getServiceID());
			if(srv.getLease().after(fs.getTimeout())) fs.setTimeout(srv.getLease()); 
		} catch (NotFoundException e1) {
			fs = new FederatedService(fed, srv.getServiceID(), false, false);
			fs.setTimeout(srv.getLease());
			this.em.persist(fs);
		}
		
		try {
			// per eitare di chiudere i cicli e rideployare sul mittente le informazioni ricevute
			if(!se.isOwnership())
				iReg.deployService(srv);
		} catch (Exception e) {
			log.error(e.getMessage() + " due to: " + e.getCause());
		}
	}
	
	public void received(String federationName, DFacetSpecificationSchema facet) {
		Federation fed = this.em.find(Federation.class, federationName);
		
		if(fed == null) {
			log.warn("Not in the federation " + federationName);
			return;
		}
		
		ServiceEnt se;
		FederatedService fs;
		try {
			se = ServiceEnt.searchByID(em, facet.getServiceID());
			fs = FederatedService.getByServiceFederation(em, fed, facet.getServiceID());
		} catch (NotFoundException e) {
			log.warn("Error while retriving an element: " + e.getMessage() + " due to: " + e.getCause());
			return;
		}

		FacetEnt fe = null;
		try {
			fe = FacetEnt.searchByID(em, facet.getSchemaID());
			if(facet.getLease().after(fe.getLease()))  fe.setLease(facet.getLease());
		} catch (NotFoundException e1) {
			fe = new FacetEnt(se, facet.getSchemaID(), false, facet.getLease());
			this.em.persist(fe);
		}

		FederatedFacet ft = null;
		try {
			ft = FederatedFacet.getByFacetFederation(em, facet.getSchemaID(), fed);
			if(facet.getLease().after(ft.getTimeout()))  ft.setTimeout(facet.getLease());
		} catch (NotFoundException e1) {
			ft = new FederatedFacet(fed, fs, facet.getSchemaID(), false);
			ft.setTimeout(facet.getLease());
			this.em.persist(ft);
		}

		
		try {
			if(!fe.isOwnership())
				iReg.storeFacetSpecification(facet);
		} catch (Exception e) {
			log.error(e.getMessage() + " due to: " + e.getCause());
		}
	}
}
