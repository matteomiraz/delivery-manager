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

package eu.secse.deliveryManager.federations.pubsubscribe.core;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.Timer;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;

import polimi.reds.ComparableFilter;
import polimi.reds.LocalDispatchingService;
import eu.secse.deliveryManager.federations.pubsubscribe.data.PubSubFederationExtraInfo;
import eu.secse.deliveryManager.federations.pubsubscribe.reds.RedsPubSubFederationListener;
import eu.secse.deliveryManager.federations.pubsubscribe.timer.IFedPsExpire;
import eu.secse.deliveryManager.federations.pubsubscribe.timer.IFedPsRenew;
import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.reds.Envelope;
import eu.secse.deliveryManager.registry.IRegistryProxy;
import eu.secse.deliveryManager.utils.IConfiguration;
import eu.secse.reds.core.IRedsConnector;
import eu.secse.reds.exceptions.AlreadyExistingException;
import eu.secse.reds.exceptions.NotFoundException;


@Service(objectName="DeliveryManager:service=pubSubFederationProxy")
public class PubSubProxyMBean implements IPubSubProxyMBean{

	private static final Log log = LogFactory.getLog(IPubSubProxyMBean.class);

	public final static String REDSID="deliverymanager-federation-publishsubscribe";

	@EJB(beanName="RedsConnector",name="RedsConnector")
	private IRedsConnector redsconnector;

	//timers
	@EJB IFedPsExpire expireBean;
	@EJB IFedPsRenew renewBean;
	
	@EJB IRegistryProxy registry;

	
	//config utility, it reads the config file
	@EJB IConfiguration conf;

//	interval between two events (renew or expire)
	private long STEP;

//	first timer event
	private long INITIAL_RENEW;
	private long INITIAL_EXPIRE;

//	renew and expire timers
	private Timer renew, expire;

	public Date getNextExpire() throws IllegalStateException, NoSuchObjectLocalException, EJBException {
		return this.expire.getNextTimeout();
	}

	public Date getNextRenew() throws IllegalStateException, NoSuchObjectLocalException, EJBException {
		return this.renew.getNextTimeout();
	}

	@PersistenceContext(unitName="deliveryManager")
	protected EntityManager em;

	private LocalDispatchingService dispatcher;

	public void start() {
		log.info("Inizializing lease and renew timers for the PubSubFederation");
		initializeTimers();
		dispatcher = null;
		// create Reds listener
		RedsPubSubFederationListener listener = new RedsPubSubFederationListener();

		try {
			redsconnector.registerDispatcher(REDSID,listener);
		} catch (AlreadyExistingException e) {
			log.error("Cannot register reds listener: already existing " + e.getMessage());
			return;
		}

		try {
			dispatcher=redsconnector.getDispatcher(REDSID);
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
		log.info("Stopping service pubSubFederationProxy");
		// Stopping timers
		this.renew.cancel();
		this.renew = null;

		this.expire.cancel();
		this.expire = null;

	}

	public void publish(Deliverable elem) {
		log.info("publishing " + elem.getClass().getSimpleName() + ":" + elem);
		dispatcher.publish(new Envelope(elem));
	}

	private void initializeTimers(){
		try {
			String step = conf.getString("FederationPS.STEP");
			log.info("Read \"" + step  + "\" from configuration file");
			this.STEP = Long.parseLong(step);
		} catch (Throwable e) {
			log.warn("Cannot read from the configuration file: " + e.getMessage() + "; using the default value");
			STEP = 60 * 60 * 1000; // 1 hour
		}
		log.info("interval between two events (renew or expire): " + STEP  + " milliseconds");
		INITIAL_EXPIRE = STEP * 3 / 4;
		INITIAL_RENEW  = STEP * 1 / 4;

		this.renew = this.renewBean.createTimer(INITIAL_RENEW, STEP);
		log.info("Renew timer started: first renew check will occur on " + this.renew.getNextTimeout());

		this.expire = this.expireBean.createTimer(INITIAL_EXPIRE, STEP);
		log.info("Expire timer started: first expiration check will occur on " + this.expire.getNextTimeout());

	}
	
	private void subscribeAll(){
		for(PubSubFederationExtraInfo intFedEnt : PubSubFederationExtraInfo.getAll(em))
			dispatcher.subscribe(intFedEnt.getFederationFilter());
	}
}
