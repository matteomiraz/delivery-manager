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

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.Query;

@Entity
@NamedQuery(name=RequestedService.get_by_id,query="from RequestedService where serviceid = :serviceid")
public class RequestedService extends RequestedElement {
	
	public static final String get_by_id="get_service_with_pending_details_requested"; 
	
	private String serviceid;

	RequestedService() {
		super();		
	}

	public RequestedService(String serviceid) {
		super();
		this.serviceid = serviceid;
	}

	public String getServiceid() {
		return serviceid;
	}

	public void setServiceid(String serviceid) {
		this.serviceid = serviceid;
	}
	
	public static RequestedService searchById(EntityManager em,String serviceid) {
		Query requesting=em.createNamedQuery(RequestedService.get_by_id);
		requesting.setParameter("serviceid",serviceid);
		RequestedService service=null;
		try {
			service=(RequestedService)requesting.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
		return service;
	}
	
	
	
	
	
}
