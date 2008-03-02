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

package eu.secse.deliveryManager.registry;

import it.s2.registry.Credentials;
import it.s2.registry.FacetSpecificationXML;
import it.s2.registry.FacetSpecificationXSD;
import it.s2.registry.Service;
import it.s2.registry.ServiceProviderDescription;

public class Utils {

	public static String convert(long[] l) {
		if(l == null) return "null";
		
		StringBuilder ret = new StringBuilder("{");
		for (int i = 0; i < l.length; i++) {
			if(i != 0) ret.append(", ");
			ret.append(l[i]);
		}
		return ret.append("}").toString();
	}

	public static String convert(String[] str) {
		if(str == null) return "null";
		
		StringBuilder ret = new StringBuilder("{");
		for (int i = 0; i < str.length; i++) {
			if(i != 0) ret.append(", ");
			ret.append(str[i]);
		}
		return ret.append("}").toString();
	}

	public static  String convert(Credentials c) {
		if(c == null) return "null";
		
		return "[login:" + c.getLogin() + ", password:" + (c.getPassword()==null?"null":"***password***") + "]";
//		return "[login:" + c.getLogin() + ", password:" + c.getPassword() + "]";
	}

	public static  String convert(Service srv) {
		if(srv == null) return "null";
		
		return "[id:" + srv.getId() + ", name:" + srv.getName() + ", prevID:" + srv.getPreviousVersionId() + ", provider:" + srv.getProviderId() + ", regId:" + srv.getRegistryId() + ", version:" + srv.getVersion() + "]";
	}

	public static  String convert(Service[] srv) {
		if(srv == null) return "null";
		
		StringBuilder ret = new StringBuilder("{");
		for (int i = 0; i < srv.length; i++) {
			if(i != 0) ret.append(", ");
			ret.append(convert(srv[i]));
		}
		return ret.append("}").toString();
	}

	public static  String convert(FacetSpecificationXSD xsd) {
		if(xsd == null) return "null";
		
		return "[doc:" + xsd.getDocument() + ", xsdId:" + xsd.getFacetSpecificationXSDId() + ", name:" + xsd.getName() + ", provider:" + xsd.getProviderId() + ", service:" + xsd.getServiceId() + "]";
	}

	public static  String convert(FacetSpecificationXSD[] xsd) {
		if(xsd == null) return "null";
		
		StringBuilder ret = new StringBuilder("{");
		for (int i = 0; i < xsd.length; i++) {
			if(i != 0) ret.append(", ");
			ret.append(convert(xsd[i]));
		}
		return ret.append("}").toString();
	}

	public static  String convert(FacetSpecificationXML xml) {
		if(xml == null) return "null";
		
		return "[doc:" + xml.getDocument() + ", xmlId:" + xml.getFacetSpecificationXMLId() + ", xsdId:" + xml.getFacetSpecificationXSDId() + ", typeName:" + xml.getFacetTypeName() + ", name:" + xml.getName() + ", provider:" + xml.getProviderId() + ", service:" + xml.getServiceId() + "]";
	}

	public static  String convert(FacetSpecificationXML[] xml) {
		if(xml == null) return "null";
		
		StringBuilder ret = new StringBuilder("{");
		for (int i = 0; i < xml.length; i++) {
			if(i != 0) ret.append(", ");
			ret.append(convert(xml[i]));
		}
		return ret.append("}").toString();
	}

	public static  String convert(ServiceProviderDescription spd) {
		if(spd == null) return "null";
		
		return "[" + spd.getId() + ", " + spd.getName() + "]";
	}
}
