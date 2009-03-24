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

package eu.secse.deliveryManager.notify.core;

import javax.ejb.Local;

import eu.secse.deliveryManager.exceptions.LoginFailedException;
import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.notify.data.Event;

@Local
public interface IQueue {

	/**
	 * This method returns the number of events in the queue of a particular user. The user is identified
	 * through his email and password; if such email is not registered or the password specified is wrong,
	 * a LoginFailedException is thrown
	 * 
	 * @param email
	 * @param password
	 * @return
	 * @throws LoginFailedException
	 */
	public int getQueueLength(String email, String password) throws LoginFailedException; 
	
	/**
	 * This method returns the set of events in the queue (without removing them from the queue) of a 
	 * particular user. The user is identified through his email and password; if such an email is not registered
	 * or the password specified is wrong, a LoginFailedException is thrown. Each event contains a subject,
	 * the content in HTML format, and a read flag. The first time that a user retrieves an event, the 
	 * system shows it as unread; the following times that he retrieves that event it is flagged as read.
	 * 
	 * @param email
	 * @param password
	 * @return
	 * @throws LoginFailedException
	 */
	public Event[] getQueueEvents(String email, String password) throws LoginFailedException;
	
	/**
	 * This method allows the user to retrieve a single message from the queue. The user is identified by his email
	 * and password;  if such an email is not registered or the password specified is wrong, a LoginFailedException 
	 * is thrown. The range of the num parameter is [0, queueLenght) and older messages have lower numbers. if is
	 * specified an invalid event number, a NotFoundException is thrown.
	 * 
	 * @param email
	 * @param password
	 * @param num
	 * @return
	 * @throws LoginFailedException
	 * @throws NotFoundException
	 */
	public Event getQueueEvent(String email, String password, int num) throws LoginFailedException, NotFoundException;
	
	/**
	 * This method allows the user to delete all his read queued events. The user is identified through
	 * his email and password; if such an email is not registered or the password specified is wrong, a LoginFailedException 
	 * is thrown.
	 * 
	 * @param email
	 * @param password
	 * @throws LoginFailedException
	 */
	public void deleteAllReadQueueEvents(String email, String password) throws LoginFailedException;
	
	/**
	 * This method allows a user to delete one of his queued events. The user is identified through
	 * his email and password; if such an email is not registered or the password specified is wrong, a LoginFailedException 
	 * is thrown. The range of the num parameter is [0, queueLenght) and older messages have lower numbers. if is
	 * specified an invalid event number, a NotFoundException is thrown.
	 * 
	 * @param email
	 * @param password
	 * @param num
	 * @throws LoginFailedException
	 * @throws NotFoundException
	 */
	public void deleteQueuedEvent(String email, String password, int num) throws LoginFailedException, NotFoundException;
}
