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


package eu.secse.deliveryManager.notify.data;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NamedQuery;
import javax.persistence.Query;

@Entity
@NamedQuery(name=NotificatedFacetSchema.GET_BY_ID, query="FROM NotificatedFacetSchema WHERE serviceId = :serviceId AND schemaId = :schemaId")
public class NotificatedFacetSchema extends NotificatedItem {

	static final String GET_BY_ID = "NOTIFICATED_FACET_SCHEMA_GET_BY_ID";

	private String serviceId;
	private String schemaId;
	
	public NotificatedFacetSchema() { }

	public NotificatedFacetSchema(NotificationInterest interest, String serviceId, String schemaId) {
		super(interest);
		this.serviceId = serviceId;
		this.schemaId = schemaId;
	}
	
	public String getServiceId() {
		return serviceId;
	}
	
	public String getSchemaId() {
		return schemaId;
	}
	
	@SuppressWarnings("unchecked")
	public static Collection<NotificatedFacetSchema> getByIDs(EntityManager em, String serviceId, String schemaId) {
		Query q = em.createNamedQuery(GET_BY_ID);
		q.setParameter("serviceId", serviceId);
		q.setParameter("schemaId", schemaId);
		return q.getResultList();
	}
}
