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

package eu.secse.deliveryManager.core;

import javax.ejb.Remote;

import eu.secse.deliveryManager.exceptions.NotFoundException;
import eu.secse.deliveryManager.exceptions.NotSubscribedException;
import eu.secse.deliveryManager.webServices.GenericInterest;

@Remote
public interface IDeliveryManager {

	/**
	 * This method returns the collection with the id of all registered interests.
	 * @return the set of all interests' id
	 */
	public long[] getAllInterestIds();

	/**
	 * This method allows to get more information about an interest.
	 * @param id the id of the interest
	 * @return the details of the selected interest 
	 * @throws NotSubscribedException if the specified interest cannot be found.
	 */
	public GenericInterest getInterestById(long id) throws NotSubscribedException;

	/**
	 * This method allows to declare an interest on some additional information facet.
	 * The interest is higly configurable, allowing the user to specify different tipology of filters.
	 * Note that, even if each parameter is optional, it <b>is required that at least one is not null</b>.<br>
	 * List of possible uses and their meaning:
	 * <ul>
	 *   <li><b>retrive all facet of a given service</b>: specify only the serviceId;
	 *   <li><b>retrive all facet with a particular schema</b>: specify only the facetSchema;
	 *   <li><b>retrive all facet with an xml that has some properties</b>: specify only the xpath;
	 *   <li><b>retrive all facet with a particular xsd of a given service</b>: specify both the serviceId and the facetSchema;
	 *   <li><b>retrive all facet of a given service with an xml that has some properties</b>: specify both the serviceId and the xpath;
	 *   <li><b>retrive all facet with a particular xsd and an xml that has some properties,</b>: specify both the facetSchema and the xpath;
	 *   <li><b>retrive all facet whose xml has some properties, with a particular xsd of a given service</b>: specify all parameters;
	 * </ul>
	 * @param serviceId the id of the service that the facet must be related to.
	 * @param facetSchema the schema document
	 * @param xpath the xpath
	 * @return the interest id
	 * @throws NotSubscribedException
	 */
	
	public long subscribeAdditionalInformationFacet(String serviceId, String facetSchema, String xpath, String description) throws NotSubscribedException;
	/**
	 * This method allows to retrive a particular facet additional information, whose id is known.
	 * It is possible to specify the related serviceId, in order to speed up the retrival of the interested
	 * informations.
	 * @param serviceId the service id <b>(optional)</b>
	 * @param facetSchemaId the facet schema identifier
	 * @return the interest id
	 * @throws NotSubscribedException 
	 */
	public long subscribeAdditionalInformationFacetById(String serviceId, String facetSchemaId, String description) throws NotSubscribedException;

	/**
	 *  This method allows declaring an interest on a particular service,
	 * identified by its id. After an invocation to this method, all service
	 * specification facets are forwarded to the local registry. This method
	 * returns the subscribe identification, that can be used for unsubscribe to
	 * the created interest.
	 * 
	 * @param serviceId
	 * @param description
	 * @return subscription id
	 */
	public long subscribeService(String serviceId, String description);

	/**
	 * This method allows to declare an interest on some services that has a particular specification facet.
	 * The interest is higly configurable, allowing the user to specify different tipology of filters.
	 * Note that, even if each parameter is optional, it <b>is required that either the schema or the xpath is not null</b>.<br>
	 * List of possible uses and their meaning:
	 * <ul>
	 *   <li><b>retrive all facet with a particular schema</b>: specify only the facetSchema;
	 *   <li><b>retrive all facet with an xml that has some properties</b>: specify only the xpath;
	 *   <li><b>retrive all facet with a particular xsd and an xml that has some properties,</b>: specify both the facetSchema and the xpath;
	 * </ul>
	 * @param facetSchema the schema document
	 * @param xpath the xpath
	 * @return the interest id
	 * @throws NotSubscribedException
	 */
	
	public long subscribeSpecificationFacet(FacetInterest []facetInterest,String description) throws NotSubscribedException;

	/**
	 * This method allows to remove the subscription with the given InterestID.
	 * If such subscription is not found, an NotSubscribedException is thrown.
	 * @param id
	 * @throws NotSubscribedException
	 */
	public void unsubscribe(long id) throws NotSubscribedException; 
	
	/**
	 * This operation deliveries the service identified by the specified id, and
	 * all its additional information facets type and instance, to the other
	 * interested registries.
	 * 
	 * @param serviceId
	 * @return
	 * @throws NotFoundException
	 */
	public long shareAllServiceAdditionalInformations(String serviceId)
	throws NotFoundException;
	
	/**
	 * This operation requires a service id and one of its facet types, which
	 * must be related to a service additional information. As result, the
	 * delivery manager shares the related facet instances with the interested
	 * registries. This method allows a finer-grade selection of the facets you
	 * want to share.
	 * 
	 * @param serviceId
	 * @param facetSchemaId
	 * @return
	 * @throws NotFoundException
	 */
	public long shareServiceAdditionalInformation(String serviceId,String facetSchemaId) throws NotFoundException;

	/**
	 * This operation deliveries the service identified by the specified id, and
	 * all its specification facets type and instance, to the other interested
	 * registries. It is required that the service is originally deployed in the
	 * local subsystem (you cannot share an information received from another
	 * subsystem).
	 * 
	 * @param serviceId
	 * @return
	 * @throws NotFoundException
	 */
	public long shareServiceSpecifications(String serviceId)
	throws NotFoundException;
	
	/**
	 * This operation allows a user to retrieve the detail of a sharing, identified by its id
	 * @param id
	 * @return the details of the sharing
	 */
	public ShareDetails getShareDetails( long id) throws NotFoundException;
	
	/**
	 * This operation allows a user to delete the sharing of an element
	 * @param id
	 */
	public void unshare(long id) throws NotFoundException;
	
	/**
	 * This operation returns the set of identifiers of the shares created by the user
	 * @return all shared ids
	 */
	public long[] getAllSharedIds();
	
	/**
	 * This method returns the Deliverymanager version
	 * @return the Delivery Manager version
	 */
	public String getVersion();
}