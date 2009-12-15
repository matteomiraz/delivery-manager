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

package eu.secse.deliveryManager.interest;

import java.io.IOException;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathException;

import org.xml.sax.SAXException;

import eu.secse.deliveryManager.model.Deliverable;

public class MultipleORInterest implements Interest {
	private static final boolean DEBUG = false; 

	private static final long serialVersionUID = 9176930148088186291L;

	private final String name;
	
	private Interest[] interests;
	
	public MultipleORInterest(String name, Interest ... interests) throws XPathException, SAXException, IOException, ParserConfigurationException {
		if(name != null) {
			this.name = name;
		} else {
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < interests.length; i++) {
				if(i > 0) sb.append(" OR ");
				sb.append(interests[i].getName());
			}
			
			this.name = sb.toString();
		}
		
		this.interests = new Interest[interests.length];
		
		for (int i = 0; i < interests.length; i++) {
			this.interests[i] = interests[i];
		}
	}

	public String getName() {
		return this.name;
	}
	
	public boolean isCoveredBy(Interest other) {
		return false;
	}

	public boolean matches(Deliverable elem) {
		if(DEBUG) System.out.println("  checking " + this.getName());

		for (Interest i : interests)
			if(i.matches(elem)) {
				if(DEBUG) System.out.println("     " + i.getName() + ": TRUE");
				return true;
			} else {
				if(DEBUG) System.out.println("     " + i.getName() + ": false");
			}
		
		return false;
	}

	public float getSimilarity(Deliverable msg) {
		float max = 0.0f;
		for (Interest i : interests) {
			float v = i.getSimilarity(msg);
			if(max < v) max = v;
		}
		
		return max; 
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(interests);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MultipleORInterest other = (MultipleORInterest) obj;
		if (!Arrays.equals(interests, other.interests))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": " +  name;
	}
}
