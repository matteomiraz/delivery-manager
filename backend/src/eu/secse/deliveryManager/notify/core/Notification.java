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

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.secse.deliveryManager.exceptions.LoginFailedException;
import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.notify.data.Interest;
import eu.secse.deliveryManager.notify.data.NotificationInterest;
import eu.secse.deliveryManager.notify.data.NotificationUser;

@Stateless
@WebService
public class Notification implements INotification{
	private static final Log log = LogFactory.getLog(INotification.class);

	@PersistenceContext(unitName="deliveryManager") EntityManager em;

	@EJB IMailer mailer;

	@EJB IRandomGenerator random;

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

	@WebMethod
	public void changePassword(@WebParam(name="email") String email, @WebParam(name="oldPassword") String oldPassword,
			@WebParam(name="newPassword") String newPassword) throws LoginFailedException {
		NotificationUser user = checkPassword(email, oldPassword);

		user.setPassword(newPassword);
		em.flush();

	}

	@WebMethod
	public void registerNotificationUser(@WebParam(name="email") String email) {
		try {
			resendPassword(email);
			return;
		} catch (LoginFailedException e) {
			log.info("The email is not registered... registering a new email!");
			NotificationUser user = new NotificationUser(email, random.generateRandom());
			em.persist(user);
			mailer.send(email, "Password for the notification service", "Your password is " + user.getPassword());

		}	
	}

	@WebMethod
	public void resendPassword(@WebParam(name="email") String email) throws LoginFailedException {
		NotificationUser user = em.find(NotificationUser.class, email);

		if(user == null) 
			throw new LoginFailedException("The email is not registered!");

		mailer.send(email, "Password for the notification service", "Your password is " + user.getPassword());

	}

	@WebMethod
	public void unregisterNotificationUser(@WebParam(name="email") String email, @WebParam(name="password") String password)
	throws LoginFailedException {
		NotificationUser user = checkPassword(email, password);
		em.remove(user);
		mailer.send(email, "Notification service", "Your account has been unregistered");

	}

	@WebMethod
	public Interest[] getAllInterests(@WebParam(name="email")String email, @WebParam(name="password") String password)
	throws LoginFailedException {
		NotificationUser user = checkPassword(email, password);
		int i = 0;
		Collection<NotificationInterest> interests = user.getInterests();
		Interest[] ret = new Interest[interests.size()];
		for (NotificationInterest interest : interests)
			ret[i++] = new Interest(interest);
		return ret;
	}

	@WebMethod
	public Interest getAllInterestsById(@WebParam(name="email")String email, @WebParam(name="password") String password,
			@WebParam(name="interestId") int interestId) throws LoginFailedException, NotFoundException {
		NotificationUser user = checkPassword(email, password);
		for(NotificationInterest notificationInterest: user.getInterests()){
			if(notificationInterest.getId() == interestId)
				return new Interest(notificationInterest);
		}
		throw new NotFoundException("Interest "+ interestId+" not found for user "+email);
	}

	@WebMethod
	public int registerNotification(@WebParam(name="email")String email, @WebParam(name="password")String password,
			@WebParam(name="serviceId")String serviceId,
			@WebParam(name="facetXpathConstraints") String facetXpathConstraints, 
			@WebParam(name="baseServiceId")String baseServiceId,
			@WebParam(name="serviceNameRegularExpression")String serviceNameRegEx, 
			@WebParam(name="federationName")String federationName,/*
			@WebParam(name="notifyService") boolean notifyService, 
			@WebParam(name="notifyFacetSpecificationSchema") boolean notifyFacetSpecificationSchema,
			@WebParam(name="notifyFacetSpecificationXml") boolean notifyFacetSpecificationXml,*/ 
			@WebParam(name = "deliveryMethod")String deliveryMethod)
	throws LoginFailedException, NotFoundException {
		NotificationUser user = checkPassword(email, password);

		if(serviceId!=null && serviceId.length() <= 0) serviceId = null;
		if(baseServiceId!=null && baseServiceId.length() <= 0) baseServiceId = null;
		if(serviceNameRegEx!=null && serviceNameRegEx.length() <= 0) serviceNameRegEx = null;
		if(federationName !=null && federationName.length() <=0 ) federationName = null;
		//if(facetXpathConstraints.length==0) facetXpathConstraints= null;
		if(serviceId == null && facetXpathConstraints == null && baseServiceId == null && serviceNameRegEx == null && federationName == null){
			throw new NotFoundException("Impossible register notification for user "+email+ ": all fields are null");
			
		}
		if(deliveryMethod !=null && deliveryMethod.length()<=0) deliveryMethod = null;
		if(deliveryMethod == null)
			throw new NotFoundException("The delivery method is not specified");
		
		NotificationInterest interest = new NotificationInterest(user, serviceId, facetXpathConstraints, baseServiceId, serviceNameRegEx,federationName, /*notifyService, notifyFacetSpecificationSchema, notifyFacetSpecificationXml,*/ deliveryMethod);
		em.persist(interest);
		
		return interest.getId();
	}

	public void unregisterNotification(String email, String password,
			int notificationId) throws LoginFailedException, NotFoundException {
		
		NotificationUser user = checkPassword(email, password);
		NotificationInterest interest = em.find(NotificationInterest.class, notificationId);
		
		if(interest == null)
			throw new NotFoundException("The notification id is not found");

		if(!user.equals(interest.getUser()))
			throw new NotFoundException("The notification id is not your!");

		em.remove(interest);

	}

}



