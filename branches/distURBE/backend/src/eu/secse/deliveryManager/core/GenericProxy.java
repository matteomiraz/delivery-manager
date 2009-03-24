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

import eu.secse.deliveryManager.data.FacetEnt;
import eu.secse.deliveryManager.data.FacetXmlEnt;
import eu.secse.deliveryManager.data.ServiceEnt;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.FacetSpec;
import eu.secse.deliveryManager.model.FacetSpecXML;

public interface GenericProxy {
	
	// ADD: c'è questo elemento che va mandato in giro!!!
	public void add(DService dsrv, ServiceEnt srv);
	public void add(FacetSpec dfacet, FacetEnt facet);
	public void add(FacetSpecXML dXml, FacetXmlEnt xml);

	// DELETE: c'è questo elemento che non va più mandato in giro!
	public void delete(ServiceEnt serv);
	public void delete(FacetEnt serv);
	public void delete(FacetXmlEnt serv);
}
