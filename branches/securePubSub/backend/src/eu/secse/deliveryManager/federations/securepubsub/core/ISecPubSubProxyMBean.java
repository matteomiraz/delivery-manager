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

package eu.secse.deliveryManager.federations.securepubsub.core;

import java.util.Date;

import javax.ejb.EJBException;
import javax.ejb.NoSuchObjectLocalException;

import org.jboss.annotation.ejb.Management;

import polimi.reds.ComparableFilter;
import eu.secse.deliveryManager.model.Deliverable;


@Management
public interface ISecPubSubProxyMBean {
//	Life cycle methods
	public void start();
	public void stop();
	
	/**
	 * This method allows to a DeliveryManager to subscribe to a filter that represents a Federation
	 * @param filter
	 */
	public void subscribe(ComparableFilter filter);
	
	/**
	 * This method allows to a DeliveryManager to unsubscribe to a filter that represents a Federation
	 * @param filter
	 */
	public void unsubscribe(ComparableFilter filter);
	
	public void publish(Deliverable message);
	
	public Date getNextExpire() throws IllegalStateException, NoSuchObjectLocalException, EJBException;
	
	public Date getNextRenew() throws IllegalStateException, NoSuchObjectLocalException, EJBException;
	
}
