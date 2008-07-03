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

package eu.secse.federationDirectory;

import org.jboss.annotation.ejb.Management;

@Management
public interface IDirectoryMBean {
	
	public String[] getFederationList();
	
	public void addFederation(String uid);
	
	public String[] getDirectoryLists();
	
	/** Returns true if the Federation Directory has to synchronize with other instances of the directory.
	 * Synchronization happens at startup. This method returns false only if this instance of directory 
	 * has already succesfully obtained a list of existing federations from other 
	 * 
	 * @return
	 */
	public boolean mustSynchronize();
	
	public void setSynchronized(boolean sync);
	
	public void synchronizeWithOtherDirectory(String endpointurl);
	
	public void addOtherDirectoryEndpoint(String endpoint);
	
	public void discoverDirectories();
	
	// Life cycle method
	public void start();
	public void stop();
	
}
