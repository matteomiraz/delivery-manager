package eu.secse.deliveryManager.federations.securepubsub.data;


import java.security.Key;
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
	private Key federationKey;

	SecPubSubKey() { }

	public SecPubSubKey(long version, Key federationKey) {
		this.version = version;
		this.federationKey = federationKey;
	}
	
	public long getId() {
		return id;
	}

	public long getVersion() {
		return version;
	}

	public Key getKey() {
		return federationKey;
	}
	
	
}
