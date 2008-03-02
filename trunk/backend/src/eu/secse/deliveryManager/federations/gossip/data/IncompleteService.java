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

package eu.secse.deliveryManager.federations.gossip.data;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.secse.deliveryManager.data.FederationEnt;

/** An incomplete service, whose details have to be requested
 * 
 * @author ercasta
 *
 */
@Entity
@NamedQueries(value={
@NamedQuery(name=IncompleteService.get_by_service_id,query="from IncompleteService as i where i.serviceid = :serviceid"),
@NamedQuery(name=IncompleteService.get_all,query="from IncompleteService")
})
public class IncompleteService extends IncompleteElement {

	public static final String get_by_service_id="get_incomplete_service_in_federation";
	public static final String get_all="get_all_incomplete_service";
	
	private static final Log log = LogFactory.getLog(IncompleteService.class);
	
	private String serviceid;
	


	public IncompleteService() {
		super();
	}

	public IncompleteService(DeliveryManagerGossipInfo from, String serviceid) {
		super(from);		
		this.serviceid = serviceid;
	}

	public static IncompleteService search(EntityManager em,String serviceid, FederationEnt fed) {
		Query q=em.createNamedQuery(get_by_service_id);
		q.setParameter("serviceid",serviceid);
		try {
			IncompleteService iserv=(IncompleteService)q.getSingleResult();
			if (iserv.getIncompleteIn().contains(fed)) {
				return iserv;
			}
		} catch (NoResultException nre) {
			log.debug("No incomplete service");
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static Collection<IncompleteService> getAll(EntityManager em) {
		Query q=em.createNamedQuery(get_all);
		Collection<IncompleteService> iserv=(Collection<IncompleteService>)q.getResultList();
		return iserv;		
	}

	public String getServiceid() {
		return serviceid;
	}
	
	
	public static IncompleteService getIncompleteServiceInFederation(EntityManager em,FederationEnt fed, String serviceid) {
		IncompleteService serv=getIncompleteService(em,serviceid);
		if (serv!=null) {
			for (FederationEnt f:serv.getIncompleteIn()) {
				if (f.getId().equals(fed.getId()))
					return serv;
			}
		}			
		return null;
	}
	
	public static IncompleteService getIncompleteService(EntityManager em,
			String serviceid) {
		Query q=em.createNamedQuery(IncompleteService.get_by_service_id);
		q.setParameter("serviceid",serviceid);			
		IncompleteService service=null;
		try {
			service=(IncompleteService)q.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
		return service;
	}

}
