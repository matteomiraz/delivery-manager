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

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TimedObject;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.mx.util.MBeanProxyExt;
import org.jboss.mx.util.MBeanServerLocator;

import eu.secse.deliveryManager.federations.data.DMFederation;
import eu.secse.deliveryManager.federations.data.IDmFederationDataConverter;
import eu.secse.deliveryManager.federations.directoryreds.IRedsDirectoryProxy;
import eu.secse.federationDirectory.reds.messages.FederationCreation;
import eu.secse.federationDirectory.reds.messages.FederationRemoval;

@Stateless
public class FederationCoordinator implements
		IFederationCoordinator,TimedObject {

	private static final Log log = LogFactory.getLog(IFederationCoordinator.class);
	
	
	private InitialContext ctx;


	private final long LEASE_STEP=1000*60*60*24; //one day
	private final long LEASE_INIT=1000*60; //one minute
	private final long DISCOVER_TIMER=1000*60; // one minute
	private final static String DISCOVER="discover";
	
	@Resource
	private TimerService	 timerService;
	
	@PersistenceContext(unitName="deliveryManager")
	private EntityManager manager;
	
	@EJB
	private IRedsDirectoryProxy dirproxy;
	
	@EJB
	private IDmFederationDataConverter converter;
	
	
	
	public FederationProxy getProxy(String method) {		
        try {
			if(ctx == null) ctx = new InitialContext();			
			MBeanServer server = MBeanServerLocator.locate();
	        IFederationCoordinationManager manager=null;
			try {
				manager = (IFederationCoordinationManager) MBeanProxyExt.create(IFederationCoordinationManager.class, "DeliveryManager:service=federationCoordinator", server);					
			} catch (MalformedObjectNameException e) {			
		        log.error(e.getMessage());
		        return null;
			}
			String lookupname=manager.getProxyLookupName(method);
			if (lookupname==null) {
				log.error("No proxy available for method " + method);
				return null;
			}
			log.debug("Looking up proxy using name " + lookupname);
			return (FederationProxy)ctx.lookup(lookupname);
        } catch (NamingException nex) {
        	log.error("Error while looking up proxy for method " + method + ". Invalid lookup name." + nex.getMessage());
        	return null;
        }
	}
	
	@SuppressWarnings("unchecked")
	public void startTimers() {
//		Reset timers
		for (Timer t:(Collection<Timer>)timerService.getTimers()) {
			t.cancel();
		}
		timerService.createTimer(LEASE_INIT,LEASE_STEP,null);	
		timerService.createTimer(DISCOVER_TIMER,DISCOVER_TIMER,DISCOVER);
		
	}
	
	@SuppressWarnings("unchecked")
	public void stopTimers() {
		for (Timer t:(Collection<Timer>)timerService.getTimers()) {
			t.cancel();
		}
	}
	/** Renew owned federations that are about to expire, remove expired federations
	 * 
	 */	
	//Recommended transaction value for timeout
	@SuppressWarnings("unchecked")
	@TransactionAttribute(value=TransactionAttributeType.REQUIRES_NEW)
	@Timeout	
	public void ejbTimeout(Timer arg0) {
		Serializable serial=arg0.getInfo();
		if (serial!=null && serial instanceof String) {
			String code=(String)serial;
			if (code.equals(DISCOVER)) {
				//retrieves FederationCoordinationManager
				IFederationCoordinationManager fedmana=lookupManager();
				if (fedmana!=null && fedmana.getDirectories().size()==0) {
					//No known directories, discover
					fedmana.discoverDirectories();
				}
			}
			return;
		}
		
		Query q=manager.createNamedQuery(DMFederation.torenew);
		List<DMFederation> torenew=q.getResultList();
		for (DMFederation f:torenew) {			
			f.setLease(computeCreationLease());				
			f.setRenew(computeRenewTime());			
			FederationCreation fc=new FederationCreation(converter.convert(f));			
			dirproxy.send(fc);
		}
		
		q=manager.createNamedQuery(DMFederation.expired);
		List<DMFederation> expired=q.getResultList();
		for (DMFederation f:expired) {
			if (!f.isOwnership()) {
				manager.remove(f);
			}
		}
	}

	public void dismissFederation(String federationid) {
		dirproxy.send(new FederationRemoval(federationid));			
	}

	public String[] getSupportedMethods() {
	    try {
			if(ctx == null) ctx = new InitialContext();			
			MBeanServer server = MBeanServerLocator.locate();
	        IFederationCoordinationManager manager=null;
			try {
				manager = (IFederationCoordinationManager) MBeanProxyExt.create(IFederationCoordinationManager.class, "DeliveryManager:service=federationCoordinator", server);
				return manager.getMethods();
			} catch (MalformedObjectNameException e) {			
		        log.error(e.getMessage());
		        return null;
			}
	    }catch (NamingException nex) {
        	log.error("Error while looking up available methods");
        	return null;
        }
	}

	public void createFederation(DMFederation fed) {	
		fed.setLease(computeCreationLease());
		fed.setRenew(computeRenewTime());
		dirproxy.send(new FederationCreation(converter.convert(fed)));		
	}
	
	private IFederationCoordinationManager lookupManager() {
		  try {
				if(ctx == null) ctx = new InitialContext();			
				MBeanServer server = MBeanServerLocator.locate();
		        IFederationCoordinationManager manager=null;
				try {
					manager = (IFederationCoordinationManager) MBeanProxyExt.create(IFederationCoordinationManager.class, "DeliveryManager:service=federationCoordinator", server);
					return manager;
				} catch (MalformedObjectNameException e) {			
			        log.error(e.getMessage());
			        return null;
				}			
	        } catch (NamingException nex) {
	        	log.error("Could not lookup IFederationCoordinationManager");
	        	return null;
	        }
	}
	
	private Date computeCreationLease() {
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.SECOND,(int)LEASE_STEP/1000);			
		return cal.getTime();
	}

	private Date computeRenewTime() {
		Calendar cal=Calendar.getInstance();
		//Renew a bit earlier
		cal.add(Calendar.SECOND,(int)(LEASE_STEP*(0.9)/1000));
		return cal.getTime();
	}
}
