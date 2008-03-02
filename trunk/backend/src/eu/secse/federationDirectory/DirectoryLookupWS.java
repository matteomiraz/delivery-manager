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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import eu.secse.federationDirectory.adapters.IFederationDataConverter;
import eu.secse.federationDirectory.db.FederationData;
import eu.secse.federationDirectory.wstypes.WSFederationData;
import eu.secse.federationDirectory.wstypes.WSFederationDataArray;

@Stateless
@WebService(name = "IDirectoryLookupWS", serviceName = "FederationDirectory", targetNamespace = "http://secse.eu/federationDirectory/webservice")
@SOAPBinding(style=SOAPBinding.Style.RPC, use=SOAPBinding.Use.LITERAL, parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
public class DirectoryLookupWS implements IDirectoryLookupWS{

	@EJB IDirectoryModelManager dirlookup;
	@EJB IFederationDataConverter converter;
	
	@WebMethod
	public WSFederationDataArray getAllFederations() {
		FederationData[] data=dirlookup.getAllFederations();
		return converter.convert(data);
	}
	
	@WebMethod
	public WSFederationDataArray searchFederationByName(String nameRegEx) {
		FederationData[] data=dirlookup.searchFederationByName(nameRegEx);
		return converter.convert(data);
	}

	@WebMethod
	public WSFederationData searchFederationByUid(String uid) {
		FederationData data= dirlookup.searchFederationByUid(uid);
		if (data!=null) {
			return converter.convertWSData(data);
		}
		return null;
	}

}
