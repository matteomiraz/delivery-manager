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

package eu.secse.deliveryManager.core;

import java.io.Serializable;

public class FacetInterest implements Serializable {
	
	private static final long serialVersionUID = 1725545304441748355L;

	private String facetSchema;
	private String xpath;
	
	public FacetInterest() { }

	public FacetInterest(String facetSchema, String xpath) {
		super();
		this.facetSchema = facetSchema;
		this.xpath = xpath;
	}

	public String getFacetSchema() {
		return facetSchema;
	}

	public void setFacetSchema(String facetSchema) {
		this.facetSchema = facetSchema;
	}

	public String getXpath() {
		return xpath;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}
}
