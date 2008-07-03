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

package eu.secse.federationDirectory.adapters;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import javax.ejb.Stateless;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import eu.secse.federationDirectory.db.FederationData;
import eu.secse.federationDirectory.db.FederationProperty;
import eu.secse.federationDirectory.db.UniqueID;
import eu.secse.federationDirectory.reds.types.RedsFederationData;
import eu.secse.federationDirectory.reds.types.RedsFederationProperty;
import eu.secse.federationDirectory.wstypes.WSFederationData;
import eu.secse.federationDirectory.wstypes.WSFederationDataArray;

@Stateless
public class FederationDataConverter implements IFederationDataConverter{
	
	public FederationData convertData(RedsFederationData data) {
		if (data==null) return null;
		FederationData fed=new FederationData(data.getId().getId());
		fed.setMethod(data.getMethod());
		fed.setLeaseExpiration(data.getLeaseExpiration().getTime());
		fed.setName(data.getName());
		Vector<FederationProperty> properties=new Vector<FederationProperty>();
		for (RedsFederationProperty prop:data.getProperties()) {
			FederationProperty fedprop=new FederationProperty(prop.getName(),prop.getValue());
			properties.add(fedprop);
		}
		fed.setProperties(properties);		
		return fed;
	}
	
	public RedsFederationData convertData(FederationData data) {
		if (data==null) return null;
		RedsFederationData fed=new RedsFederationData(data.getId().getId());
		fed.setMethod(data.getMethod());
		GregorianCalendar cal=new GregorianCalendar();
		cal.setTimeInMillis(data.getLeaseExpiration());
		fed.setLeaseExpiration(cal.getTime());
		fed.setName(data.getName());
		Vector<RedsFederationProperty> properties=new Vector<RedsFederationProperty>();
		for (FederationProperty prop:data.getProperties()) {
			RedsFederationProperty fedprop=new RedsFederationProperty(prop.getName(),prop.getValue());
			properties.add(fedprop);
		}
		fed.setProperties(properties);		
		return fed;
	}
	
	public WSFederationData convertWSData(FederationData data) {
		if (data==null) return null;
		Document d=new Document();
		d.setRootElement(convertWSElement(data));
		XMLOutputter out=new XMLOutputter();
		String fed=out.outputString(d);		
		WSFederationData wsdata=new WSFederationData(fed);
		return wsdata;
	}
	
	private Element convertWSElement(FederationData data) {
		if (data==null) return null;
		Element root=new Element("federation");		
		root.setAttribute("uniqueid",data.getId().getId());
		root.setAttribute("name",data.getName());
		root.setAttribute("method",data.getMethod());
		root.setAttribute("lease",data.getLeaseExpiration() + "");		
		Collection<FederationProperty> properties=data.getProperties();							
		Element props=new Element("properties");			
		for (FederationProperty prop:properties) {
			Element propele=new Element("property");
			propele.setAttribute("name",prop.getName());
			propele.setAttribute("value",prop.getValue());					
			props.addContent(propele);		
		}
		root.addContent(props);
		return root;
	}
	
	@SuppressWarnings("unchecked")
	private FederationData convertWSElement(Element root) {
		if (root==null) return null;
		FederationData feddata=new FederationData();
		feddata.setName(root.getAttributeValue("name"));
		feddata.setId(new UniqueID(root.getAttributeValue("uniqueid")));
		feddata.setMethod(root.getAttributeValue("method"));
		feddata.setLeaseExpiration(Long.parseLong(root.getAttributeValue("lease")));		
		List<Element> elements=(List<Element>)root.getChild("properties").getChildren();
		Collection<FederationProperty> properties=new Vector<FederationProperty>();
		for (Element ele: elements) {
			String name=ele.getAttributeValue("name");
			String value=ele.getAttributeValue("value");
			properties.add(new FederationProperty(name,value));
		}
		feddata.setProperties(properties);
		return feddata;
	}
	
	public WSFederationDataArray convert(FederationData[] data) {
		if (data==null) return null;
		Element root=new Element("federations");		
		for (FederationData d:data) {
			Element converteddata=convertWSElement(d);
			if (converteddata!=null) {
				root.addContent(converteddata);
			}
		}
		Document d=new Document();
		d.setRootElement(root);
		XMLOutputter out=new XMLOutputter();
		
		return new WSFederationDataArray(out.outputString(d));
	}
	public FederationData convertWSData(WSFederationData data) {
		if (data==null) return null;
		
		String doc=data.getData();
		SAXBuilder builder=new SAXBuilder();
		Document d;		
		try {
			d = builder.build(new StringReader(doc));
			Element root=d.getRootElement();
			return convertWSElement(root);
			
			
		} catch (JDOMException e) {
			//Ignore properties 
		} catch (IOException e) {
			//Ignore properties			
		}		
		return null;
	}

	@SuppressWarnings("unchecked")
	public FederationData[] convert(WSFederationDataArray data) {
		if (data==null) return null;
		String xmldata=data.getData();
		SAXBuilder builder =new SAXBuilder();		
		try {
			Document d=builder.build(new StringReader(xmldata));
			List<Element> federations=(List<Element>)d.getRootElement().getChildren();
			FederationData[] dbdata=new FederationData[federations.size()];
			int i=0;
			for (Element e: federations) {
				dbdata[i]=convertWSElement(e);
				i++;
			}
			return dbdata;
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
