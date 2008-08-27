package eu.secse.deliveryManager.federations.securepubsub.core;

import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
@WebService
public class UserGrant implements IUserGrant {

	@PersistenceContext(unitName="deliveryManager")
	protected EntityManager em;

	@EJB ISecPubSubProxy secureProxy;
	
	public void receivedReadRequest(String federationId, PublicKey userKey, String name, X509Certificate certificate) {
		SecureFederationUser user = SecureFederationUser.findUser(em, federationId, userKey);
		if(user != null) {
			if(user.isBanned()) return;
			if(user.isCanRead()) {
				secureProxy.allowReadingPermission(user);
				return;
			}
		} else {
			user = new SecureFederationUser(name, federationId, userKey, certificate);
			em.persist(user);
		}
		
		user.setWantsRead(true);
	}
	
	@WebMethod @WebResult(name="PendingRequests")
	public Collection<UserRequest> getJoinedRequests(@WebParam(name="federationId")String federationId) throws CertificateEncodingException {
		Collection<SecureFederationUser> req = SecureFederationUser.getAll(em, federationId, true, false, null, null, null, false);
		Collection<UserRequest> ret = new ArrayList<UserRequest>();
		
		for (SecureFederationUser e : req)
			ret.add(new UserRequest(e.getId(), e.getFederation(), e.getName(), e.getCertificate()));
		
		return ret;
	}
	
	@WebMethod
	public void approveJoinRequest(@WebParam(name="id")long id) {
		SecureFederationUser user = em.find(SecureFederationUser.class, id);
		
		if(user == null) throw new NoSuchElementException("The request with id " + id + " doesn't exist!");
		
		if(user.isWantsRead()) {
			user.setWantsRead(false);
			user.setCanRead(true);
			secureProxy.allowReadingPermission(user);
		} else 
			throw new NoSuchElementException("The user with id " + id + " doesn't want to join!");
	}
	
	@WebMethod  @WebResult(name="members")
	public Collection<UserRequest> listMembers(@WebParam(name="federationId")String federationId) throws CertificateEncodingException {
		Collection<SecureFederationUser> req = SecureFederationUser.getAll(em, federationId, null, true, null, null, null, null);
		Collection<UserRequest> ret = new ArrayList<UserRequest>();
		
		for (SecureFederationUser e : req)
			ret.add(new UserRequest(e.getId(), e.getFederation(), e.getName(), e.getCertificate()));
		
		return ret;
	}
	
	/** The user doesn't wants to receive the federation's messages anymore... notice that is the user that wants to quit (he is not banned!) */
	public void discardReadPermissions(String federationId, PublicKey userKey) {
		SecureFederationUser user = SecureFederationUser.findUser(em, federationId, userKey);
		if(user != null) {
			if(user.isCanWrite()) {
				user.setCanWrite(false);
				secureProxy.removeWritingPermission(user);
			}

			if(user.isCanRead()) {
				user.setCanRead(false);
				secureProxy.removeReadingPermission(user);
			}
		}
	}
	
	@WebMethod
	public void banUser(@WebParam(name="permissionId") long id) {
		SecureFederationUser user = em.find(SecureFederationUser.class, id);
		
		if(user == null) throw new NoSuchElementException("The request with id " + id + " doesn't exist!");
		
		user.setBanned(true);
		user.setWantsRead(false);
		user.setWantsWrite(false);
		
		if(user.isCanWrite()) {
			user.setCanWrite(false);
			secureProxy.removeWritingPermission(user);
		}

		if(user.isCanRead()) {
			user.setCanRead(false);
			secureProxy.removeReadingPermission(user);
		}
	}

	@WebMethod
	public void revokeReadPermissions(@WebParam(name="permissionId") long id) {
		SecureFederationUser user = em.find(SecureFederationUser.class, id);
		
		if(user == null) throw new NoSuchElementException("The request with id " + id + " doesn't exist!");
		
		if(user.isCanWrite()) {
			user.setCanWrite(false);
			secureProxy.removeWritingPermission(user);
		}

		if(user.isCanRead()) {
			user.setCanRead(false);
			secureProxy.removeReadingPermission(user);
		}
	}
	
	@WebMethod @WebResult(name="bannedUsers")
	public Collection<UserRequest> listBannedUsers(@WebParam(name="federationId")String federationId) throws CertificateEncodingException {
		Collection<SecureFederationUser> req = SecureFederationUser.getAll(em, federationId, null, null, null, null, null, true);
		Collection<UserRequest> ret = new ArrayList<UserRequest>();
		
		for (SecureFederationUser e : req)
			ret.add(new UserRequest(e.getId(), e.getFederation(), e.getName(), e.getCertificate()));
		
		return ret;
	}
	
	@WebMethod
	public void revokeBan(@WebParam(name="permissionId") long id) {
		SecureFederationUser user = em.find(SecureFederationUser.class, id);
		
		if(user == null) throw new NoSuchElementException("The request with id " + id + " doesn't exist!");
		
		if(user.isBanned()) {
			user.setBanned(false);
			user.setWantsRead(false);
			user.setCanRead(false);
			user.setWantsWrite(false);
			user.setCanWrite(false);
		} else 
			throw new NoSuchElementException("The user with id " + id + " is not banned!");

	}
	
