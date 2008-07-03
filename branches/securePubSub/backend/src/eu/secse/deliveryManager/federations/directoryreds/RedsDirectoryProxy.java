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

package eu.secse.deliveryManager.federations.directoryreds;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import polimi.reds.LocalDispatchingService;
import polimi.reds.Message;
import eu.secse.deliveryManager.core.FederationCoordinationMBean;
import eu.secse.reds.core.IRedsConnector;
import eu.secse.reds.exceptions.NotFoundException;

@Stateless
public class RedsDirectoryProxy implements IRedsDirectoryProxy{

	private static final Log log;
	
	static {
		log = LogFactory.getLog(IRedsDirectoryProxy.class);		
	}
	
	@EJB
	private IRedsConnector connector;
	
	public void send(Message m) {
		try {
			LocalDispatchingService serv=connector.getDispatcher(FederationCoordinationMBean.REDSID);
			serv.publish(m);
		} catch (NotFoundException e) {
			log.debug("Could not obtain dispatcher with id " +FederationCoordinationMBean.REDSID);
		}		
		
	}

}
