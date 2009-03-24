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

import javax.management.MBeanServer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.mx.util.MBeanProxyExt;
import org.jboss.mx.util.MBeanServerLocator;

import polimi.reds.Message;
import eu.secse.deliveryManager.core.IFederationCoordinationManager;
import eu.secse.federationDirectory.reds.messages.EndpointInfo;
import eu.secse.reds.core.ARedsReplyListener;

public class RedsDirectoryListener extends
		ARedsReplyListener {

	private static final Log log = LogFactory.getLog(RedsDirectoryListener.class);
	
	private IFederationCoordinationManager coordinator;
	
	public RedsDirectoryListener() {
		try {
	        MBeanServer server = MBeanServerLocator.locate();
	        this.coordinator= (IFederationCoordinationManager) MBeanProxyExt.create(IFederationCoordinationManager.class, "DeliveryManager:service=federationCoordinator", server);
	    } catch (Exception e) {
	        log.error(e.getMessage());	       
	    }
	}
	
	@Override
	public void onMessage(Message msg) {
		if (msg instanceof EndpointInfo) {
			EndpointInfo ei=(EndpointInfo)msg;
			String endpoint=ei.getEndpoint();
			coordinator.addDirectoryEndpoint(endpoint);
		}
	}

}
