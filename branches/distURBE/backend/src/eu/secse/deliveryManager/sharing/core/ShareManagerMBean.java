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

package eu.secse.deliveryManager.sharing.core;

import java.util.Collection;
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
import eu.secse.deliveryManager.interest.InterestAdditionalInformation;
import eu.secse.deliveryManager.interest.InterestAdditionalInformationId;
import eu.secse.deliveryManager.interest.InterestService;
import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.reds.Envelope;
import eu.secse.deliveryManager.reds.InterestEnvelope;
import eu.secse.deliveryManager.registry.IRegistryProxy;
import eu.secse.deliveryManager.sharing.data.filters.InterestEnt;
import eu.secse.deliveryManager.sharing.data.filters.InterestFacetEnt;
import eu.secse.deliveryManager.sharing.data.filters.InterestServiceEnt;
import eu.secse.deliveryManager.sharing.data.filters.InterestFacetEnt.FacetInterestType;
import eu.secse.deliveryManager.sharing.reds.SharingListener;
import eu.secse.deliveryManager.sharing.timer.ISharingTimers;
import eu.secse.deliveryManager.utils.IConfiguration;
import eu.secse.reds.core.IRedsConnector;
import eu.secse.reds.exceptions.AlreadyExistingException;
import eu.secse.reds.exceptions.NotFoundException;

@Service(objectName="DeliveryManager:service=shareManager")
public class ShareManagerMBean implements IShareManagerMBean {

	private static final Log log = LogFactory.getLog(IShareManagerMBean.class);
	
	public final static String REDSID="deliverymanager-sharing-pubsubscribe";
	
	@EJB(beanName="RedsConnector",name="RedsConnector")
	private IRedsConnector redsconnector;
	
	//config utility, it reads the config file
	@EJB IConfiguration conf;
	
	@EJB ISharingTimers timersBean;
	
	@EJB private IRegistryProxy registry;

	@PersistenceContext(unitName="deliveryManager")
	protected EntityManager em;

	
//	 interval between two events (renew or expire)
	private long STEP;
	
//	 first timer event
	private long INITIAL_RENEW;
	private long INITIAL_EXPIRE;

//	 renew and expire timers
	private Timer renew, expire;
	
	private LocalDispatchingService dispatcher;

	public Date getNextExpire() throws IllegalStateException,
			NoSuchObjectLocalException, EJBException {
		return this.expire.getNextTimeout();
	}

	public Date getNextRenew() throws IllegalStateException,
			NoSuchObjectLocalException, EJBException {
		return this.renew.getNextTimeout();
	}

	public LocalDispatchingService getDispatcher() {
		return dispatcher;
	}
	public void publish(Deliverable message) {
//		StringBuilder sb = new StringBuilder();
//		for (StackTraceElement st : Thread.currentThread().getStackTrace()) {
//			sb.append("   ").append(st.toString()).append("\n");
//		}
//		log.info("publishing " + message.getClass().getSimpleName() + ":" + message + "\n" + sb.toString());

		log.info("publishing " + message.getClass().getSimpleName() + ":" + message);
		
		
		dispatcher.publish(new Envelope(message));
	}

	public void start() {
		log.info("Inizializing lease and renew timers for the Sharing");
		initializeTimers();
		dispatcher = null;
		// create Reds listener
		SharingListener listener = new SharingListener();
		
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
		
		subscribeAll("Precedent subscriptions");
	}
	
	public void subscribeAll(String description) {
		log.info("Resubscribing to Service and facet filters");
		Collection<InterestEnt> interests = InterestEnt.getAllInterests(em);
		for(InterestEnt i: interests){
			if (i instanceof InterestServiceEnt) {
				InterestService interestService = new InterestService(((InterestServiceEnt)i).getServiceID());
				this.subscribe(new InterestEnvelope(interestService, registry.getRegistryId()));
			} else {
				if(i instanceof InterestFacetEnt) { 
					InterestFacetEnt intFacetEnt = (InterestFacetEnt)i;
					if(intFacetEnt.getType().equals(FacetInterestType.additionalInformationFacet))
						try{

							InterestAdditionalInformation intAddInfo = new InterestAdditionalInformation("", ((InterestFacetEnt)i).getServiceId(), ((InterestFacetEnt)i).getFacetInterest()[0].getFacetSchema(), ((InterestFacetEnt)i).getFacetInterest()[0].getXpath());
							this.subscribe(new InterestEnvelope(intAddInfo, registry.getRegistryId()));

						} catch (Exception ex) {
							log.error(ex.getMessage());
						}
						else {
							if(intFacetEnt.getType().equals(FacetInterestType.additionalInformationFacetById))
								try{

									InterestAdditionalInformationId intAddInfoId = new InterestAdditionalInformationId(((InterestFacetEnt)i).getServiceId(), ((InterestFacetEnt)i).getFacetSchemaId());
									this.subscribe(new InterestEnvelope(intAddInfoId, registry.getRegistryId()));

								} catch (Exception ex) {
									log.error(ex.getMessage());
								}	
						}
				} else log.warn("Filter class unknown");
			}
		}
	}


	public void stop() {
		log.info("Stopping service shareManager");
		// Stopping timers
		this.renew.cancel();
		this.renew = null;

		this.expire.cancel();
		this.expire = null;
		
	}

	public void subscribe(ComparableFilter filter) {
		dispatcher.subscribe(filter);
		
	}

	public void unsubscribe(ComparableFilter filter) {
		dispatcher.unsubscribe(filter);
		
	}
	private void initializeTimers(){
		try {
			String step = conf.getString("NodeManagerMBean.STEP");
			log.info("Read \"" + step  + "\" from configuration file");
			this.STEP = Long.parseLong(step);
		} catch (Throwable e) {
			log.warn("Cannot read from the configuration file: " + e.getMessage() + "; using the default value");
			STEP = 1 * 60 * 1000; // 1 hour
		}
		log.info("interval between two events (renew or expire): " + STEP  + " milliseconds");
		INITIAL_EXPIRE = STEP * 3 / 4;
		INITIAL_RENEW  = STEP * 1 / 4;

		this.expire = timersBean.createExpireTimer(INITIAL_EXPIRE, STEP);
		log.info("Expire timer started: first expiration check will occur on " + this.expire.getNextTimeout());

		this.renew = timersBean.createRenewTimer(INITIAL_RENEW, STEP);
		log.info("Renew timer started: first renew check will occur on " + this.renew.getNextTimeout());
		
	}

}
