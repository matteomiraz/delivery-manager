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


package reds.test;

import java.net.ConnectException;

import polimi.reds.DispatchingService;
import polimi.reds.Message;
import polimi.reds.TCPDispatchingService;
import polimi.reds.UDPDispatchingService;
import eu.secse.deliveryManager.reds.Envelope;
import eu.secse.deliveryManager.reds.InterestEnvelope;

public class RedsLogger {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String []st = args[0].split(":");
		DispatchingService ds;
		if (st[0].equals("reds-tcp")) {
			if(st.length != 3) {
				System.err.println("Cannot use the specified broker (" + args[0] + "): for tcp use reds-tcp:<address>:<port>");
				return;
			}
			
			String dsAddr = st[1];
			int dsPort = Integer.parseInt(st[2]);
			ds = new TCPDispatchingService(dsAddr, dsPort);
		} else if (st[0].equals("reds-udp")) {
			if(st.length != 4) {
				System.err.println("Cannot use the specified broker (" + args[0] + "): for udp use reds-tcp:<address>:<brokerPort>:<localPort>");
				return;
			}
			
			String dsAddr = st[1];
			int dsPort = Integer.parseInt(st[2]);
			int udpPort = Integer.parseInt(st[3]);
			ds = new UDPDispatchingService(dsAddr, dsPort, udpPort);
		} else {
			System.err.println("Cannot use the specified broker");
			return;
		}

		try {
			ds.open();
		} catch (ConnectException e) {
			System.err.println("cannot connect to broker: " + e.getMessage() + "\n caused by: " + e.getCause());
			return;
		}
		
		System.err.println("reds connected to broker " + args[0]);
		
		ds.subscribe(new InterestEnvelope(new AllDeliverables(), "RedsLogger", 1));
		
		while(ds.isOpened()) {
			Message msg = ds.getNextMessage(10000);

			// if received a message
			if(msg != null) {
				// if it is a envelop of a model (envelope or EnvelopeFederation)
				if(msg instanceof Envelope) {
					System.out.println("received: " + msg);
				} else 
					System.err.println("Unknown message container: " + msg.getClass().getCanonicalName());
			}
		}
		
		System.err.println("Ending");
	}
}
