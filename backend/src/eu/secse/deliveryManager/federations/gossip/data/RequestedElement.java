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
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;

/** Represent an element that has been requested by some delivery manager. When details arrive,
 * the delivery manager has to forward them
 * 
 * @author ercasta
 *
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class RequestedElement {
	
		@SuppressWarnings("unused")
		@Id
		@GeneratedValue
		private long id;
	
		
		@ManyToMany(mappedBy="requested")
		//Bidirectional		
		private Collection<DeliveryManagerGossipInfo> waiting; 
		
		public RequestedElement() {
			waiting=new Vector<DeliveryManagerGossipInfo>();
		}

		public Collection<DeliveryManagerGossipInfo> getWaiting() {
			return waiting;
		}

		public void setWaiting(
				Collection<DeliveryManagerGossipInfo> waiting) {
			this.waiting = waiting;
		}
				
		@Override
		public boolean equals(Object obj) {
			if (obj==null) return false;
			if (getClass() != obj.getClass() ) return false;
			final RequestedElement reqelem=(RequestedElement)obj;
			if (id!=reqelem.id) return false;
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
