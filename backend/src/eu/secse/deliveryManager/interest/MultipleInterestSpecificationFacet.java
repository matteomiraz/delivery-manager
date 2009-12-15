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

import eu.secse.deliveryManager.core.FacetInterest;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.Deliverable;

public class MultipleInterestSpecificationFacet implements Interest {

	private static final long serialVersionUID = -3165403040448883388L;

	private final String name;
	
	private SingleInterestSpecificationFacet[] single;
	
	public MultipleInterestSpecificationFacet(String name, FacetInterest[] interest) throws XPathException, SAXException, IOException, ParserConfigurationException {
		if(name != null) {
			this.name = name;
		} else {
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < interest.length; i++) {
				if(i > 0) sb.append("; ");
				sb.append(interest[i].getXpath());
			}
			
			this.name = sb.toString();
		}
		
		single = new SingleInterestSpecificationFacet[interest.length];
		
		for (int i = 0; i < interest.length; i++) {
			single[i] = new SingleInterestSpecificationFacet("", interest[i].getFacetSchema(), interest[i].getXpath());
		}
	}

	public String getName() {
		return name;
	}

	public boolean isCoveredBy(Interest other) {
		return false;
	}

	public boolean matches(Deliverable elem) {
		if (!(elem instanceof DService)) return false;
		
		for (SingleInterestSpecificationFacet s : single) 
			if(!s.matches(elem)) return false;
		
		return true;
	}
	
	public float getSimilarity(Deliverable msg) {
		if(matches(msg)) return 1;
		else return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(single);
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
		MultipleInterestSpecificationFacet other = (MultipleInterestSpecificationFacet) obj;
		if (!Arrays.equals(single, other.single))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": " +  name;
	}
}