	public void receivedWriteRequest(String federationId, PublicKey userKey, String name, X509Certificate certificate) {
		SecureFederationUser user = SecureFederationUser.findUser(em, federationId, userKey);
		if(user != null) {
			if(user.isBanned()) return;
			if(user.isCanWrite()) {
				secureProxy.allowWritingPermission(user);
				return;
			}
		} else {
			user = new SecureFederationUser(name, federationId, userKey, certificate);
			user.setWantsRead(true);
			em.persist(user);
		}
		
		user.setWantsWrite(true);
	}

	@WebMethod @WebResult(name="PendingRequests")
	public Collection<UserRequest> getWritingRequests(@WebParam(name="federationId")String federationId) throws CertificateEncodingException {
		Collection<SecureFederationUser> req = SecureFederationUser.getAll(em, federationId, null, true, true, false, null, false);
		Collection<UserRequest> ret = new ArrayList<UserRequest>();
		
		for (SecureFederationUser e : req)
			ret.add(new UserRequest(e.getId(), e.getFederation(), e.getName(), e.getCertificate()));
		
		return ret;
	}
	
	@WebMethod
	public void approveWriteRequest(@WebParam(name="id")long id) {
		SecureFederationUser user = em.find(SecureFederationUser.class, id);
		
		if(user == null) throw new NoSuchElementException("The request with id " + id + " doesn't exist!");

		if(user.isCannotWrite()) throw new IllegalStateException("The selected user has the writing permission revoked.");
		
		if(user.isWantsWrite()) {
			user.setWantsWrite(false);
			user.setCanWrite(true);
			secureProxy.allowReadingPermission(user);
		} else 
			throw new NoSuchElementException("The user with id " + id + " doesn't want the writing permission!");
	}
	
	@WebMethod  @WebResult(name="writingMembers")
	public Collection<UserRequest> listWritingMembers(@WebParam(name="federationId")String federationId) throws CertificateEncodingException {
		Collection<SecureFederationUser> req = SecureFederationUser.getAll(em, federationId, null, true, null, true, null, false);
		Collection<UserRequest> ret = new ArrayList<UserRequest>();
		
		for (SecureFederationUser e : req)
			ret.add(new UserRequest(e.getId(), e.getFederation(), e.getName(), e.getCertificate()));
		
		return ret;
	}
	
	/** The user doesn't wants to send messages to this federation... */
	public void discardWritePermissions(String federationId, PublicKey userKey) {
		SecureFederationUser user = SecureFederationUser.findUser(em, federationId, userKey);
		if(user != null) {
			if(user.isCanWrite()) {
				user.setCanWrite(false);
				secureProxy.removeWritingPermission(user);
			}
		}
	}
	
	@WebMethod
	public void revokeWritePermissions(@WebParam(name="permissionId") long id) {
		SecureFederationUser user = em.find(SecureFederationUser.class, id);
		
		if(user == null) throw new NoSuchElementException("The request with id " + id + " doesn't exist!");
		
		if(user.isCanWrite()) {
			user.setCanWrite(false);
			user.setCannotWrite(true);
			secureProxy.removeWritingPermission(user);
		}
	}
	
	@WebMethod @WebResult(name="RevokedWritingPermissions")
	public Collection<UserRequest> getRevokedWritingPermissions(@WebParam(name="federationId")String federationId) throws CertificateEncodingException {
		Collection<SecureFederationUser> req = SecureFederationUser.getAll(em, federationId, null, null, null, null, true, null);
		Collection<UserRequest> ret = new ArrayList<UserRequest>();
		
		for (SecureFederationUser e : req)
			ret.add(new UserRequest(e.getId(), e.getFederation(), e.getName(), e.getCertificate()));
		
		return ret;
	}
	
	@WebMethod
	public void discardWritingRevoke(@WebParam(name="permissionId") long id) {
		SecureFederationUser user = em.find(SecureFederationUser.class, id);
		
		if(user == null) throw new NoSuchElementException("The request with id " + id + " doesn't exist!");
		
		if(user.isCannotWrite())
			user.setCannotWrite(false);
	}

	public void discardFederation(String federationId) {
		Collection<SecureFederationUser> users = SecureFederationUser.getAll(em, federationId, null, null, null, null, null, null);			
		
		for (SecureFederationUser u : users)
			em.remove(u);
	}
}


class UserRequest {
	long id;
	String userName;
	String federationId;
	private byte[] certificate;
	
	public UserRequest(long id, String federationId, String userName, X509Certificate certificate) throws CertificateEncodingException {
		this.id = id;
		this.federationId = federationId;
		this.userName = userName;
		this.certificate = certificate.getEncoded();
	}

	UserRequest() { }

	public long getId() {
		return id;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public String getFederationId() {
		return federationId;
	}
	
	public byte[] getCertificate() {
		return certificate;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setFederationId(String federationId) {
		this.federationId = federationId;
	}

	public void setCertificate(byte[] certificate) {
		this.certificate = certificate;
	}
}