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

package eu.secse.deliveryManager.federations.gossip.messaging.messages;

public class FacetHeader extends PromotionHeader {

	
	private static final long serialVersionUID = -5253831755031310797L;
	
	private long timestamp;
	private String facetid;
	private boolean attachXML;
	private boolean addInfo;
	

	public FacetHeader(String serviceid, String facetid, long timestamp, boolean addInfo,long promotiontimestamp) {
		super(serviceid,promotiontimestamp);
		this.facetid = facetid;
		this.timestamp = timestamp;
		this.attachXML=true;
		this.addInfo=addInfo;		
	}

	public String getFacetid() {
		return facetid;
	}

	public long getTimestamp() {
		return timestamp;
	}

	/** Only used for requests. It is ignored in promotions
	 * 
	 * @return
	 */
	public boolean isAttachXML() {
		return attachXML;
	}

	public void setAttachXML(boolean attachXML) {
		this.attachXML = attachXML;
	}

	public boolean isAddInfo() {
		return addInfo;
	}

	public void setAddInfo(boolean addInfo) {
		this.addInfo = addInfo;
	}
	
	@Override
	public String toString() {
		return "facet header (serviceid " + serviceid + ", facetid" + facetid + ")"; 
	}

}
