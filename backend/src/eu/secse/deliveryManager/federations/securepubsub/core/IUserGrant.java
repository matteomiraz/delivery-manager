package eu.secse.deliveryManager.federations.securepubsub.core;

import java.security.PublicKey;
import java.security.cert.X509Certificate;

import javax.ejb.Local;

@Local
public interface IUserGrant {
	public void receivedReadRequest(String federationId, PublicKey userKey, String name, X509Certificate certificate);
	public void discardReadPermissions(String federationId, PublicKey userKey);
	
	public void receivedWriteRequest(String federationId, PublicKey userKey, String name, X509Certificate certificate);
	public void discardWritePermissions(String federationId, PublicKey userKey);
	
	public void discardFederation(String federationId);
}


// TODO: storia timeout degli elementi (problema con le firme)