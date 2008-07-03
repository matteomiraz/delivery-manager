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

package eu.secse.deliveryManager.logger;

import org.jboss.annotation.ejb.Management;

@Management
public interface IPerformanceLoggerMBean {

	public abstract void start();
	public abstract void stop();

	public abstract void log(String s);
	public abstract void flush();

	public abstract boolean isEnabled();
	public abstract void setEnabled(boolean enabled);

	public abstract String getLogFile();

	public abstract int getBufferSize();
	public abstract void setBufferSize(int bufferSize);

	public abstract int getBufferUsage();
	public abstract String getBufferUsagePercent();
	
	public int getRedsBufferCapacity();
	public int getRedsBufferUsage();
	
	public String getRedsLogFile();
}