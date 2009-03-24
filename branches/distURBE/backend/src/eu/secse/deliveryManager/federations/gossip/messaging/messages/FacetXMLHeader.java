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

public class FacetXMLHeader extends PromotionHeader {
	
	private static final long serialVersionUID = 2039814742602805317L;
	
	private String xmlid;
	private String facetid;
	private boolean addInfo;
	private long facetTimestamp;
	
	public FacetXMLHeader(String serviceid, String facetid, String xmlid,long timestamp,boolean isAddInfo,long facetTimestamp) {
		super(serviceid,timestamp);
		this.facetid = facetid;
		this.xmlid = xmlid;
		this.addInfo=isAddInfo;
		this.facetTimestamp=facetTimestamp;
	}
	
	public String getFacetid() {
		return facetid;
	}
	public String getXmlid() {
		return xmlid;
	}

	public boolean isAddInfo() {
		return addInfo;
	}

	public void setAddInfo(boolean addInfo) {
		this.addInfo = addInfo;
	}

	public long getFacetTimestamp() {
		return facetTimestamp;
	}

	public void setFacetTimestamp(long facetTimestamp) {
		this.facetTimestamp = facetTimestamp;
	}
	
	@Override
	public String toString() {
		return "xml header (serviceid " + serviceid + ", facetid" + facetid + ", xmlid" +  xmlid + ")"; 
	}
	
}
