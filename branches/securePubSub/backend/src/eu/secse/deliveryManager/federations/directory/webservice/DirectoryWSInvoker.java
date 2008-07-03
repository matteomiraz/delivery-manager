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

package eu.secse.deliveryManager.federations.directory.webservice;

import java.util.Collection;
import java.util.Vector;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import eu.secse.deliveryManager.federations.data.Federation;
import eu.secse.deliveryManager.federations.data.IDmFederationDataConverter;
import eu.secse.federationDirectory.reds.types.RedsFederationData;
import eu.secse.federationDirectory.wsclient.IDirectoryProxy;

@Stateless
public class DirectoryWSInvoker implements IDirectoryWSInvoker{
	
	@EJB
	private IDirectoryProxy wsproxy;

	@EJB
	private IDmFederationDataConverter converter;
	
	public Collection<Federation> getFederations(String endpoint) {
		Collection<RedsFederationData> data=wsproxy.getAllFederations(endpoint);
		Collection<Federation> f=new Vector<Federation>();
		if(data!=null){
		for (RedsFederationData rdata:data) {
			f.add(converter.convert(rdata));
		}}
		return f;
	}

	public Federation getById(String endpoint, String id) {
		RedsFederationData data=wsproxy.searchFederationByUid(id,endpoint);
		return converter.convert(data);
	}

	public Collection<Federation> getByName(String endpoint, String nameexpression) {
		Collection<RedsFederationData> data=wsproxy.searchFederationByName(nameexpression,endpoint);
		Collection<Federation> f=new Vector<Federation>();
		for (RedsFederationData rdata:data) {
			f.add(converter.convert(rdata));
		}
		return f;
	}

}
