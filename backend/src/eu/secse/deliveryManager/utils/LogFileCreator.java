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

package eu.secse.deliveryManager.utils;

import java.io.File;
import java.io.IOException;

public class LogFileCreator {
	
	private static final long simulationId = System.currentTimeMillis();

	private static final String DIRE = "/dire/performances/";

	/**
	 * Log file creator.<br>
	 * Usage:
	 * <code>
	 * simulationId = 12; // system-wide simulation id <br>
	 * File f = createFile("test", ".log");
	 * </code>
	 * @param prefix the prefix of the generated file
	 * @param suffix the suffix (including the dot) of the generated file
	 * @return the generated file: it could be in the users' home directory or 
	 * in the temporary directory
	 * @throws IOException if something goes very wrong
	 */
	public static final File createFile(String prefix, String suffix) throws IOException {
		File ret;

		String dirString = System.getProperty("user.home") + DIRE;
		File dir = new File(dirString);
		
		if(dir.exists() || dir.mkdirs()) {
			ret = new File(dir, prefix + simulationId + suffix);

			try {
				if(!ret.exists() && ret.createNewFile()) 
					return ret;
			} catch (IOException e) {
			}
		}
		
		ret = File.createTempFile(prefix, suffix);
		return ret;
	}
}
