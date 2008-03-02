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

import javax.ejb.Remote;

import eu.secse.deliveryManager.exceptions.LoginFailedException;
import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.notify.data.Interest;

@Remote
public interface INotification {

	/**
	 * This method allows the notification system to receive the notification of a user identified by
	 * its email address. The system then generates a random password sent to the user in an email message.
	 * If the user is already registered in the system, the system resends the password anyway
	 * 
	 * @param email
	 */
	public void registerNotificationUser(String email);

	/**
	 * This method resends the email containing the password of the user identified by the email address.
	 * If the user is not registered, a LoginFailed exception is thrown.
	 * 
	 * @param email
	 * @throws LoginFailedException
	 */
	public void resendPassword(String email) throws LoginFailedException;

	/**
	 * This method changes the password of a user. He has to specify his email, his old password and his
	 * new password. If the user is not registered, or if his oldPassword does not match the current password,
	 * a LoginFailedException is thrown. 
	 * 
	 * @param email
	 * @param oldPassword
	 * @param newPassword
	 * @throws LoginFailedException
	 */
	public void changePassword(String email, String oldPassword, String newPassword) throws LoginFailedException;

	
	/**
	 * This method unregisters a user from the notification system, discarding automatically all the
	 * interests. If the user does not exist or a wrong password is specified, a LoginFaildException is
	 * thrown.
	 * 
	 * @param email
	 * @param password
	 * @throws LoginFailedException
	 */
	public void unregisterNotificationUser(String email, String password) throws LoginFailedException;
	
	/**
	 * This method allows the user to declare the interest on some kind of events. The user is identified using his email and
	 * password. If the user is not registered or the password is wrong, a LoginFailedException is thrown. In order to specify 
	 * the interesting events, the user can specify serviceId, facetXpathConstraints, baseServiceId, serviceNameRegEx and federationName.
	 * Even if all those parameters are optional (a null value indicates a wildcard), it is required that at least one is
	 * specified. If the user already knows the id of the service, he can specify it using serviceId paramenter. if he wants
	 * to specify some constraints for the XML content of facets, he can use the FacetXpathConstraints. The graphical user interface
	 * provide some templates for these constraints (such as the one that selects the services provided by a particular organization).
	 * If the user wants to receive a notification when new versions of a base service are received, he can specify the baseServiceId
	 * parameter. In some situations, the user wants to receive all the services that match a regular expression: in this case he can use
	 * the serviceNameRegEx parameter. If he needs to check if a service is promoted in a particular joined federation, he can specify 
	 * the federationName. The parameters notifyService, notifyFacetSpecificationSchema and notifyFacetSpecificationXml allow the user
	 * to specify if he wants to be notified respectively when the service, the facet specification schema or the facet specification xml
	 * document is inserted or removed from the registry. The deliveryMethod parameter allows the user to select the way ho wants to receive
	 * the notification. The current implementation accepts either "email" or "queue" as valid values.
	 * The method returns the numeric identifier of the interest.
	 * 
	 * 
	 * 
	 * @param email
	 * @param password
	 * @param serviceId
	 * @param facetXpathConstraints
	 * @param baseServiceId
	 * @param serviceNameRegEx
	 * @param federationName
	 * @param deliveryMethod
	 * @return
	 * @throws LoginFailedException
	 * @throws NotFoundException
	 */
	public int registerNotification(String email, String password, String serviceId, String facetXpathConstraints, String baseServiceId, String serviceNameRegEx, String federationName,/* boolean notifyService, boolean notifyFacetSpecificationSchema, boolean notifyFacetSpecificationXml,*/ String deliveryMethod) throws LoginFailedException, NotFoundException;

	/**
	 * This method returns the set of interests specified by a user, identified by his email and password.
	 * If the user is not registered or the password is wrong, a LoginFailedException is thrown.
	 * 
	 * @param email
	 * @param password
	 * @return
	 * @throws LoginFailedException
	 */
	public Interest[] getAllInterests(String email, String password) throws LoginFailedException;

	/**
	 * This method returns a particular interest identified by its id. The user is identified through his
	 * email and password. If the user is not registered or the password is wrong, a LoginFailedException is
	 * thrown. The interestId is the numeric identifier of the interest. if the user doesn't have an interest
	 * with such an id, a NotFoundException is thrown, otherwise the method returns the specified interest.
	 * 
	 * @param email
	 * @param password
	 * @param interestId
	 * @return
	 * @throws LoginFailedException
	 * @throws NotFoundException
	 */
	public Interest getAllInterestsById(String email, String password, int interestId) throws LoginFailedException, NotFoundException;
	
	/**
	 * This method allows us to dismiss an interest of a user, identified through his email and password
	 * If the user is not registered or the password is wrong, a LoginFailedException is thrown. The interest
	 * is selected using the parameter interestId. If the user doesn't have such an interest, a NotFoundException
	 * is thrown; otherwise the method discard the interest.
	 * 
	 * @param email
	 * @param password
	 * @param notificationId
	 * @throws LoginFailedException
	 * @throws NotFoundException
	 */
	public void unregisterNotification(String email, String password, int notificationId) throws LoginFailedException, NotFoundException;
}
