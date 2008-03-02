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

import java.util.Collection;

import org.jboss.annotation.ejb.Management;

@Management
public interface IFederationCoordinationManager {
	
	public String getStatus();
	
	public Collection<String> getDirectories();
	
	/** Returns true iff the synchronization process has started (needs at least one endpoint)
	 * 
	 * @return
	 */
	public boolean synchronizeWithDirectory();
	
	
	public void synchronizeWithDirectory(String endpoint);
		
	public void addDirectoryEndpoint(String endpoint);
	
	/** Start discovering directories
	 * 
	 *
	 */
	public void discoverDirectories();
	
	
	public Collection<String> getFederations();
	
	//Life cycle methods
	public void start();
	public void stop();
	
	/** Performs the mapping between symbolic name (es: gossip, ps) to the JNDI lookup name
	 * 
	 * @param proxyname
	 */
	public String getProxyLookupName(String proxyname);
	
	
	public String[] getMethods();
}
