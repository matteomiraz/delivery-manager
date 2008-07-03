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

package eu.secse.deliveryManager.federations.data;

import java.util.Collection;
import java.util.Vector;

import javax.ejb.Stateless;

import eu.secse.federationDirectory.reds.types.RedsFederationData;
import eu.secse.federationDirectory.reds.types.RedsFederationProperty;

@Stateless
public class DmFederationDataConverter implements
		IDmFederationDataConverter {

	public RedsFederationData convert(Federation f) {		
		if (f==null) return null;
		RedsFederationData d=new RedsFederationData(f.getId());
		d.setName(f.getName());
		d.setMethod(f.getMethod());
		d.setLeaseExpiration(f.getLease());
		Collection<RedsFederationProperty> properties=new Vector<RedsFederationProperty>();
		for (FederationProperty fp: f.getProperties() ) {
			RedsFederationProperty rp=new RedsFederationProperty(fp.getName(),fp.getValue());
			properties.add(rp);
		}
		d.setProperties(properties);
		return d;
	}


	public Federation convert(RedsFederationData rf) {
		if (rf==null) return null;
		Federation d=new Federation();
		d.setId(rf.getId().getId());
		d.setName(rf.getName());
		d.setMethod(rf.getMethod());
		d.setLease(rf.getLeaseExpiration());
		Collection<FederationProperty> properties=new Vector<FederationProperty>();
		for (RedsFederationProperty fp: rf.getProperties() ) {
			FederationProperty rp=new FederationProperty(fp.getName(),fp.getValue());
			properties.add(rp);
		}
		d.setProperties(properties);
		return d;
	}

}
