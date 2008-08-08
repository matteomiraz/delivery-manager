package eu.secse.deliveryManager.federations.securepubsub.core;

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Query;

@Entity
@NamedQueries({
	@NamedQuery(name=SecureFederationUser.FIND_USER, query="FROM SecureFederationUser WHERE federation=:federation AND userKey=:userKey"),
	@NamedQuery(name=SecureFederationUser.GET_ALL, query="FROM SecureFederationUser WHERE (:ignoreFederation OR federation=:federation) " +
			"AND (:ignoreWantsRead OR wantsRead=:wantsRead) AND (:ignoreRead OR read=:read) AND (:ignoreWantsWrite OR wantsWrite=:wantsWrite) " +
			"AND (:ignoreWrite OR write=:write) AND (:ignoreCannotWrite OR cannotWrite=:cannotWrite) AND (:ignoreBanned OR banned=:banned)")
})
public class SecureFederationUser {
	static final String FIND_USER = "SecureFederationUser-FIND_USER";
	static final String GET_ALL = "SecureFederationUser-GET_ALL";
	
	@Id 
	@GeneratedValue
	private long id;

	private String name;
	
	/** The federation the user wants to join */ 
	private String federation;

	/** Public key of the user */
	@Lob
	private PublicKey userKey;
	
	/** The user's writing certificate */
	@Lob
	private X509Certificate certificate;
	
	private boolean canRead, canWrite, cannotWrite, banned;
	private boolean wantsRead, wantsWrite;
	
	SecureFederationUser() { }
	public SecureFederationUser(String name, String federation, PublicKey userKey, X509Certificate certificate) {
		this.name = name;
		this.federation = federation;
		this.userKey = userKey;
		this.certificate = certificate;
		this.certificate = null;
	}
	
	public long getId() {
		return id;
	}
	
	public String getFederation() {
		return federation;
	}
	
	public String getName() {
		return name;
	}
	
	public PublicKey getUserKey() {
		return userKey;
	}
	
	public X509Certificate getCertificate() {
		return certificate;
	}
	
	public boolean isCanRead() {
		return canRead;
	}

	public void setCanRead(boolean canRead) {
		this.canRead = canRead;
	}
	
	public boolean isCanWrite() {
		return canWrite;
	}
	
	public void setCanWrite(boolean canWrite) {
		this.canWrite = canWrite;
	}

	public boolean isCannotWrite() {
		return cannotWrite;
	}
	
	public void setCannotWrite(boolean cannotWrite) {
		this.cannotWrite = cannotWrite;
	}
	
	public boolean isBanned() {
		return banned;
	}
	public void setBanned(boolean banned) {
		this.banned = banned;
	}
	public boolean isWantsRead() {
		return wantsRead;
	}
	public void setWantsRead(boolean wantsRead) {
		this.wantsRead = wantsRead;
	}
	public boolean isWantsWrite() {
		return wantsWrite;
	}
	public void setWantsWrite(boolean wantsWrite) {
		this.wantsWrite = wantsWrite;
	}
	public static SecureFederationUser findUser(EntityManager em, String federation, PublicKey userKey) {
		try {
			return (SecureFederationUser) em.createNamedQuery(SecureFederationUser.FIND_USER)
			  		 .setParameter("federation", federation)
			  		 .setParameter("userKey", userKey).getSingleResult();
		} catch (Throwable e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static Collection<SecureFederationUser> getAll(EntityManager em, String federationId, Boolean wantsRead, Boolean read, Boolean wantsWrite, Boolean write, Boolean cannotWrite, Boolean banned) {
		Query query = em.createNamedQuery(SecureFederationUser.GET_ALL);

		if(federationId == null) query.setParameter("ignoreFederation", true).setParameter("federation", "nessuna");
		else query.setParameter("ignoreFederation", false).setParameter("federation", federationId);

		if(wantsRead == null) query.setParameter("ignoreWantsRead", true).setParameter("wantsRead", true);
		else query.setParameter("ignoreWantsRead", false).setParameter("wantsRead", wantsRead);

		if(read == null) query.setParameter("ignoreRead", true).setParameter("read", true);
		else query.setParameter("ignoreRead", false).setParameter("read", read);

		if(wantsWrite == null) query.setParameter("ignoreWantsWrite", true).setParameter("wantsWrite", true);
		else query.setParameter("ignoreWantsWrite", false).setParameter("wantsWrite", wantsWrite);

		if(write == null) query.setParameter("ignoreWrite", true).setParameter("write", true);
		else query.setParameter("ignoreWrite", false).setParameter("write", write);

		if(cannotWrite == null) query.setParameter("ignoreCannotWrite", true).setParameter("cannotWrite", true);
		else query.setParameter("ignoreCannotWrite", false).setParameter("cannotWrite", cannotWrite);

		if(banned == null) query.setParameter("ignoreBanned", true).setParameter("banned", true);
		else query.setParameter("ignoreBanned", false).setParameter("banned", banned);

		return (Collection<SecureFederationUser>) query.getResultList();
	}
}
