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

package eu.secse.deliveryManager.notify.data;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries(value={
		@NamedQuery(name=Event.QUEUE_LENGHT,query="SELECT COUNT(e) FROM Event AS e WHERE e.email= :email"),
		@NamedQuery(name=Event.FIND_ALL, query="SELECT e FROM Event AS e WHERE e.email = :email ORDER BY e.date ASC"),
		@NamedQuery(name=Event.READ, query = " SELECT e FROM Event AS e WHERE e.email = :email AND e.read = TRUE ORDER BY e.date ASC")}
)
public class Event implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1595722692835234720L;
	
	public static final String QUEUE_LENGHT = "queue_lenght";
	
	public static final String FIND_ALL = "find_all_Event";
	
	public static final String READ = "read";
	
	@Id @GeneratedValue
	long id;
	
	private Timestamp date;
	
	/** user email **/
	private String email;
	
	/** the subject of the message */
	private String subject;
	
	/** the message body **/
	@Lob
	private String message;
	
	/** true if the service/facet/xml is added, false if service/facet/xml is deleted**/
	private boolean isAdded;
	
	/** the service Id **/
	private String serviceId;
	
	/** true if the message is read **/
	private boolean read;
	
	//TODO: inserire la data
	
	public Event(String email, String subject, String message,
			boolean isAdded, String serviceId) {
		super();
		this.email = email;
		this.subject = subject;
		this.message = message;
		this.isAdded = isAdded;
		this.serviceId = serviceId;
		date = new Timestamp(System.currentTimeMillis());
		read = false;
	}
	public String getSubject() {
		return subject;
	}
	public String getMessage() {
		return message;
	}
	public boolean isAdded() {
		return isAdded;
	}
	public String getServiceId() {
		return serviceId;
	}
	public String getEmail() {
		return email;
	}
	
	public boolean isRead() {
		return read;
	}
	public void setRead(boolean read) {
		this.read = read;
	}
	public Timestamp getDate() {
		return date;
	}
	public void setDate(Timestamp date) {
		this.date = date;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void setAdded(boolean isAdded) {
		this.isAdded = isAdded;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public long getId() {
		return id;
	}
	
	

}
