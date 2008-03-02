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

package eu.secse.reds.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import polimi.reds.Message;


/** This Listener allows waiting for replies too
 * 
 * @author ercasta
 *
 */
public abstract class ARedsReplyListener extends ARedsListener {
	
	private static final Log log = LogFactory.getLog(ARedsReplyListener.class);	
	
	public ARedsReplyListener() {
		super();
	}

	
	@Override
	public void run() {
		this.running = true;
		while (running && ds.isOpened()) {			
			log.info("started");
			
			while(this.running && this.ds.isOpened()) {
				Message msg = this.ds.getNextReply(1000);
				// if received a message
				if(msg != null) {
					log.info("received reply: " + msg);
					onMessage(msg);
				}
				
				msg = this.ds.getNextMessage(1000);
				// if received a message
				if(msg != null) {
					log.info("received: " + msg);
					onMessage(msg);
				}
			}	
			log.info("stopped");
		}
	}
	

}
