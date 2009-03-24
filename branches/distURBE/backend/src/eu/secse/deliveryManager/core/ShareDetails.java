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

package eu.secse.deliveryManager.core;


public class ShareDetails {
	
	private long id;
	
	/** Used when browsing from an ElementEnt to an ElementExtraInfo.
	 * Notice that this field is used as key of the map in ElementEnt, so given
	 * an element, the infoType MUST be unique */
	private String infoType;
	
	/**
	 * Id of the shared Element
	 */
	private String elementId;
	
	/**
	 * type of the shared element
	 */
	private String type;
	
	/** This extra info keeps alive the associated element. */
	private boolean keepsAlive;
	
	/** If not null, specifies the JNDI name of the handler associated with this extra info that needs to be notified about any changes of this element */
	private String notifier;
	
	/** If true states that the element is being shared.  */
	private boolean sharing;

	/** If true (and linked to  a service), shares all facets of the service. */
	private boolean shareAll;

	public ShareDetails() { }
	

	public ShareDetails(long id, String infoType, String elementId,
			String type, boolean keepsAlive, String notifier, boolean sharing,
			boolean shareAll) {
		super();
		this.id = id;
		this.infoType = infoType;
		this.elementId = elementId;
		this.type = type;
		this.keepsAlive = keepsAlive;
		this.notifier = notifier;
		this.sharing = sharing;
		this.shareAll = shareAll;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getInfoType() {
		return infoType;
	}

	public void setInfoType(String infoType) {
		this.infoType = infoType;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isKeepsAlive() {
		return keepsAlive;
	}

	public void setKeepsAlive(boolean keepsAlive) {
		this.keepsAlive = keepsAlive;
	}

	public String getNotifier() {
		return notifier;
	}

	public void setNotifier(String notifier) {
		this.notifier = notifier;
	}

	public boolean isSharing() {
		return sharing;
	}

	public void setSharing(boolean sharing) {
		this.sharing = sharing;
	}

	public boolean isShareAll() {
		return shareAll;
	}

	public void setShareAll(boolean shareAll) {
		this.shareAll = shareAll;
	}

}
