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

package eu.secse.deliveryManager.notify.core;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.secse.deliveryManager.notify.data.Event;

@Stateless
public class Mailer implements ISender, IMailer{

private final static Log log = LogFactory.getLog(Mailer.class);
	
	@Resource(mappedName="java:/Mail") private Session session;

	public void send(Event e) {
		Message msg = new  MimeMessage(session);
		
		try {
			msg.setRecipient(Message.RecipientType.TO, new InternetAddress(e.getEmail()));
			msg.setSubject(e.getSubject());
			msg.setContent(e.getMessage(), "text/plain");
			Transport.send(msg);
		} catch (MessagingException ex) {
			log.warn("Cannot send the email to " + e.getEmail() + ": " + ex.getMessage() + " due to: " + ex.getCause());
		}
	}

	public void send(String to, String subject, String message) {
		Message msg = new  MimeMessage(session);
		
		try {
			msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
			msg.setSubject(subject);
			msg.setContent(message, "text/plain");
			Transport.send(msg);
		} catch (MessagingException ex) {
			log.warn("Cannot send the email to " + to + ": " + ex.getMessage() + " due to: " + ex.getCause());
		}
		
	}
	

}
