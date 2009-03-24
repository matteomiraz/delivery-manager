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

package eu.secse.federationDirectory.redsintegration;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.management.MBeanServer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.mx.util.MBeanProxyExt;
import org.jboss.mx.util.MBeanServerLocator;

import eu.secse.deliveryManager.utils.IConfiguration;
import eu.secse.federationDirectory.IDirectoryMBean;
import eu.secse.federationDirectory.IDirectoryModelManager;
import eu.secse.federationDirectory.adapters.IFederationDataConverter;
import eu.secse.federationDirectory.db.FederationData;
import eu.secse.federationDirectory.reds.messages.EndpointInfo;
import eu.secse.federationDirectory.reds.messages.FederationCreation;
import eu.secse.federationDirectory.reds.messages.FederationRemoval;
import eu.secse.federationDirectory.reds.types.RedsFederationData;
import eu.secse.federationDirectory.wsclient.IDirectoryProxy;


/**
 * Receive the update and the modification from the <b>other</b> registries.
 * 
 * @author matteo
 */
@MessageDriven(activationConfig = {
@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/reds-directory")
})
public class JMSListener implements MessageListener {
	
	/** Which synchronization technique is used:
	 * MANUAL : manual through Mbean
	 * FIRST: The first answering directory 
	 * 
	 * @author ercasta
	 *
	 */
	private enum SyncStyle {
		
		SYNC_MANUAL("Manual"),
		SYNC_FIRST("First");
		
		private String value;
		
		SyncStyle(String value) {
			this.value=value;
		}
		
		String getValue() {
			return value;
		}
	} 
	private static final Log log = LogFactory.getLog(JMSListener.class);

	@EJB 
	private IDirectoryModelManager modelmanager;
	
	@EJB
	private IDirectoryMBean managingbean;
	
	@EJB
	private IConfiguration config;
	
	@EJB
	private IDirectoryProxy dirproxy;
	
	@EJB
	private IFederationDataConverter converter;
	
	@PostConstruct
	public void myInit() {
		try {
	        MBeanServer server = MBeanServerLocator.locate();
	        managingbean = (IDirectoryMBean) MBeanProxyExt.create(IDirectoryMBean.class, "FederationDirectory:service=fedlist", server);
	        
	    } catch (Exception e) {
	        log.warn(e.getMessage());		        
	    }
	}
	
	public void onMessage(Message msg) {

		// receive an objectMessage
		if(msg instanceof ObjectMessage) {
			try {
				Serializable obj = ((ObjectMessage)msg).getObject(); 
				log.info("Received " + obj.getClass().getSimpleName());
				
				//TODO: Handle other kinds of messages (which kinds?)
				if (obj instanceof FederationCreation) {
					FederationCreation fedcreat=(FederationCreation)obj;
					FederationData data=converter.convertData(fedcreat.getFed());									
					modelmanager.addFederationData(data);
				} else if (obj instanceof FederationRemoval) {
					FederationRemoval removal=(FederationRemoval)obj;
					String federationid=removal.getFederationid();
					modelmanager.removeFederation(federationid);			
				} else if (obj instanceof EndpointInfo) {
//					This must be a reply to our request					
					EndpointInfo ep=(EndpointInfo)obj;
					String endpoint=ep.getEndpoint();
					managingbean.addOtherDirectoryEndpoint(endpoint);
					String synchstyle=config.getString("Directory.synchronize");
					if (synchstyle==null) {
						synchstyle=SyncStyle.SYNC_MANUAL.getValue();
						log.warn("Missing Directory.synchronize property, using \"Manual\"");
					}
					
					if (synchstyle.equals(SyncStyle.SYNC_MANUAL.getValue())) {
						log.info("Nothing to do:manual synchronization");
					} else if (synchstyle.equals(SyncStyle.SYNC_FIRST.getValue()) && managingbean.mustSynchronize()) {						
						log.info("First answer received and automatic, first answer synchronization selected: synchronizing");						
						Collection<RedsFederationData> dbdata=dirproxy.getAllFederations(endpoint);
						for (RedsFederationData d:dbdata) {							
							modelmanager.addFederationData(converter.convertData(d));
						}
						managingbean.setSynchronized(true);
					} else {
						log.warn("Invalid Directory.synchronize property value " + synchstyle + ", using \"Manual\"");
					}
										
				} else {
					log.warn("Received unknown message: " + obj.getClass().getCanonicalName());
				}
								
			} catch (JMSException e) {
				log.warn("Cannot receive the message through the JMS queue. Error: " + e.getMessage());
			}
		} else 
			log.warn("Received a non-object message: " + msg.getClass().getSimpleName());
	}
}
