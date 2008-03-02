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


import javax.ejb.Local;
import javax.ejb.TimedObject;

import eu.secse.federationDirectory.db.FederationData;


@Local
public interface IDirectoryModelManager extends TimedObject{
	
	/** Returns a list of existing federations. If no federation exists, returns null 
	 * 
	 * @return
	 */
	public FederationData[] getAllFederations() ;
	
	/** Returns a list of federations whose name matches the given regular expression.
	 * If no federation
	 * TODO: define how regular expressions are interpreted.
	 * @param nameRegEx
	 * @return
	 */
	public FederationData[] searchFederationByName(String nameRegEx) ;
	
	
	/** Returns the data of the federation having the specified id. Returns null if there is no federation
	 * with the given id.  
	 * 
	 * @param uid
	 * @return
	 */
	public FederationData searchFederationByUid(String uid) ;
	
	/** Adds a federation to the list of existing federation. If a federation with the same uid already exists,
	 * its data are updated.
	 * 
	 * @param fed
	 */
	public void addFederationData(FederationData fed);
	
	public void removeAllFederations();
	
	public void removeFederation(String federationid);
	
	public void startLeaseTimer();
	public void stopLeaseTimer();
}
