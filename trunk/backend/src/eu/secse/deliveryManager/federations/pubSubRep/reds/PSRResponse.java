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

package eu.secse.deliveryManager.federations.pubSubRep.reds;

import java.util.ArrayList;
import java.util.Collection;

import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.model.FacetAddInfo;

/**
 * This class contains services promoted in the federation
 * @author matteo
 */
public class PSRResponse implements Deliverable {
	private static final long serialVersionUID = 5250314692729713319L;

	private Collection<Deliverable> elems;
	
	public PSRResponse() {
		elems = new ArrayList<Deliverable>();
	}
	
	public void add(DService srv) {
		elems.add(srv);
	}
	
	public void addServices(Collection<DService> srv) {
		elems.addAll(srv);
	}
	
	public void add(FacetAddInfo facet) {
		elems.add(facet);
	}
	
	public void addFacets(Collection<FacetAddInfo> facet) {
		elems.addAll(facet);
	}
	
	public Collection<Deliverable> getElems() {
		return elems;
	}
	
	public String getType() {
		return "PSR-RESP";
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("PsrResp-containing").append(elems.size()).append(" elements:{");
		for (Deliverable d : elems) {
			if(d instanceof DService) sb.append("srv:").append(((DService)d).getServiceID()).append(" ");
			else if(d instanceof FacetAddInfo) sb.append("facet:").append(((FacetAddInfo)d).getSchemaID()).append(" ");
			else sb.append("?").append(d.getClass().getCanonicalName()).append("# ");
		}
		
		return sb.append("}").toString();
	}
}
