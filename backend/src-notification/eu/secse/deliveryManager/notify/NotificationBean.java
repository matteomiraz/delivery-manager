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


package eu.secse.deliveryManager.notify;

import java.util.Collection;

import javax.annotation.EJB;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;

import eu.secse.deliveryManager.notify.data.NotificationInterest;
import eu.secse.deliveryManager.notify.data.NotificationUser;

@Stateless 
@WebService
public class NotificationBean implements Notification {

	Logger log = Logger.getLogger(Notification.class);
	
	@PersistenceContext(unitName="notification") EntityManager em;
	
	@EJB RandomGenerator random;
	@EJB Mailer mailer;
	
	@WebMethod
	public void registerNotificationUser(
			@WebParam(name="email") String email) {
		try {
			resendPassword(email);
			return;
		} catch (LoginFailedException e) {
			// The email is not registered... registering a new email!
		}
		
		NotificationUser user = new NotificationUser(email, random.generateRandom());
		em.persist(user);
		mailer.send(email, "Password for the notification service", "Your password is " + user.getPassword());
	}
	
	@WebMethod
	public void resendPassword(
			@WebParam(name="email") String email) throws LoginFailedException {
		NotificationUser user = em.find(NotificationUser.class, email);
		
		if(user == null) 
			throw new LoginFailedException("The email is not registered!");

		mailer.send(email, "Password for the notification service", "Your password is " + user.getPassword());
	}
	
	@WebMethod
	public void changePassword(
			@WebParam(name="email") String email,
			@WebParam(name="oldPassword") String oldPassword,
			@WebParam(name="newPassword") String newPassword) throws LoginFailedException {
		NotificationUser user = checkPassword(email, oldPassword);

		user.setPassword(newPassword);
	}
	
	@WebMethod
	public void unregisterNotificationUser(
			@WebParam(name="email") String email,
			@WebParam(name="password") String password) throws LoginFailedException {
		NotificationUser user = checkPassword(email, password);
		em.remove(user);
		mailer.send(email, "Notification service", "Your account has been unregistered");
	}

	@WebMethod
	public int registerNotification(
			@WebParam(name="email") String email, 
			@WebParam(name="password") String password,
			@WebParam(name="providerName") String providerName,
			@WebParam(name="serviceId") String serviceId,
			@WebParam(name="baseServiceId") String baseServiceId,
			@WebParam(name="serviceNameRegex") String serviceNameRegEx,
			@WebParam(name="notifyService") boolean notifyService,
			@WebParam(name="notifyFacetSchema") boolean notifyFacetSpecificationSchema,
			@WebParam(name="notifyFacetXml") boolean notifyFacetSpecificationXml) throws LoginFailedException {
		NotificationUser user = checkPassword(email, password);

		// an empty string is null... (for some buggy soap clients) 
		if(providerName!= null && providerName.length() <= 0) providerName = null;
		if(serviceId!=null && serviceId.length() <= 0) serviceId = null;
		if(baseServiceId!=null && baseServiceId.length() <= 0) baseServiceId = null;
		if(serviceNameRegEx!=null && serviceNameRegEx.length() <= 0) serviceNameRegEx = null;

		NotificationInterest interest = new NotificationInterest(user, providerName, serviceId, baseServiceId, serviceNameRegEx, notifyService, notifyFacetSpecificationSchema, notifyFacetSpecificationXml);
		em.persist(interest);
		
		return interest.getId();
	}

	@WebMethod
	public Interest[] getAllInterests(
			@WebParam(name="email") String email,
			@WebParam(name="password") String password) throws LoginFailedException {
		NotificationUser user = checkPassword(email, password);
		
		int i = 0;
		Collection<NotificationInterest> interests = user.getInterests();
		Interest[] ret = new Interest[interests.size()];
		for (NotificationInterest interest : interests)
			ret[i++] = new Interest(interest);
		
		return ret;
	}

	@WebMethod
	public void unregisterNotification(
			@WebParam(name="email") String email,
			@WebParam(name="password") String password,
			@WebParam(name="notificationID") int notificationId) throws LoginFailedException, NotFoundException {
		NotificationUser user = checkPassword(email, password);
		NotificationInterest interest = em.find(NotificationInterest.class, notificationId);
		
		if(interest == null)
			throw new NotFoundException("The notification id is not found");

		if(!user.equals(interest.getUser()))
			throw new NotFoundException("The notification id is not your!");

		em.remove(interest);
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
