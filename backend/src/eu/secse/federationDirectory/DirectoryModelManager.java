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

package eu.secse.federationDirectory;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.secse.federationDirectory.db.FederationData;

@Stateless
public class DirectoryModelManager implements IDirectoryModelManager{

	@PersistenceContext(unitName="federationdirectory")
	private EntityManager manager;
	
	@Resource
	private TimerService timer;
	
	private static final Log log= LogFactory.getLog(IDirectoryModelManager.class);
	
	@SuppressWarnings("unchecked")
	public FederationData[] getAllFederations() {
		Query allquery=manager.createNamedQuery(FederationData.getall);
		List<FederationData> list=(List<FederationData>)allquery.getResultList();
		FederationData[] fedarray=list.toArray(new FederationData[1]);		
		return fedarray;		
	}

	@SuppressWarnings("unchecked")
	public FederationData[] searchFederationByName(
			String nameRegEx) {
		Query regexpquery=manager.createNamedQuery(FederationData.like);
		regexpquery.setParameter("name",nameRegEx);
		List<FederationData> list=(List<FederationData>)regexpquery.getResultList();
		if (list.isEmpty()) {
			return null;
		}
		return list.toArray(new FederationData[1]);
	}

	public FederationData searchFederationByUid(String uid) {
		return manager.find(FederationData.class, uid);
	}

	public void addFederationData(FederationData fed) {
		FederationData existing=manager.find(FederationData.class,fed.getId().getId());
		if (existing!=null) {
			log.debug("federation " + fed.getId().getId() + " already existing, merging information");
			manager.merge(fed);
		} else {
			log.debug("federation " + fed.getId().getId() + " is new, persisting");
			manager.persist(fed);
		}
	}

	public void startLeaseTimer() {
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.MINUTE,5); //check every 5 minutes
		long interval=1000*5*60 ; //five minutes in millis
		timer.createTimer(cal.getTime(),interval,null);	
		log.info("Started lease timer");
	}
	
	//@Timeout	
	@SuppressWarnings("unchecked")
	public void ejbTimeout(Timer arg0) {
		log.info("Removing expired federations");
		Query expiredquery=manager.createNamedQuery(FederationData.expired);
		
		List<FederationData> list=(List<FederationData>)expiredquery.getResultList();		
		for (FederationData fed:list) {
			manager.remove(fed);
			log.info("Removed federation " + fed.getName() + "(" + fed.getId()+")");
		}		
		
	}
	
	@SuppressWarnings("unchecked")
	public void removeAllFederations() {
		log.info("Removing all federations");
		Query query=manager.createNamedQuery(FederationData.getall);
		
		List<FederationData> list=(List<FederationData>)query.getResultList();		
		for (FederationData fed:list) {
			manager.remove(fed);
			log.info("Removed federation " + fed.getName() + "(" + fed.getId()+")");
		}
		
	}

	@SuppressWarnings("unchecked")
	public void stopLeaseTimer() {
		for (Timer t: (Collection<Timer>)timer.getTimers()) {
			t.cancel();
		}
		
		
	}

	public void removeFederation(String federationid) {
		FederationData fed=manager.find(FederationData.class,federationid);
		if (fed!=null) {
			manager.remove(fed);
		} else {
			log.debug("Federation " + federationid + " is already deleted");
		}
	} 


}
