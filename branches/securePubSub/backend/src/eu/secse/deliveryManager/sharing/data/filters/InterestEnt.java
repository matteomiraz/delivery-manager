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

package eu.secse.deliveryManager.sharing.data.filters;

import java.util.Collection;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "interestType")
@NamedQueries({
	@NamedQuery(name=InterestEnt.ALL_INTERETS, query="SELECT i FROM InterestEnt AS i")
})
public class InterestEnt {
	static final String ALL_INTERETS = "allInterets";
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@Lob
	private String description;
	
	public String getDescription() {
		return description;
	}

	public InterestEnt() { /* empty constructor */ }
	
	public InterestEnt(String description) {
		super();
		this.description = description;
	}

	public long getId() {
		return id;
	}
	
	@SuppressWarnings("unchecked")
	public static Collection<InterestEnt> getAllInterests(EntityManager em) {
		return em.createNamedQuery(ALL_INTERETS).getResultList();
	}
}
