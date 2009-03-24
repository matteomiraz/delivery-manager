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
import java.util.Date;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import eu.secse.deliveryManager.data.FederationEnt;

/** Represents elements whose details need to be requested
 *  These elements are fetched and then put into the registry and under the control of the model
 *  manager
 *
 *
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@NamedQueries(value={
@NamedQuery(name=IncompleteElement.currently_fetching,query="from IncompleteElement where fetching=true"),
@NamedQuery(name=IncompleteElement.to_be_fetch,query="from IncompleteElement where fetching=false"),
@NamedQuery(name=IncompleteElement.byfederation,query="from IncompleteElement where sourcedm.federationinfo.federation.id=:federationid"),
@NamedQuery(name=IncompleteElement.bytimes,query="from IncompleteElement where num_try>:num_try")
})
public abstract class IncompleteElement {
	
	public static final String currently_fetching="incomplete_elements_being_fetched";
	public static final String to_be_fetch="incomplete_elements_to_be_fetch";
	public static final String byfederation="get_incomplete_elements_by_federation";
	public static final String bytimes="get_incomplete_elements_by_num_tries";
	
	@SuppressWarnings("unused")
	@Id
	@GeneratedValue
	private long	id;
	
	/** True iff details of this element have been requested */
	private boolean fetching;
	
	@ManyToMany
	private Collection<FederationEnt> incompletein;
	
	@Temporal(value=TemporalType.TIMESTAMP)
	private Date start_fetch;
	
	/** The number of times fetching failed
	 * 
	 */
	private int num_try;
	
	@ManyToOne
	private DeliveryManagerGossipInfo sourcedm;
	
	
	public IncompleteElement() {
		super();
		// TODO Auto-generated constructor stub
	}

	public IncompleteElement(DeliveryManagerGossipInfo from) {
		this.sourcedm=from;
		fetching=false;		
		num_try=0;
		incompletein=new Vector<FederationEnt>();
		incompletein.add(from.getFederationinfo().getFederation());
	}
	
	public DeliveryManagerGossipInfo getFrom() {
		return sourcedm;
	}


	public boolean isFetching() {
		return fetching;
	}

	public void setFetching(boolean fetching) {
		this.fetching = fetching;
	}

	public int getNum_try() {
		return num_try;
	}

	public void setNum_try(int num_try) {
		this.num_try = num_try;
	}

	public Date getStart_fetch() {
		return start_fetch;
	}

	public void setStart_fetch(Date start_fetch) {
		this.start_fetch = start_fetch;
	}


	public Collection<FederationEnt> getIncompleteIn() {
		return incompletein;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj==null) return false;
		if (getClass() != obj.getClass() ) return false;
		final IncompleteElement incelem=(IncompleteElement)obj;
		if (id!=incelem.id) return false;
		return true;	
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + (int)(id);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Collection<IncompleteElement> getByFederation(EntityManager em,FederationEnt fed) {
		Query q=em.createNamedQuery(IncompleteElement.byfederation);
		q.setParameter("federationid",fed.getId());
		return (Collection<IncompleteElement>)q.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public static Collection<IncompleteElement> getToBeFetch(EntityManager em) {
		Query q=em.createNamedQuery(IncompleteElement.to_be_fetch);
		return (Collection<IncompleteElement>)q.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public static Collection<IncompleteElement> getTriedManyTimes(EntityManager em, int times) {
		Query q=em.createNamedQuery(IncompleteElement.bytimes);
		q.setParameter("num_try",times);
		return (Collection<IncompleteElement>)q.getResultList();
	}
}
