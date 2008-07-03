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

@NamedQuery(name=DeletedService.get_deleted_services, query="from DeletedService as d where d.federationid = :federationid and d.serviceid = :serviceid")
public class DeletedService extends DeletedElement {
	
	public static final String get_deleted_services="get_delete_services_in_federation";
	
	private String serviceid;

	public String getServiceid() {
		return serviceid;
	}

	DeletedService() {super();}
	
	public DeletedService(String federationid, String serviceid, long deletiontime) {
		super(federationid,deletiontime);
		this.serviceid = serviceid;
	}
	
	public static DeletedService getDeletedService(EntityManager em,String federationid, String serviceid) {
		Query q=em.createNamedQuery(DeletedService.get_deleted_services);
		q.setParameter("serviceid",serviceid);
		q.setParameter("federationid",federationid);
		DeletedService delservice=null;
		try {
			delservice=(DeletedService)q.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
		return delservice;
	}
	
}
