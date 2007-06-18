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


package eu.secse.deliveryManager.reds;

import polimi.reds.ComparableFilter;
import polimi.reds.Message;
import eu.secse.deliveryManager.interest.Interest;

public class InterestEnvelope implements ComparableFilter {

	private static final long serialVersionUID = -3626899393365144959L;

	/** Interest wrapped */
	private Interest interest;
	
	/** Subscriber node */
	private String node;
	/** Subscriber ID */
	private long id;
	
	public InterestEnvelope(Interest interest, String node, long id) {
		this.interest = interest;
		this.node = node;
		this.id = id;
	}
	
	public long getId() {
		return this.id;
	}
	
	public boolean isCoveredBy(ComparableFilter filter) {
		return filter instanceof InterestEnvelope &&
			this.interest.isCoveredBy(((InterestEnvelope)filter).interest);
	}

	public boolean matches(Message msg) {
		if(!(msg instanceof Envelope)) 
			return false;

		return this.interest.matches(((Envelope)msg).getObject());
	}
	
	@Override
	public String toString() {
		return "[InterestEnvelope - " + this.node + ":" + this.id + " - " + (this.interest==null?"null":this.interest.toString()) + "]";
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + (int) (this.id ^ (this.id >>> 32));
		result = PRIME * result + ((this.node == null) ? 0 : this.node.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		final InterestEnvelope other = (InterestEnvelope) obj;
		if (this.id != other.id)
			return false;
		if (this.node == null) {
			if (other.node != null)
				return false;
		} else if (!this.node.equals(other.node))
			return false;
		return true;
	}
}
