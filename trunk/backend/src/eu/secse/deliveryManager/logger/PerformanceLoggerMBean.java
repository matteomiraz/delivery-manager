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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.Service;

import eu.secse.RedsPerformanceLogger;
import eu.secse.deliveryManager.utils.LogFileCreator;

@Service(objectName="DeliveryManager:service=performanceLogger")
public class PerformanceLoggerMBean implements IPerformanceLoggerMBean  {
	private static final Log log = LogFactory.getLog(IPerformanceLoggerMBean.class);

	private boolean enabled = true;
	
	private File logFile;
	private int bufferSize = 1024;
	
	private StringBuffer buffer;
	private Object lock;

	private Thread writer;
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.logger.IPerformanceLoggerMBean#start()
	 */
	public void start() {
		lock = new Object();
		buffer = new StringBuffer(bufferSize);
		
		writer = new Thread(new Runnable() {
			public void run() {
				try {
					while(true) {
						Thread.sleep(60000);
						PerformanceLoggerMBean.this.flush();
					}
				} catch (InterruptedException e) {
				}
			}
		});
		
		writer.setDaemon(true);
		writer.start();
		
		try {
			this.logFile = LogFileCreator.createFile("performanceDeliveryManager", ".log");
		} catch (Exception e) {
			log.error("Unable to create performance log file");
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.logger.IPerformanceLoggerMBean#stop()
	 */
	public void stop() {
		this.flush();
		RedsPerformanceLogger.getSingleton().flush();

		try {
			writer.interrupt();
		} catch (Throwable e) {
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.logger.IPerformanceLoggerMBean#flush()
	 */
	public void flush() {
		synchronized (lock) {
			log.info("Saving performance indexes");
			try {
				BufferedWriter wr = new BufferedWriter(new FileWriter(logFile, true));
				wr.write(buffer.toString());
				wr.close();

				buffer = new StringBuffer();
			} catch (IOException e) {
				log.error("Cannot log performances measures: " + e.getMessage() + " due to: " + e.getCause());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.logger.IPerformanceLoggerMBean#log(java.lang.String)
	 */
	public void log(String s) {
		if(! s.endsWith("\n")) s = s + "\n";

		buffer.append(s);
		
		if(buffer.length() > bufferSize) {
			synchronized (lock) {
				if(buffer.length() > bufferSize) 
					flush();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.logger.IPerformanceLoggerMBean#isEnabled()
	 */
	public boolean isEnabled() {
		return enabled;
	}
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.logger.IPerformanceLoggerMBean#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.logger.IPerformanceLoggerMBean#getLogFile()
	 */
	public String getLogFile() {
		return logFile.getAbsolutePath();
	}
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.logger.IPerformanceLoggerMBean#getBufferSize()
	 */
	public int getBufferSize() {
		return bufferSize;
	}
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.logger.IPerformanceLoggerMBean#setBufferSize(int)
	 */
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}
	
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.logger.IPerformanceLoggerMBean#getBufferUsage()
	 */
	public int getBufferUsage() {
		return buffer.length();
	}
	/* (non-Javadoc)
	 * @see eu.secse.deliveryManager.logger.IPerformanceLoggerMBean#getBufferUsagePercent()
	 */
	public String getBufferUsagePercent() {
		float percent = (buffer.length() * 10000 / bufferSize) / 100.0f;
		
		return percent + " %";
	}

	public int getRedsBufferCapacity() {
		return RedsPerformanceLogger.getSingleton().getSize();
	}

	public int getRedsBufferUsage() {
		return RedsPerformanceLogger.getSingleton().getUsed();
	}

	public String getRedsLogFile() {
		return RedsPerformanceLogger.getSingleton().getFileName();
	}
}
