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
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.secse.deliveryManager.core.ModelManager;
import eu.secse.deliveryManager.data.ElementEnt;
import eu.secse.deliveryManager.data.ElementExtraInfo;
import eu.secse.deliveryManager.data.FacetEnt;
import eu.secse.deliveryManager.data.FacetXmlEnt;
import eu.secse.deliveryManager.data.ServiceEnt;
import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.FacetAddInfo;
import eu.secse.deliveryManager.model.FacetSpec;
import eu.secse.deliveryManager.model.FacetSpecXML;
import eu.secse.deliveryManager.sharing.data.CreatedElementExtraInfo;
import eu.secse.deliveryManager.sharing.data.ReceivedElementExtraInfo;
import eu.secse.deliveryManager.timeout.ILeaseManager;

@Stateless
public class ShareManager implements IShareManager{

	private static final Log log = LogFactory.getLog(IShareManager.class);

	@PersistenceContext(unitName="deliveryManager")
	protected EntityManager em;

	@EJB private ILeaseManager iLease;

	@EJB private IShareManagerMBean redsManager;

	@EJB private ModelManager modelManager;


	public void add(DService dsrv, ServiceEnt srv) {
		//send service
		send(dsrv, srv);

		//set initial renew
		CreatedElementExtraInfo elementExtraInfo = ((CreatedElementExtraInfo)srv.getExtraInfo().get(CreatedElementExtraInfo.INFO_TYPE));
		elementExtraInfo.setRenew(iLease.getInitialRenew());

		em.flush();
	}

	public void add(FacetSpec dfacet, FacetEnt facet) {
		if(dfacet instanceof FacetAddInfo){
			FacetAddInfo facetAddInfo = (FacetAddInfo)dfacet;
			// publishes the  DFederationMessage with FacetAddInfo
			send( facet, (FacetAddInfo)dfacet);

//			set initial renew
			ReceivedElementExtraInfo extraInfo = new ReceivedElementExtraInfo(facet, ReceivedElementExtraInfo.INFO_TYPE,true, false, null);
			extraInfo.setExpire((Date)facetAddInfo.getInfo());
			em.persist(extraInfo);
			em.flush();


		} else{
			DService service = modelManager.getServiceData(facet.getService().getElemPK().getId());

			add(service, facet.getService());

		}	
	}

	public void add(FacetSpecXML xml, FacetXmlEnt xml2) {
		FacetEnt facetEnt = xml2.getFacet();
		Collection<ElementExtraInfo> extraInfo = facetEnt.getExtraInfo().values();
		if(extraInfo.size()> 0){
			FacetAddInfo facetAddInfo = modelManager.getFacetAdditionalData(facetEnt.getService().getElemPK().getId(), facetEnt.getElemPK().getId());
			this.add(facetAddInfo, facetEnt);
		}
		else {
			FacetSpec facetSpec = modelManager.getFacetSpecificationData(facetEnt.getService().getElemPK().getId(), facetEnt.getElemPK().getId());
			this.add(facetSpec, facetEnt);
		}
	}

	public void delete(ServiceEnt serv) {
		// TODO Auto-generated method stub

	}

	public void delete(FacetEnt serv) {
		// TODO Auto-generated method stub

	}

	public void delete(FacetXmlEnt serv) {
		// TODO Auto-generated method stub

	}

	public void send(DService dsrv, ServiceEnt srv) {
		//set standard renew
		CreatedElementExtraInfo elementExtraInfo = ((CreatedElementExtraInfo)srv.getExtraInfo().get(CreatedElementExtraInfo.INFO_TYPE));
		elementExtraInfo.setRenew(iLease.getStandardRenew());
		em.flush();

		//publish service with specificationfacet
		dsrv.setInfo(iLease.getLease());
		redsManager.publish(dsrv);

		//	add additional facets
		if(elementExtraInfo.isShareAll()){
			log.info("Sending Additional Facet for service "+dsrv.getServiceID());

			Collection<FacetAddInfo> facetAddInfo = modelManager.getFacetAdditionalInfo(dsrv.getServiceID());
			if(facetAddInfo!=null){
				for (FacetAddInfo f : facetAddInfo) {
					f.setInfo(iLease.getLease());
					redsManager.publish(f);
				}
			}
		}

	}

	public void send(FacetEnt facetEnt, FacetAddInfo facetAddInfo){
		//set standard renew
		((CreatedElementExtraInfo)facetEnt.getExtraInfo().get(CreatedElementExtraInfo.INFO_TYPE)).setRenew(iLease.getStandardRenew());
		em.flush();

		facetAddInfo.setInfo(iLease.getLease());

		redsManager.publish(facetAddInfo);
	}



	public void received(DService service) throws NotFoundException{
		log.debug("received service " + service.getServiceID() );
		ServiceEnt serviceEnt = modelManager.add(service);
		Date lease = (Date)service.getInfo();
		if(serviceEnt!=null)
			addExtraInfo(serviceEnt, lease );
		else throw new NotFoundException("Impossible add service "+service.getServiceID());

		Collection<FacetSpec> facetSpecs = service.getSpecType();
		for(FacetSpec facet: facetSpecs) {
			FacetEnt facetEnt = modelManager.add(facet, serviceEnt);
			addExtraInfo(facetEnt, lease);
		}
	}

	public void received(FacetAddInfo facet) {
		if(modelManager.lookup(facet.getServiceID())!= null){
			FacetEnt facetEnt = modelManager.add(facet);
			addExtraInfo(facetEnt, (Date)facet.getInfo());	
		} else { 
			/* This facet arrived before the serivce it refers to... 
			 * In a future release it should be nice to cache this facet
			 * instad of throwing it away */
		}

	}


	private ReceivedElementExtraInfo addExtraInfo(ElementEnt elementEnt, Date lease) {
		log.info("Renewing the lease of element" + elementEnt.toString());
		ReceivedElementExtraInfo extraInfo = (ReceivedElementExtraInfo)elementEnt.getExtraInfo().get(ReceivedElementExtraInfo.INFO_TYPE);
		if(extraInfo==null){
			log.info("extraInfo = null");
			extraInfo = new ReceivedElementExtraInfo(elementEnt, ReceivedElementExtraInfo.INFO_TYPE,true, false, null);
			extraInfo.setExpire(lease);
			elementEnt.getExtraInfo().put(ReceivedElementExtraInfo.INFO_TYPE, extraInfo);
			em.persist(extraInfo);
			em.flush();
		} else{
			extraInfo.setExpire(lease);
			em.flush();
		}
		return extraInfo;
	}

	

	
}