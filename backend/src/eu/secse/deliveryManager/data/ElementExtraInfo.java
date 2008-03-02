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

/**
 * @author   matteo
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class ElementExtraInfo {
	
	@Id @GeneratedValue private long id;
	
	/** Used when browsing from an ElementEnt to an ElementExtraInfo.
	 * Notice that this field is used as key of the map in ElementEnt, so given
	 * an element, the infoType MUST be unique */
	private String infoType;
	
	@ManyToOne(optional=false, cascade=CascadeType.REFRESH)
	private ElementEnt element;
	
	/** This extra info keeps alive the associated element. */
	private boolean keepsAlive;
	
	/** If not null, specifies the JNDI name of the handler associated with this extra info that needs to be notified about any changes of this element */
	private String notifier;
	
	/** If true states that the element is being shared.  */
	private boolean sharing;

	/** If true (and linked to a service), shares all facets of the service. */
	private boolean shareAll;
	
	public ElementExtraInfo() { }
	
	public ElementExtraInfo(ElementEnt element, String infoType, boolean keepsAlive, boolean sharing, String notifier) {
		super();
		this.element = element;
		this.infoType = infoType;
		this.keepsAlive = keepsAlive;
		this.sharing = sharing;
		this.notifier = notifier;
		shareAll = false;
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
	
	public String getInfoType() {
		return infoType;
	}
	
	public void setInfoType(String infoType) {
		this.infoType = infoType;
	}
	
	public boolean isKeepsAlive() {
		return keepsAlive;
	}
	
	public boolean isSharing() {
		return sharing;
	}
	
	public void setKeepsAlive(boolean keepsAlive) {
		if((keepsAlive && sharing) || (!keepsAlive && !sharing)) 
			System.err.println("The element " + element.getElemPK().getId() + " has invalid extra info(" + this.id + ") keepsAlive: " + keepsAlive + "; sharing: " + sharing);
		
		this.keepsAlive = keepsAlive;
	}
	
	public String getNotifier() {
		return notifier;
	}
	
	public void setNotifier(String notifier) {
		this.notifier = notifier;
	}
	
	public boolean isShareAll() {
		return shareAll;
	}
	
	public void setShareAll(boolean shareAll) {
		if((keepsAlive && sharing) || (!keepsAlive && !sharing)) 
			System.err.println("The element " + element.getElemPK().getId() + " has invalid extra info(" + this.id + ") keepsAlive: " + keepsAlive + "; sharing: " + sharing);
		
		this.shareAll = shareAll;
	}
	
}
