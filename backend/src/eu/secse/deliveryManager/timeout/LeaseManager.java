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

package eu.secse.deliveryManager.timeout;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.secse.deliveryManager.data.ElementEnt;
import eu.secse.deliveryManager.data.ElementExtraInfo;
import eu.secse.deliveryManager.utils.IConfiguration;

@Stateless
public class LeaseManager implements ILeaseManager {
	private static final Log log = LogFactory.getLog(ILeaseManager.class);

	@EJB IConfiguration conf;

	/** when resend the created elements */
	Long initialRenew = null;

	/** lease period of all elements */
	Long leaseTimeout = null;

	/** when resend all elements */
	Long standardRenew = null;
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.ILeaseManager#getInitialRenew()
	 */
	public Date getInitialRenew() {
		
		if (initialRenew == null) {
			try {
				String step = conf.getString("LeaseManager.initialRenew");
				this.initialRenew = Long.parseLong(step);
			} catch (Throwable e) {
				initialRenew = (long) (8 * 3600 * 1000);
			}
			log.info("initial renew of an element: " + initialRenew + " milliseconds");
		}		
		return new Date(System.currentTimeMillis() + this.initialRenew);
	}

	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.ILeaseManager#getStandardRenew()
	 */
	public Date getStandardRenew() {
		if (standardRenew == null) {
			try {
				String step = conf.getString("LeaseManager.standardRenew");
				this.standardRenew = Long.parseLong(step);
			} catch (Throwable e) {
				this.standardRenew = (long) (24 * 3600 * 1000);
			}
			log.info("standard renew of an element: " + standardRenew + " milliseconds");
		}		
		return new Date(System.currentTimeMillis() + this.standardRenew);
	}

	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.ILeaseManager#getLease()
	 */
	public Date getLease(ElementEnt elem) {
		
		ElementExtraInfo elementLease = elem.getExtraInfo().get(LeaseExtraInfo.INFO_TYPE);
		if(elementLease != null && elementLease instanceof LeaseExtraInfo) return ((LeaseExtraInfo)elementLease).getLease();
		
		if (leaseTimeout == null) {
			try {
				String step = conf.getString("LeaseManager.leaseTimeout");
				this.leaseTimeout = Long.parseLong(step);
			} catch (Throwable e) {
				this.leaseTimeout = (long) (7 * 24 * 3600 * 1000);
			}
			log.info("lease time of an element: " + leaseTimeout
					+ " milliseconds");
		}		
		return new Date(System.currentTimeMillis() + this.leaseTimeout);
	}

}
