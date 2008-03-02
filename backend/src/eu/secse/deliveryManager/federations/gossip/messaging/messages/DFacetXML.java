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

import java.util.Date;

import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.model.FacetSpecXML;

public class DFacetXML extends FacetSpecXML implements Deliverable {


	private static final long serialVersionUID = -1954023674397812736L;
	private Date schemaTimestamp;
	private String schemaId;
	private String serviceid;
	private String facetschemaid;
	
	public DFacetXML(String serviceid,String facetschemaid, FacetSpecXML xml,Date schematimestamp) {
		super(xml.getXmlID(),xml.getName(),xml.getDocument(),xml.getTimestamp(),xml.getIsoTimestamp());
		this.serviceid=serviceid;
		this.facetschemaid=facetschemaid;
		this.schemaTimestamp=schematimestamp;
	}
	
	public DFacetXML(String serviceid, String facetschemaid,String xmlID, String name, String document, String timestamp, String isoTimestamp,Date schematimestamp) {
		super(xmlID, name, document, timestamp, isoTimestamp);
		this.serviceid=serviceid;
		this.facetschemaid=facetschemaid;
		this.schemaTimestamp=schematimestamp;
	}

	public String getSchemaId() {
		return schemaId;
	}

	public void setSchemaId(String schemaId) {
		this.schemaId = schemaId;
	}

	public Date getSchemaTimestamp() {
		return schemaTimestamp;
	}

	public void setSchemaTimestamp(Date schemaTimestamp) {
		this.schemaTimestamp = schemaTimestamp;
	}

	public String getFacetschemaid() {
		return facetschemaid;
	}

	public void setFacetschemaid(String facetschemaid) {
		this.facetschemaid = facetschemaid;
	}

	public String getServiceid() {
		return serviceid;
	}

	public void setServiceid(String serviceid) {
		this.serviceid = serviceid;
	}

	public String getType() {
		return "GOSSIP";
	}
}
