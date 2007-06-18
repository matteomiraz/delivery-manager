/* This file is part of Delivery Manager.
 * (c) 2006 Matteo Miraz, Politecnico di Milano
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


package eu.secse.deliveryManager.reds;

import javax.ejb.Local;

import eu.secse.deliveryManager.model.Deliverable;


/**
 * Standard interface for interacting with a publish / subscribe framework.
 * In order to integrate a new framework, make a stateless bean that implements this interface 
 * @author matteo
 */
@Local
public interface IRedsProxy {
	public void publish(Deliverable elem);

	public void subscribe(InterestEnvelope interest);
	public void unsubscribe(String nodeIdentifier, long interestID);
	
	/**
	 * Send a wakeup message to keep up tcp links
	 */
	public void sendWakeUp();
}
