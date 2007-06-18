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
import eu.secse.deliveryManager.exceptions.CredentialsNotValidException;
import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.model.DFacetSpecificationSchema;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.notify.IRegistryEventProcessor;
import eu.secse.deliveryManager.reds.IRedsProxy;
import eu.secse.deliveryManager.registry.IRegistryProxy;

@Stateless
public class Update implements IUpdate {

	private static final Log log = LogFactory.getLog(IUpdate.class);

	@PersistenceContext(unitName="deliveryManager")
	protected EntityManager em;

	@EJB private IRedsProxy iReds;
	@EJB private IRegistryProxy iReg;
	@EJB private IFederationModelManager iFed;
	@EJB private IRegistryEventProcessor iNotify;

	public void addService(String serviceId, long providerId) {

		log.info("Added service " + serviceId + " by provider: " + providerId);

		iNotify.serviceAdded(serviceId);
	}

	public void deleteService(String serviceId, long providerId) {

		log.info("Deleted service " + serviceId);

		try {
			if(this.iReg.getDeliveryManagerId() != providerId) {
				try {
					ServiceEnt srv = ServiceEnt.searchByID(em, serviceId);
					if(srv != null) {
						log.info("Removed service " + srv.getServiceID()); 
						this.em.remove(srv);
					}
				} catch (Exception e) {
					// il servizio non esiste => non devo cancellarlo!
				}

				try {
					Collection<FederatedService> fss = FederatedService.getByService(em, serviceId);
					for (FederatedService fs : fss) {
						log.info("Removing the promotion of the service id " + serviceId + " in the federation " + fs.getFederation().getFederationName()); 
						this.em.remove(fs);
					}
				} catch (Exception e) {
					// il servizio è in nessuna federazione => non devo cancellarlo!
				}
			}
		} catch (Exception e) {
			log.error("Cannot process the deletion of the service id " + serviceId + ": " + e.getMessage() + " due to " + e.getCause());
		}

		iNotify.serviceRemoved(serviceId);
	}

	public void addFacetSpecificationSchema(String serviceId, String schemaId, boolean additionalInformation, long providerId) {

		log.info("Added facet specification schema " + schemaId + " for service id " + serviceId);

		try {
			if(iReg.getDeliveryManagerId() != providerId) {
				try {
					ServiceEnt srv = ServiceEnt.searchByID(em, serviceId);
					if(srv != null && srv.isOwnership()) {
						if(additionalInformation && srv.isShareFacets()) {
							try {
								DFacetSpecificationSchema ft = this.iReg.getAdditionalInformationFacet(schemaId);
								ft.setLease(srv.getLease());
								iReds.publish(ft);
							} catch (Exception e) {
								log.warn("Cannot retrive the new facet schema with id " + schemaId);
							}
						} else if(!additionalInformation) {
							try {
								DService dsrv = this.iReg.getService(serviceId);
								dsrv.setLease(srv.getLease());
								iReds.publish(dsrv);
							} catch (Exception e) {
								log.warn("Cannot retrive the service with id " + schemaId);
							}
						}
					}
				} catch (Exception e) {
					// Service Not found: nothing to do here!
				}
			}
		} catch (Exception e) {
			log.warn(e.getMessage() + " due to: " + e.getCause());
		}

		try {
			Collection<FederatedService> fss = FederatedService.getByService(em, serviceId);
			log.info("Found " + fss.size() + " federated services");
			for (FederatedService fs : fss)
				if(fs.isOriginal()) {
					try {
						log.info("Re-sending the federated service " + fs.getElementId() + " in the federation " + fs.getFederation().getFederationName());
						iFed.send(fs); 
					} catch (Throwable e) {
						log.warn("Error: " + e.getMessage() + " due to: " + e.getCause());
					}
				}
		} catch (NotFoundException e) {
			// (federated)Service Not found: nothing to do here!
		}

		iNotify.FacetSchemaAdded(serviceId, schemaId);
	}

	public void deleteFacetSpecificationSchema(String serviceId, String schemaId, boolean additionalInformation, long providerId) {

		log.info("Deleted facet specification schema " + schemaId + " for service id " + serviceId);

		try {
			if(this.iReg.getDeliveryManagerId() != providerId) {
				try {
					FacetEnt fe = FacetEnt.searchByID(em, schemaId);
					if(fe != null) {
						log.info("Removed facet specification schema " + fe.getFacetSchemaID()); 
						this.em.remove(fe);
					}
				} catch (Exception e) {
					// il servizio non esiste => non devo cancellarlo!
				}

				try {
					Collection<FederatedFacet> fss = FederatedFacet.getByFacet(em, schemaId);
					for (FederatedFacet fs : fss) {
						log.info("Removing the promotion of the facet specification schema id " + schemaId + " in the federation " + fs.getFederation().getFederationName()); 
						this.em.remove(fs);
					}
				} catch (Exception e) {
					// il servizio è in nessuna federazione => non devo cancellarlo!
				}
			}
		} catch (Exception e) {
			log.error("Cannot process the deletion of the service id " + serviceId + ": " + e.getMessage() + " due to " + e.getCause());
		}

		iNotify.FacetSchemaRemoved(serviceId, schemaId);
	}

