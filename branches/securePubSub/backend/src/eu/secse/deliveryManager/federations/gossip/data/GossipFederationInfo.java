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
import java.util.Map;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import eu.secse.deliveryManager.data.FederationEnt;
import eu.secse.deliveryManager.data.FederationExtraInfo;

@Entity
public class GossipFederationInfo extends FederationExtraInfo {
			
	private String initialcontact;

	@OneToMany(mappedBy="federationinfo",cascade={CascadeType.ALL})
	private Collection<DeliveryManagerGossipInfo> dminfo;

	//True iff i am the coordinator of the federation
	private boolean coordinator;
	
	//See the FederationSubscriptionStatus class 
	private String status;
	
	//Is true when the unic federation member is its owner
	private boolean coordinator_alone;
	
	 GossipFederationInfo() {
		super();		
	}
	
	
	/** Valid option keys are: 
	 * 
	 * "initialcontact": complete address of the initial contact
	 * 
	 * @param fed
	 * @param options
	 */
	public GossipFederationInfo(FederationEnt fed, Map<String,String> options) {
		if (fed.isOwnership()) {
			setCoordinator(true);
		} else {
			setCoordinator(false);
		}
		dminfo=new Vector<DeliveryManagerGossipInfo>();		
		setInitialcontact(options.get("initialcontact"));		
		coordinator_alone=false;			
	}


	public String getInitialcontact() {
		return initialcontact;
	}

	public void setInitialcontact(String initialcontact) {
		this.initialcontact = initialcontact;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isCoordinator() {
		return coordinator;
	}

	public void setCoordinator(boolean coordinator) {
		this.coordinator = coordinator;
	}
	
	
	public Collection<DeliveryManagerGossipInfo> getDminfo() {
		return dminfo;
	}


	public boolean isCoordinator_alone() {
		return coordinator_alone;
	}


	public void setCoordinator_alone(boolean coordinator_alone) {
		this.coordinator_alone = coordinator_alone;
	}
	
//	public Collection<DeliveryManagerGossipInfo> getPartialView() {
//		Vector<DeliveryManagerGossipInfo> partial=new Vector<DeliveryManagerGossipInfo>();
//		for (DeliveryManagerGossipInfo ginfo:dminfo) {
//			if (ginfo.isPartialview()) {
//				partial.add(ginfo);
//			}
//		}
//		return partial;
//	}
//	
//	public Collection<DeliveryManagerGossipInfo> getInView() {
//		Vector<DeliveryManagerGossipInfo> partial=new Vector<DeliveryManagerGossipInfo>();
//		for (DeliveryManagerGossipInfo ginfo:dminfo) {
//			if (ginfo.isInview()) {
//				partial.add(ginfo);
//			}
//		}
//		return partial;
//	}
//	
//	public Collection<DeliveryManagerGossipInfo> getLiveInView() {
//		Vector<DeliveryManagerGossipInfo> partial=new Vector<DeliveryManagerGossipInfo>();
//		for (DeliveryManagerGossipInfo ginfo:dminfo) {
//			if (ginfo.isLiveinview()) {
//				partial.add(ginfo);
//			}
//		}
//		return partial;
//	} 


}
