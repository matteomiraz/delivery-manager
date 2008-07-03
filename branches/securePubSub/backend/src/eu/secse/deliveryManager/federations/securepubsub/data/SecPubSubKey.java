package eu.secse.deliveryManager.federations.securepubsub.data;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class SecPubSubKey {

	@Id
	@GeneratedValue
	private long id;
	
	private long version;
	
	@Lob
	private Serializable key;

	SecPubSubKey() { }

	public SecPubSubKey(long version, Serializable key) {
		this.version = version;
		this.key = key;
	}
	
	public long getId() {
		return id;
	}

	public long getVersion() {
		return version;
	}

	public Serializable getKey() {
		return key;
	}
	
	
}
