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

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQuery;
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@NamedQuery(name=DeletedElement.by_federation, query="from DeletedElement where federationid = :federationid")
public abstract class DeletedElement {
	 
	@SuppressWarnings("unused")
	@Id
	@GeneratedValue
	private long	id;
	
	public static final String by_federation="search_deleted_elements_by_federation";
	private String federationid;
	
	// If it is pending, we are waiting to send it, because currently the partial view is empty or federation 
	// subscription has not completed yet
	private boolean pending;
	
	@Temporal(value=TemporalType.TIMESTAMP)
	private Date deletiontime; 
	
	@Temporal(value=TemporalType.TIMESTAMP)
	private Date keep_until;
	
	DeletedElement() {}
	
	public DeletedElement(String federationid,long deletiontime) {
		this.federationid=federationid;
		this.pending=false;
		Calendar c=new GregorianCalendar();
		c.add(Calendar.SECOND,3600*24*7); //one week
		keep_until=c.getTime();		
		this.deletiontime=new Date(deletiontime);
	}

	public String getFederationid() {
		return federationid;
	}

	public void setFederationid(String federationid) {
		this.federationid = federationid;
	}

	public Date getKeep_until() {
		return keep_until;
	}

	public void setKeep_until(Date keep_until) {
		this.keep_until = keep_until;
	}

	public boolean isPending() {
		return pending;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
	}
	
	@SuppressWarnings("unchecked")
	public static Collection<DeletedElement> searchByFederation(EntityManager em,String federationid) {
		Query q=em.createNamedQuery(by_federation);
		q.setParameter("federationid",federationid);
		return (Collection<DeletedElement>)q.getResultList();
	}

	public Date getDeletiontime() {
		return deletiontime;
	}

	public void setDeletiontime(Date deletiontime) {
		this.deletiontime = deletiontime;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj==null) return false;
		if (getClass() != obj.getClass() ) return false;
		final DeletedElement delem=(DeletedElement)obj;
		if (id!=delem.id) return false;
		return true;	
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + (int)(id);
		return result;
	}
	
	
	
	
	
}
