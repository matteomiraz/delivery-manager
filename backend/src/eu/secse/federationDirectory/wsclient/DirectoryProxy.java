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

package eu.secse.federationDirectory.wsclient;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.ejb.Stateless;
import javax.xml.rpc.ServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import eu.secse.federationDirectory.reds.types.RedsFederationData;
import eu.secse.federationDirectory.reds.types.RedsFederationProperty;
import eu.secse.federationDirectory.reds.types.RedsUniqueID;
import eu.secse.federationDirectory.webservice.FederationDirectoryLocator;
import eu.secse.federationDirectory.webservice.IDirectoryLookupWS;

@Stateless
public class DirectoryProxy implements IDirectoryProxy {
	
	private static final Log log= LogFactory.getLog(IDirectoryProxy.class);
	
	public Collection<RedsFederationData> getAllFederations(String endpointurl) {
		try {			
			IDirectoryLookupWS port=locate(endpointurl);			
			return convert(port.getAllFederations());
		} catch (MalformedURLException e) {
			log.error("Invalid url:" + endpointurl);
			e.printStackTrace();
			return null;
		} catch (ServiceException e) {
			log.error("Service invocation failed");
			e.printStackTrace();
			return null;
		} catch (RemoteException re) {
			log.error("Service invocation failed (remote exception)");
			re.printStackTrace();
			return null;
		}
		
	}

	public Collection<RedsFederationData> searchFederationByName(String nameRegEx, String endpointurl) {
		try {
			IDirectoryLookupWS port=locate(endpointurl);			
			return convert(port.searchFederationByName(nameRegEx));
		} catch (MalformedURLException e) {
			log.error("Invalid url:" + endpointurl);
			e.printStackTrace();
			return null;
		} catch (ServiceException e) {
			log.error("Service invocation failed");
			e.printStackTrace();
			return null;
		} catch (RemoteException re) {
			log.error("Service invocation failed (remote exception)");
			re.printStackTrace();
			return null;
		}
	}

	public RedsFederationData searchFederationByUid(String uid, String endpointurl) {
		try {
			IDirectoryLookupWS port=locate(endpointurl);			
			return convert(port.searchFederationByUid(uid));
		} catch (MalformedURLException e) {
			log.error("Invalid url:" + endpointurl);
			e.printStackTrace();
			return null;
		} catch (ServiceException e) {
			log.error("Service invocation failed");
			e.printStackTrace();
			return null;
		} catch (RemoteException re) {
			log.error("Service invocation failed (remote exception)");
			re.printStackTrace();
			return null;
		}
	}
	
	private IDirectoryLookupWS locate(String url) throws MalformedURLException, ServiceException {
		FederationDirectoryLocator locator= new FederationDirectoryLocator();
		return locator.getIDirectoryLookupWSPort(new URL(url));		
	}	
	
	@SuppressWarnings("unchecked")
	private RedsFederationData convertWSElement(Element root) {
		if (root==null) return null;
		RedsFederationData feddata=new RedsFederationData();
		feddata.setName(root.getAttributeValue("name"));
		
		feddata.setId(new RedsUniqueID(root.getAttributeValue("uniqueid")));
		feddata.setMethod(root.getAttributeValue("method"));
	
		Date d=new Date();
		d.setTime(Long.parseLong(root.getAttributeValue("lease")));
		feddata.setLeaseExpiration(d);		
		List<Element> elements=(List<Element>)root.getChild("properties").getChildren();
		Collection<RedsFederationProperty> properties=new Vector<RedsFederationProperty>();
		for (Element ele: elements) {
			String name=ele.getAttributeValue("name");
			String value=ele.getAttributeValue("value");
			properties.add(new RedsFederationProperty(name,value));
		}
		feddata.setProperties(properties);
		return feddata;
	}
	
	@SuppressWarnings("unchecked")
	private Collection<RedsFederationData> convert(eu.secse.federationDirectory.webservice.WSFederationDataArray wsdata) {
		if (wsdata==null) return null;
		String xmldata=wsdata.getData();
		SAXBuilder builder =new SAXBuilder();		
		try {
			Document d=builder.build(new StringReader(xmldata));
			List<Element> federations=(List<Element>)d.getRootElement().getChildren();
			Collection<RedsFederationData> dbdata=new Vector<RedsFederationData>();
			int i=0;
			for (Element e: federations) {
				dbdata.add(convertWSElement(e));
				i++;
			}
			return dbdata;
		} catch (JDOMException e) {
			log.debug("Conversion failed"); 
			e.printStackTrace();
		} catch (IOException e) {
			log.debug("Conversion failed");
			e.printStackTrace();
		}
		return null;
	}
			
	
	private RedsFederationData convert(eu.secse.federationDirectory.webservice.WSFederationData wsdata) {
		String data=wsdata.getData();		
	    if (data==null) return null;
		
		String doc=data;
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
		
}
