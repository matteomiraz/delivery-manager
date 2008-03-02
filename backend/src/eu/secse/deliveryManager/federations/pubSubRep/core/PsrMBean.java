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

package eu.secse.deliveryManager.federations.pubSubRep.core;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;

import polimi.reds.ComparableFilter;
import polimi.reds.LocalDispatchingService;
import polimi.reds.Message;
import polimi.reds.MessageID;
import eu.secse.deliveryManager.federations.pubSubRep.data.PubSubReplyFederationExtraInfo;
import eu.secse.deliveryManager.federations.pubSubRep.reds.RedsPSRFederationListener;
import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.reds.Envelope;
import eu.secse.deliveryManager.reds.RepliableEnvelope;
import eu.secse.deliveryManager.registry.IRegistryProxy;
import eu.secse.deliveryManager.utils.IConfiguration;
import eu.secse.reds.core.IRedsConnector;
import eu.secse.reds.exceptions.AlreadyExistingException;
import eu.secse.reds.exceptions.NotFoundException;

@Service(objectName="DeliveryManager:service=pubSubRepFederationProxy")
public class PsrMBean implements IPsrMBean{

	private static final Log log = LogFactory.getLog(IPsrMBean.class);

	public final static String REDS_ID="deliverymanager-federation-psr";

	@EJB(beanName="RedsConnector",name="RedsConnector")
	private IRedsConnector redsconnector;

	@EJB IRegistryProxy registry;
	
	//config utility, it reads the config file
	@EJB IConfiguration conf;

	@PersistenceContext(unitName="deliveryManager")
	protected EntityManager em;

	private LocalDispatchingService dispatcher;

	public void start() {
		log.info("Inizializing Publish / Subscribe with Reply federation manager");
		// create Reds listener
		dispatcher = null;
		RedsPSRFederationListener listener = new RedsPSRFederationListener();

		try {
			redsconnector.registerDispatcher(REDS_ID,listener);
		} catch (AlreadyExistingException e) {
			log.error("Cannot register reds listener: already existing " + e.getMessage());
			return;
		}

		try {
			dispatcher=redsconnector.getDispatcher(REDS_ID);
		} catch (NotFoundException e) {
			log.error("Cannot retrieve reds dispatcher: not registered" + e.getMessage());
			return;
		}

		log.info("subscribing all federations");
		subscribeAll();
	}

	public void subscribe(ComparableFilter filter){
		dispatcher.subscribe(filter);
	}
	public void unsubscribe(ComparableFilter filter){
		dispatcher.unsubscribe(filter);
	}

	public LocalDispatchingService getDispatcher() {
		return dispatcher;
	}

	public void stop() {
		log.info("Stopping Publish / Subscribe with reply Federation manager");
	}

	public void publish(Deliverable elem) {
		log.info("publishing " + elem.getClass().getSimpleName() + ":" + elem);
		dispatcher.publish(new Envelope(elem));
	}

	public void publishRepliable(Deliverable elem) {
		log.info("publishing repliable" + elem.getClass().getSimpleName() + ":" + elem);
		dispatcher.publish(new RepliableEnvelope(elem));
	}

	private void subscribeAll(){
		for(PubSubReplyFederationExtraInfo intFedEnt : PubSubReplyFederationExtraInfo.getAll(em))
			dispatcher.subscribe(intFedEnt.getFederationFilter());
	}
	
	public void reply(Message reply, MessageID repliableMessageID) {
		dispatcher.reply(reply, repliableMessageID);
	}
}
