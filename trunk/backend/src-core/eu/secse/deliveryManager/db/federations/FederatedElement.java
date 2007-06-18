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


package eu.secse.deliveryManager.db.federations;

import java.util.Collection;
import java.util.Date;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Rappresenta un elemento promosso in una federazione.
 * @author matteo
 */
@Entity
@NamedQueries({
	@NamedQuery(name=FederatedElement.FEDERATED_ELEMENT_TO_RENEW, query="FROM FederatedElement WHERE original = true AND timeout < current_timestamp()"),
	@NamedQuery(name=FederatedElement.FEDERATED_ELEMENT_TO_EXPIRE, query="FROM FederatedElement WHERE original = false AND timeout < current_timestamp()")
})
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name = "type")
public abstract class FederatedElement {
	
	static final String FEDERATED_ELEMENT_TO_EXPIRE = "FederatedElementToExpire";
	static final String FEDERATED_ELEMENT_TO_RENEW = "FederatedElementToRenew";

	@Id
	@GeneratedValue
	private long promotionId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Federation federation;
	
	private String elementId;
	
	/** originally Promoted in the federation By This Delivery Manager */
	private boolean original;
	
	/** renew or lease of this element */
	@Temporal(TemporalType.TIMESTAMP)
	private Date timeout;

	protected FederatedElement() { /* empty constructor */ }

	/** If the <b>PROMOTION</b> is received from another delivery manager, set original false */
	protected FederatedElement(Federation federation, String elementId, boolean original) {
		this.federation = federation;
		this.elementId = elementId;
		this.original = original;
	}

	public Federation getFederation() {
		return federation;
	}
	
	public String getElementId() {
		return elementId;
	}
	
	public long getPromotionId() {
		return promotionId;
	}
	
	public void setTimeout(Date timeout) {
		this.timeout = timeout;
	}
	
	public Date getTimeout() {
		return timeout;
	}
	
	public boolean isOriginal() {
		return original;
	}
	
	/** Get the list of the elements to be renewed. */
	@SuppressWarnings("unchecked")
	public static Collection<FederatedElement> getRenew(EntityManager em) {
		return em.createNamedQuery(FEDERATED_ELEMENT_TO_RENEW).getResultList();
	}

	/** Get the list of the elements to be renewed. */
	@SuppressWarnings("unchecked")
	public static Collection<FederatedElement> getExpired(EntityManager em) {
		return em.createNamedQuery(FEDERATED_ELEMENT_TO_EXPIRE).getResultList();
	}
}