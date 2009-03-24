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

import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.secse.deliveryManager.exceptions.LoginFailedException;
import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.notify.data.Event;
import eu.secse.deliveryManager.notify.data.NotificationUser;


public class Queue implements IQueue{
	
	private static final Log log = LogFactory.getLog(IQueue.class);
	
	
	@PersistenceContext(unitName="deliveryManager") EntityManager em;

	@SuppressWarnings("unchecked")
	public void deleteAllReadQueueEvents(String email, String password)
			throws LoginFailedException {
		log.debug("deleting all read queue events for user "+email);
		checkPassword(email, password);
		Query query = em.createNamedQuery(Event.READ);
		query.setParameter("email", email);
		ArrayList<Event> events = (ArrayList<Event>)query.getResultList();
		for(Event e : events)
			em.remove(e);
	
	}

	public void deleteQueuedEvent(String email, String password, int num)
			throws LoginFailedException, NotFoundException {
		log.debug("deleting queued event "+num+" for user "+email);
		checkPassword(email, password);
		Query query = em.createNamedQuery(Event.QUEUE_LENGHT);
		query.setParameter("email", email);
		int count = (Integer)query.getSingleResult();
		if(num < 0 || num >= count) throw new NotFoundException("Invalid event number "+num);
		query = em.createNamedQuery(Event.FIND_ALL);
		query.setParameter("email", email);
		Event e =(Event)query.setFirstResult(num).getSingleResult();
		em.remove(e);
	}

	public Event getQueueEvent(String email, String password, int num)
			throws LoginFailedException, NotFoundException {
		log.debug("getting queued event "+num+" for user "+email);
		checkPassword(email, password);
		Query query = em.createNamedQuery(Event.QUEUE_LENGHT);
		query.setParameter("email", email);
		int count = (Integer)query.getSingleResult();
		if(num < 0 || num >= count) throw new NotFoundException("Invalid event number "+num);
		query = em.createNamedQuery(Event.FIND_ALL);
		query.setParameter("email", email);
		return (Event)query.setFirstResult(num).getSingleResult();
		
	}

	@SuppressWarnings("unchecked")
	public Event[] getQueueEvents(String email, String password)
			throws LoginFailedException {
		log.debug("getting all queued events for user "+email);
		checkPassword(email, password);
		Query query = em.createNamedQuery(Event.FIND_ALL);
		query.setParameter("email", email);
		ArrayList<Event> queryResult = (ArrayList<Event>)query.getResultList();
		Event[] results = new Event[queryResult.size()];
		queryResult.toArray(results);
		
		for(Event e: queryResult){
			if(!e.isRead()){
				e.setRead(true);
				em.flush();
			}
		}	
		return results;
	}

	public int getQueueLength(String email, String password)
			throws LoginFailedException {
		log.debug("getting queue lenght for user "+email);
		checkPassword(email, password);
		Query query = em.createNamedQuery(Event.QUEUE_LENGHT);
		query.setParameter("email", email);
		return (Integer)query.getSingleResult();
	
	}

	
	
	/**
	 * Get the user and check if the password is ok
	 * @param email the email of the user
	 * @param password the password
	 * @return the notification user
	 * @throws LoginFailedException the email is not registered or the password is wrong
	 */
	private NotificationUser checkPassword(String email, String password) throws LoginFailedException {
		NotificationUser user = em.find(NotificationUser.class, email);

		if(user == null) 
			throw new LoginFailedException("The email is not registered!");

		if(!user.getPassword().equals(password))
			throw new LoginFailedException("The password not matches!");
		return user;
	}


}