	public void addFacetSpecificationXML(String serviceId, String schemaId, String xmlId, boolean additionalInformation, long providerId) {

		log.info("Added facet specification xml id  " + xmlId + ", schema " + schemaId + " for service id " + serviceId);

		try {
			facetXmlModification(providerId, serviceId, schemaId);
		} catch (Exception e) {
			log.warn("Cannot add the facet specification xml id " + xmlId);
		}

		try {
			Collection<FederatedService> fss = FederatedService.getByService(em, serviceId);
			log.info("Found " + fss.size() + " federated services");
			for (FederatedService fs : fss)
				if(fs.isOriginal()) {
					try {
						log.info("Re-sending the federated service " + fs.getElementId() + " in the federation " + fs.getFederation().getFederationName());
						iFed.send(fs); 
					} catch (Throwable e) {
						log.warn("Error: " + e.getMessage() + " due to: " + e.getCause());
					}
				}
		} catch (NotFoundException e) {
			// (federated)Service Not found: nothing to do here!
		}

		try {
			Collection<FederatedService> fss = FederatedService.getByService(em, serviceId);
			log.info("Found " + fss.size() + " federated services");
			for (FederatedService fs : fss)
				if (fs.isOriginal()) {
					try {
						log.info("Re-sending the federated service "
								+ fs.getElementId() + " in the federation "
								+ fs.getFederation().getFederationName());
						iFed.send(fs);
					} catch (Throwable e) {
						log.warn("Error: " + e.getMessage() + " due to: "
								+ e.getCause());
					}
				}
		} catch (NotFoundException e) {
			// (federated)Service Not found: nothing to do here!
		}

		try {
			Collection<FederatedFacet> ffs = FederatedFacet.getByFacet(em, schemaId);
			log.info("Found " + ffs.size() + " federated facets");
			for (FederatedFacet ft : ffs)
				if (ft.isOriginal()) {
					try {
						log.info("Re-sending the federated facet "
								+ ft.getElementId() + " in the federation "
								+ ft.getFederation().getFederationName());
						iFed.send(ft);
					} catch (Throwable e) {
						log.warn("Error: " + e.getMessage() + " due to: "
								+ e.getCause());
					}
				}
		} catch (NotFoundException e) {
			// (federated)Service Not found: nothing to do here!
		}
		
		iNotify.facetXmlAdded(serviceId, schemaId, xmlId, additionalInformation);
	}

	/**
	 * @param providerId
	 * @param serviceId
	 * @param schemaId
	 * @throws CredentialsNotValidException
	 * @throws RemoteException
	 */
	private void facetXmlModification(long providerId, String serviceId, String schemaId) throws CredentialsNotValidException, RemoteException {
		if(iReg.getDeliveryManagerId() != providerId) {
			try {
				// controllo se il servizio condivide tutto
				ServiceEnt srv = ServiceEnt.searchByID(em, serviceId);
				if(srv != null && srv.isOwnership() && srv.isShareFacets()) {
					// dobbiamo spedire tutte le facet associate al servizio
					try {
						DFacetSpecificationSchema ft = this.iReg.getAdditionalInformationFacet(schemaId);
						ft.setLease(srv.getLease());
						iReds.publish(ft);
					} catch (Exception e) {
						log.warn("Cannot retrive the new facet schema with id " + schemaId);
					}
				}
			} catch (Exception e) {
				// il servizio non esiste => non devo spedire la facet!
			}

			try {
				// controllo se il servizio condivide tutto
				FacetEnt fe = FacetEnt.searchByID(em, schemaId);
				if(fe != null) {
					// dobbiamo spedire tutte l'aggiornamento alla facet
					try {
						DFacetSpecificationSchema ft = this.iReg.getAdditionalInformationFacet(schemaId);
						ft.setLease(fe.getLease());
						iReds.publish(ft);
					} catch (Exception e) {
						log.warn("Cannot retrive the new facet schema with id " + schemaId);
					}
				}
			} catch (Exception e) {
				// la facet non esiste => non devo spedire la facet!
			}
		}
	}

	public void deleteFacetFacetSpecificationXML(String serviceId, String schemaId, String xmlId, boolean additionalInformation, long providerId) {

		log.info("Deleted facet specification xml " + xmlId + ", schema " + schemaId + " for service id " + serviceId);

		try {
			facetXmlModification(providerId, serviceId, schemaId);
		} catch (Exception e) {
			log.warn("Cannot delete facet specification xml with id " + xmlId);
		}

		iNotify.facetXmlRemoved(serviceId, schemaId, xmlId, additionalInformation);
	}
}
