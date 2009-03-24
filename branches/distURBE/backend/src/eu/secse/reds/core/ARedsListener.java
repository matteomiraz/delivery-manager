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

import polimi.reds.DispatchingService;
import polimi.reds.Message;


public abstract class ARedsListener extends Thread {
	
	private static final Log log = LogFactory.getLog(ARedsListener.class);
	
	protected boolean running;
	protected DispatchingService ds;
	
	/** Callback method 
	 * 
	 * @param msg
	 */
	public abstract void onMessage(Message msg);
	
	public ARedsListener() {
		
	}
	
	public void setDispatcher(DispatchingService ds) {
		this.ds=ds;
	}
	
	@Override
	public void run() {
		
		this.running = true;
		log.info("started");
		
		while(this.running && this.ds.isOpened()) {
			Message msg = this.ds.getNextMessage(1000);
			// if received a message
			if(msg != null) {
				log.debug("received: " + msg);
				onMessage(msg);
			}
		}	
		log.info("stopped");
	
	}
	
	public void stopListening() {
		log.debug("Shutting down reds listener");
		this.running = false;

		try {
			this.join();
		} catch (InterruptedException e) {
			log.warn("Reds listener brutally stopped");
		}
	}
}
