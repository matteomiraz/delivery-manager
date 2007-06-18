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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Query;

@Entity
@NamedQueries({
	@NamedQuery(name=NotificationInterest.GET_ALL, query="FROM NotificationInterest"),
	@NamedQuery(name=NotificationInterest.GET_SERVICES_PROVIDER, query="FROM NotificationInterest WHERE providerName IS NOT NULL AND (serviceId IS NULL OR serviceId = :serviceId)")
})
public class NotificationInterest {
	
	static final String GET_ALL = "NotificationInterest_GET_ALL"; 
	static final String GET_SERVICES_PROVIDER = "NotificationInterest_GET_SERVICES_PROVIDER"; 
	
	@Id
	@GeneratedValue
	private int id;
	
	@ManyToOne(optional=false)
	private NotificationUser user;
	
	@OneToMany(mappedBy="interest")
	private Collection<NotificatedItem> notificatedItems;
	
	private String providerName;
	private String serviceId;
	private String baseServiceId;
	private String serviceNameRegex;
	
	private boolean notifyService;
	private boolean notifyFacetSpecificationSchema;
	private boolean notifyFacetSpecificationXml;
	
	public NotificationInterest() { }

	public NotificationInterest(NotificationUser user, String providerName, String serviceId, String baseServiceId, String serviceNameRegex, boolean notifyService, boolean notifyFacetSpecificationSchema, boolean notifyFacetSpecificationXml) {
		this.user = user;
		this.providerName = providerName;
		this.serviceId = serviceId;
		this.baseServiceId = baseServiceId;
		this.serviceNameRegex = serviceNameRegex;
		
		this.notifyService = notifyService;
		this.notifyFacetSpecificationSchema = notifyFacetSpecificationSchema;
		this.notifyFacetSpecificationXml = notifyFacetSpecificationXml;
	}

	public String getProviderName() {
		return providerName;
	}

	public String getServiceNameRegex() {
		return serviceNameRegex;
	}

	public NotificationUser getUser() {
		return user;
	}

	public String getServiceId() {
		return serviceId;
	}

	public String getBaseServiceId() {
		return baseServiceId;
	}
	
	public int getId() {
		return id;
	}

	public boolean isNotifyFacetSpecificationSchema() {
		return notifyFacetSpecificationSchema;
	}

	public boolean isNotifyFacetSpecificationXml() {
		return notifyFacetSpecificationXml;
	}

	public boolean isNotifyService() {
		return notifyService;
	}
	
	public Collection<NotificatedItem> getNotificatedItems() {
		return notificatedItems;
	}

	@SuppressWarnings("unchecked")
	public static Collection<NotificationInterest> getAll(EntityManager em) {
		return em.createNamedQuery(GET_ALL).getResultList();
	}

	@SuppressWarnings("unchecked")
	public static Collection<NotificationInterest> getServiceProvider(EntityManager em, String serviceId) {
		Query query = em.createNamedQuery(GET_SERVICES_PROVIDER);
		query.setParameter("serviceId", serviceId);
		return query.getResultList();
	}

	
}
