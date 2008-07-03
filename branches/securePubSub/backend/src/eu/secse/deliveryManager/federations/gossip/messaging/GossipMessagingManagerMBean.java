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

package eu.secse.deliveryManager.federations.gossip.messaging;

import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;

import eu.secse.deliveryManager.utils.IConfiguration;

@Service(objectName="DeliveryManager:service=GossipMessaging")
public class GossipMessagingManagerMBean implements
		IGossipMessagingManager {
	
	//Fallback values, used when configuration files are wrong
	private static final int GOSSIP_PORT=3335;	
	private static final int C_FALLBACK=2;
	private static final int PARTIAL_FALLBACK=3600*1000*24; //one day	
	
	private static final Log log= LogFactory.getLog(IGossipMessagingManager.class);
	
	@EJB
	private IConfiguration config;
	
	private MessageListener listthread=null;
	
	private String this_url;
	
	//Copy parameter, for gossip
	private int C;
	
	//Timeout parameter, for gossip
	private int PARTIAL_VIEW_LEASE;
	
	public int getC() {
		return C;
	}

	public void start() {		
		startListener();
		String Cstring=config.getString("Gossip.Copyforwarding");
		if (Cstring!=null) {
			try {
				C=Integer.parseInt(Cstring);
			} catch (NumberFormatException nfe) {
				log.warn("Could not parse the Gossip.Copyforwarding parameter. Invalid value: " + Cstring);
				C=C_FALLBACK;				
			}						
		} else {
			log.warn("Could not parse the Gossip.Copyforwarding parameter. Invalid value: " + Cstring);
			C=C_FALLBACK;
		}
		String Pstring=config.getString("Gossip.partial_expire");		
		if (Pstring!=null) {
			try {
				PARTIAL_VIEW_LEASE=Integer.parseInt(Pstring);
			} catch (NumberFormatException nfe) {
				log.warn("Could not parse the Gossip.partial_expire parameter. Invalid value: " + Pstring);
				PARTIAL_VIEW_LEASE=PARTIAL_FALLBACK;		
			}						
		} else {
			log.warn("Could not parse the Gossip.partial_expire parameter. Invalid value: " + Pstring);
			PARTIAL_VIEW_LEASE=PARTIAL_FALLBACK;
		}	
		
	}
	
	

	public void stopListener() {
		if (listthread!=null) {
			listthread.stopListening();	
			try {
				listthread.join();
			} catch (InterruptedException e) {
				log.warn("Could not wait for listening thread to stop");
			}
			listthread=null;
		}
	}
	
	public void startListener() {
		if (listthread!=null) {
			listthread.stopListening();
		}
		String confport=config.getString("Gossip.Port");
		int cfgport=GOSSIP_PORT;
		try {
			cfgport=Integer.parseInt(confport);
			listthread=new MessageListener(cfgport);
		} catch (NumberFormatException nfe) {			
			log.warn("Could not read port from configuration file. Property Gossip.Port is either missing or invalid. Using port " + GOSSIP_PORT);
			listthread=new MessageListener(cfgport);	
		}		
		String address=config.getString("Gossip.URL");
		if (address==null) {
			log.error("Could not read public address from configuration file. Aborting");
			return;
		}
		this_url=address+":"+cfgport;
		listthread.start();		
	}
	
	public void stop() {
		stopListener();
	}

	public String getAddress() {
		return this_url;
	}

	public int getPartialViewLeaseTimeout() {
		return PARTIAL_VIEW_LEASE;
	}
	

}
