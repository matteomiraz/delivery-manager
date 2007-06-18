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


package eu.secse.deliveryManager.reds;


import javax.ejb.PrePassivate;
import javax.ejb.Stateless;
import javax.management.MBeanServer;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.mx.util.MBeanProxyExt;
import org.jboss.mx.util.MBeanServerLocator;

import eu.secse.deliveryManager.model.Deliverable;


@Stateless
public class RedsProxy implements IRedsProxy {
	private static final Log log = LogFactory.getLog(RedsProxy.class);
	
	private IRedsMBean reds;
	
	@PersistenceContext(unitName="deliveryManager")
	protected EntityManager em;

	@PrePassivate
	public void passivate() {
		this.reds = null;
	}

	public void subscribe(InterestEnvelope interest) {
		if(this.isRedsRunning()) {
			log.info("subscribing to " + interest);
			this.reds.dispatchingService().subscribe(interest);
		} else {
			log.error("The connection with reds broker is not estabilished. Cannot subscribe!");
		}
	}

	private boolean isRedsRunning() {
		if(this.reds == null) {
			try {
		        MBeanServer server = MBeanServerLocator.locate();
		        this.reds = (IRedsMBean) MBeanProxyExt.create(IRedsMBean.class, "DeliveryManager:service=reds", server);
		    } catch (Exception e) {
		        log.warn(e.getMessage());
		        return false;
		    }
		}
		return this.reds.dispatchingService() != null && this.reds.dispatchingService().isOpened();
	}

	public void unsubscribe(String nodeIdentifier, long interestID) {
		if(this.isRedsRunning()) {
			log.info("unsubscribing to interest id: " + interestID);
			this.reds.dispatchingService().unsubscribe(
					new InterestEnvelope(null, nodeIdentifier, interestID));
		} else {
			log.error("The connection with reds broker is not estabilished. Cannot unsubscribe!");
		}
	}

	public void publish(Deliverable elem) {
		if(this.isRedsRunning()) {
			log.info("publishing " + elem.getClass().getSimpleName() + ":" + elem);
			this.reds.publish(new Envelope(elem));
		} else
			log.warn("warn: cannot publish " + elem.getClass().getSimpleName() + ": " + elem + ": reds not running.");
	}
	
	public void sendWakeUp() {
		if(this.isRedsRunning()) {
			WakeUpMessage w = new WakeUpMessage(System.currentTimeMillis());
			this.reds.publish(w);
			log.debug("Sending wakeup: " + w.toString());
		} else
			log.warn("warn: cannot send renew!");
	}
}
