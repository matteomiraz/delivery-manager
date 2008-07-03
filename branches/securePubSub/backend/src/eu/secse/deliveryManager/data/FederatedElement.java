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

package eu.secse.deliveryManager.data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * Rappresenta un elemento promosso in una federazione (da questo o altri dm).
 */

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class FederatedElement {
	
	@Id
	@GeneratedValue
	private long id;
	
	@ManyToOne(optional=false)
	private FederationEnt federation;
	
	@ManyToOne(optional=false)
	private ElementEnt element;
	
	@OneToOne(optional=true,cascade={CascadeType.ALL})
	private FederatedElementExtraInfo extraInfo;
	
	/* true when received the element */
	/* ElementEnt.ownership && FederatedElement.received = 0 */

	private boolean received;
	
	protected FederatedElement() { /* empty constructor */ }

	public FederatedElement(FederationEnt federation, ElementEnt element, FederatedElementExtraInfo extraInfo) {		
		this.federation = federation;
		federation.addElement(this);
		this.element = element;
		element.addFederatedElement(this);
		this.extraInfo = extraInfo;
		if (extraInfo!=null) {
			extraInfo.setElem(this);
		}
		this.received=true;
	}

	public long getId() {
		return id;
	}
	
	public ElementEnt getElement() {
		return element;
	}

	public void setElement(ElementEnt element) {
		this.element = element;
	}

	public FederationEnt getFederation() {
		return federation;
	}

	public void setFederation(FederationEnt federation) {
		this.federation = federation;
	}

	public FederatedElementExtraInfo getExtraInfo() {
		return extraInfo;
	}
	
	public void setExtraInfo(FederatedElementExtraInfo extraInfo) {
		this.extraInfo = extraInfo;
	}

	public boolean isReceived() {
		return received;
	}

	public void setReceived(boolean received) {
		this.received = received;
	}	
}