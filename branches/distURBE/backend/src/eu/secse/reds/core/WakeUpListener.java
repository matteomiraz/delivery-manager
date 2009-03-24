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

public class WakeUpListener extends ARedsListener {
	
	private static final Log log = LogFactory.getLog(WakeUpListener.class);	
	private long lastMessage;

	public WakeUpListener() {
		reset();
	}

	@Override
	public void onMessage(Message msg) {		
		log.debug("Stayin' Alive!");
		lastMessage = System.currentTimeMillis();
	}

	public long getLastMessage() {
		return lastMessage;
	}
	
	public void reset() {
		lastMessage = System.currentTimeMillis();
	}
}
