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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
/** Represents information for a DeliveryManager in a given federation
 * 
 */
@NamedQueries(value={		
		@NamedQuery(name=DeliveryManagerGossipInfo.get_dm_by_address,query="select d from DeliveryManagerGossipInfo AS d where d.federationinfo.federation.id = :federation and d.address = :address"),
		@NamedQuery(name=DeliveryManagerGossipInfo.get_expired_contacts,query="select d from DeliveryManagerGossipInfo AS d where d.federationinfo.federation.id = :federationid and d.partial_expire < current_timestamp()")
}
)
public class DeliveryManagerGossipInfo {
	
	public static final String get_dm_by_address="get_by_address";
	public final static String get_inview="get_inview";
	public final static String get_liveinview="get_liveinview";
	public final static String get_partialview="get_partialview";
	public final static String get_expired_contacts="get_expired_partial_view_members";
	private final static long CONTACT_INCREMENT=10;
	
	
	@SuppressWarnings("unused")
	@Id
	@GeneratedValue
	private long id;	
	
	private String address;
	
	@ManyToOne
	private GossipFederationInfo federationinfo;
	
	private long contactfrom;
	
	private boolean inview;
	
	private boolean partialview;
	
	private boolean liveinview;  

	@ManyToMany
	private Collection<RequestedElement> requested;
	
	@OneToMany(mappedBy="sourcedm",cascade=CascadeType.ALL)
	private Collection<IncompleteElement> incomplete;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date partial_expire;
	
	DeliveryManagerGossipInfo() {super();}
	
	public DeliveryManagerGossipInfo(String address) {
		this.address=address;
		requested=new Vector<RequestedElement>();
		incomplete=new Vector<IncompleteElement>();
		contactfrom=0;
		inview=false;
		partialview=false;
		liveinview=false;
	}
	
	public long getContactfrom() {
		return contactfrom;
	}

	public void setContactfrom(long contactfrom) {
		this.contactfrom = contactfrom;	
	}

	public boolean isInview() {
		return inview;
	}

	public void setInview(boolean inview) {
		this.inview = inview;
	}

	public boolean isLiveinview() {
		return liveinview;
	}

	public void setLiveinview(boolean liveinview) {
		this.liveinview = liveinview;
	}

	public boolean isPartialview() {
		return partialview;
	}

	public void setPartialview(boolean partialview) {
		this.partialview = partialview;
	}

	public GossipFederationInfo getFederationinfo() {
		return federationinfo;
	}

	public void setFederationinfo(
			GossipFederationInfo federationinfo) {
		this.federationinfo = federationinfo;
	}

	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}

	public void incrementContactFrom() {
		contactfrom+=CONTACT_INCREMENT;
	}

	public void setPartial_expire(Date partial_expire) {
		this.partial_expire = partial_expire;
	}

	public Date getPartial_expire() {
		return partial_expire;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj==null) return false;
		if (getClass() != obj.getClass() ) return false;
		final DeliveryManagerGossipInfo ginfo=(DeliveryManagerGossipInfo)obj;
		if (id!=ginfo.id) return false;
		return true;	
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + (int)(id);
		return result;
	}
	
	public Collection<RequestedElement> getRequested() {
		return requested;
	}
	
	public void setRequested(Collection<RequestedElement> requested) {
		this.requested=requested;
	}

	public Collection<IncompleteElement> getIncomplete() {
		return incomplete;
	}

	public void setIncomplete(
			Collection<IncompleteElement> incomplete) {
		this.incomplete = incomplete;
	}
}
