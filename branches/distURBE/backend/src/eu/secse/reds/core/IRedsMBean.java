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

package eu.secse.reds.core;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.util.Collection;

import org.jboss.annotation.ejb.Management;

import polimi.reds.LocalDispatchingService;
import polimi.reds.broker.overlay.AlreadyAddedNeighborException;
import polimi.reds.broker.overlay.Overlay;
import eu.secse.reds.exceptions.AlreadyExistingException;
import eu.secse.reds.exceptions.NotFoundException;

@Management
public interface IRedsMBean {
	public Collection<String> getConnectedBrokers();
	public boolean addBroker(String broker) throws ConnectException, MalformedURLException, AlreadyAddedNeighborException;

	public String getRedsCodebase();
	public void setRedsCodebase(String codebase);

	public void registerDispatcher(String id, ARedsListener listener) throws AlreadyExistingException;	
	public LocalDispatchingService getDispatcher(String id) throws NotFoundException;
	
	// Life cycle method
	public void start();
	public void stop();
	
	public void sendWakeUp();
	
	public WakeUpListener getWakeUpListener();
	
	public Overlay getOverlay();
}