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


package eu.secse.deliveryManager;

import javax.ejb.Local;

import eu.secse.deliveryManager.exceptions.NotSubscribedException;


@Local
public interface IInterestManager {
	// =======================================================================
	// interests
	// -----------------------------------------------------------------------

	/**
	 * This method allows declaring an interest on a particular service,
	 * identified by its id. After an invocation to this method, all service
	 * specification facets are forwarded to the local registry. This method
	 * returns the subscribe identification, that can be used for unsubscribe to
	 * the created interest.
	 */
	long subscribeService(String serviceId);
	
	// TODO: se avanza tempo... long subscribeServiceByFacetSpecification(FacetSpecificationConstraint constraint);
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
	long subscribeSpecificationFacet(String facetSchema, String xpath)
			throws NotSubscribedException;

	/**
	 * This method allows to retrive a particular facet additional information, whose id is known.
	 * It is possible to specify the related serviceId, in order to speed up the retrival of the interested
	 * informations.
	 * @param serviceId the service id <b>(optional)</b>
	 * @param facetSchemaId the facet schema identifier
	 * @return the interest id
	 * @throws NotSubscribedException 
	 */
	long subscribeAdditionalInformationFacetById(String serviceId, String facetSchemaId) throws NotSubscribedException;

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
	long subscribeAdditionalInformationFacet(String serviceId, String facetSchema, String xpath)
			throws NotSubscribedException;
	
	/**
	 * This method returns the collection with the id of all registered interests.
	 * @return the set of all interests' id
	 */
	long[] getAllInterestIds();
	
	/**
	 * This method allows to get more information about an interest.
	 * @param id the id of the interest
	 * @return the details of the selected interest 
	 * @throws NotSubscribedException if the specified interest cannot be found.
	 */
	GenericInterest getInterestById(long id) throws NotSubscribedException;
	
	/**
	 * This method allows to remove the subscription with the given InterestID.
	 * If such subscription is not found, an NotSubscribedException is thrown.
	 */
	void unsubscribe(long id) throws NotSubscribedException;

	void subscribeAll();
}
