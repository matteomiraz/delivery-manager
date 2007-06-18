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

import java.util.Date;

import javax.annotation.EJB;
import javax.ejb.EJBException;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.Timer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;

import eu.secse.deliveryManager.reds.IKeepAliveMaster;

@Service(objectName="DeliveryManager:service=nodeManager")
public class NodeManagerMBean implements INodeManager {
	private static final Log log = LogFactory.getLog(INodeManager.class);

	@EJB IConfiguration conf;
	@EJB IExpire expireBean;
	@EJB IRenew renewBean;
	@EJB IKeepAliveMaster keepAliveBean;
	
	// interval between two events (renew or expire)
	private long STEP;
	
	// first timer event
	private long INITIAL_RENEW;
	private long INITIAL_EXPIRE;

	// renew and expire timers
	private Timer renew, expire, keepAlive;

	public Date getNextExpire() throws IllegalStateException, NoSuchObjectLocalException, EJBException {
		return this.expire.getNextTimeout();
	}

	public Date getNextRenew() throws IllegalStateException, NoSuchObjectLocalException, EJBException {
		return this.renew.getNextTimeout();
	}

	/* (non-Javadoc)
	 * @see notification.reds.Reds#start()
	 */
	public void start() {
		log.info("This is Delivery Manager, GPL edition");

		try {
			String step = conf.getString("NodeManagerMBean.STEP");
			log.info("Read \"" + step  + "\" from configuration file"); //TODO:
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
		
		if(new Boolean(conf.getString("KeepAliveMaster"))) {
			log.info("Configured as keepalive master");
			this.keepAlive = this.keepAliveBean.createTimer(5000, 15000);
		}
	}
	
	public void stop() throws Exception {
		this.renew.cancel();
		this.renew = null;

		this.expire.cancel();
		this.expire = null;
		
		if(keepAlive != null) {
			this.keepAlive.cancel();
			this.keepAlive = null;
		}
		
		log.info("Node manager stopped");
	}
}
