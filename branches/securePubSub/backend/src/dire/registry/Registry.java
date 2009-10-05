package dire.registry;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import dire.registry.data.Facet;
import dire.registry.data.RegistryId;
import dire.registry.data.Service;
import dire.registry.data.User;
import dire.registry.exception.AuthenticationException;
import dire.registry.Signature;
import dire.registry.ws.FacetWS;
import dire.registry.ws.ServiceWS;
import eu.secse.deliveryManager.core.IUpdate;

@Stateless
@WebService
public class Registry implements IRegistry {

	private static final Log log = LogFactory.getLog(IRegistry.class);

	@PersistenceContext(unitName = "registry")
	protected EntityManager em;

	private InitialContext ctx;

	@EJB
	IRegistryWriter regWriter;

	public long authenticate(@WebParam(name = "username") String username,
			@WebParam(name = "passwd") String password)
			throws AuthenticationException {
		return auth(username, password).getId();
	}

	private User auth(String username, String password)
			throws AuthenticationException {
		User u = User.authenticate(em, username, password);
		if (u == null)
			throw new AuthenticationException();
		return u;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public long registerServiceProvider(
			@WebParam(name = "username") String username,
			@WebParam(name = "passwd") String password,
			@WebParam(name = "nickname") String nickname) {
		User u = new User(username, password, nickname);
		em.persist(u);
		return u.getId();
	}

	public String getRegistryId() {
		return RegistryId.getRegistryId(em);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String deployService(
			@WebParam(name = "username") String username,
			@WebParam(name = "passwd") String password,
			@WebParam(name = "serviceId") String serviceID,
			@WebParam(name = "name") String name,
			@WebParam(name = "allowAddInfo") boolean allowAdditionalInformation,
			@WebParam(name = "version") String version,
			@WebParam(name = "previousVersionId") String previousVersionId,
			@WebParam(name = "timestamp") String timestamp)
			throws AuthenticationException {
		User u = auth(username, password);

		String ret = regWriter.reallyDeployService(serviceID, name,
				allowAdditionalInformation, version, previousVersionId,
				timestamp, u.getId());

		try {
			getUpdate().addService(ret, u.getId());
		} catch (Throwable t) {
			log.warn("Cannot contact the update.addService: " + t.getMessage()
					+ " due to: " + t.getCause());
		}

		return ret;
	}

	public ServiceWS getService(@WebParam(name = "serviceId") String serviceId) {
		return new ServiceWS(Service.getByFullId(em, serviceId));
	}

	public Signature getServiceSignature(
			@WebParam(name = "serviceId") String serviceID) {
		return Service.getByFullId(em, serviceID).getSignature();
	}

	public void setServiceSignature(@WebParam(name = "username") String username, @WebParam(name = "passwd") String password, @WebParam(name = "serviceId") String serviceID, @WebParam(name = "signature") Signature signature)
			throws AuthenticationException {
		User u = auth(username, password);
		Service s = Service.getByFullId(em, serviceID);

		if (!s.getProvider().equals(u))
			throw new AuthenticationException();

		s.setSignature(signature);
	}

	public void undeployService(@WebParam(name = "username") String username,
			@WebParam(name = "passwd") String password,
			@WebParam(name = "serviceId") String serviceId)
			throws AuthenticationException {
		User u = auth(username, password);
		Service s = Service.getByFullId(em, serviceId);

		if (!s.getProvider().equals(u))
			throw new AuthenticationException();

		try {
			getUpdate().deleteService(serviceId, u.getId());
		} catch (Throwable t) {
			log.warn("Cannot contact the update.deleteService: "
					+ t.getMessage() + " due to: " + t.getCause());
		}

		em.remove(s);
	}

	public String storeFacetXSD(@WebParam(name = "username") String username,
			@WebParam(name = "passwd") String password,
			@WebParam(name = "serviceId") String serviceId,
			@WebParam(name = "facet") FacetWS facet,
			@WebParam(name = "addInfo") boolean addInfo)
			throws AuthenticationException {
		User u = auth(username, password);
		Service s = Service.getByFullId(em, serviceId);

		Facet f = Facet.getBySchemaId(em, facet.getId());
		String ret;
		if (f == null) {
			if (addInfo && !s.getProvider().equals(u))
				throw new AuthenticationException();

			ret = regWriter.reallyStoreFacetXsd(facet, addInfo, u.getId(), s
					.getId());

			try {
				getUpdate().addFacetSpecificationSchema(serviceId, ret,
						addInfo, u.getId());
			} catch (Throwable t) {
				log
						.warn("Cannot contact the update.addFacetSpecificationSchema: "
								+ t.getMessage() + " due to: " + t.getCause());
			}
		} else {
			ret = f.getSchemaId();
		}

		return ret;
	}

	public FacetWS getFacetXsdById(@WebParam(name = "id") String id) {
		Facet f = Facet.getBySchemaId(em, id);
		if (f == null)
			return null;

		return new FacetWS(f.getProvider().getId(), f.getSchemaId(), f
				.getSchemaName(), f.getSchemaDocument(), f.getSchemaName(), f
				.getSchemaTimestamp());
	}

	public String storeFacetXML(@WebParam(name = "username") String username,
			@WebParam(name = "passwd") String password,
			@WebParam(name = "serviceId") String serviceId,
			@WebParam(name = "facetSchemaId") String xsdId,
			@WebParam(name = "facetXml") FacetWS xml) throws RemoteException {
		User u = auth(username, password);
		Service s = Service.getByFullId(em, serviceId);
		Facet f = Facet.getBySchemaId(em, xsdId);

		if (f == null)
			throw new RemoteException();
		if (!f.getService().equals(s))
			throw new AuthenticationException();
		if (!f.getProvider().equals(u))
			throw new AuthenticationException();

		String ret = regWriter.reallyStoreFacetXml(xml, f.getId());

		try {
			getUpdate().addFacetSpecificationXML(serviceId, xsdId, ret,
					f.isAddInfo(), u.getId());
		} catch (Throwable t) {
			log.warn("Cannot contact the update.addFacetSpecificationXML: "
					+ t.getMessage() + " due to: " + t.getCause());
		}

		return ret;
	}

	public void removeFacetXSD(@WebParam(name = "username") String username,
			@WebParam(name = "passwd") String password,
			@WebParam(name = "facetId") String facetId) throws RemoteException {
		User u = auth(username, password);
		Facet f = Facet.getBySchemaId(em, facetId);
		if (f == null)
			throw new RemoteException();

		if (!f.getProvider().equals(u))
			throw new AuthenticationException();

		try {
			getUpdate().deleteFacetFacetSpecificationXML(
					f.getService().getFullId(), f.getSchemaId(), f.getXmlId(),
					f.isAddInfo(), f.getProvider().getId());
		} catch (Throwable t) {
			log
					.warn("Cannot contact the update.deleteFacetFacetSpecificationXML: "
							+ t.getMessage() + " due to: " + t.getCause());
		}

		em.remove(f);
	}

	public FacetWS getFacetXmlBySchemaId(
			@WebParam(name = "facetSchemaId") String facetSchemaId) {
		Facet f = Facet.getBySchemaId(em, facetSchemaId);
		if (f == null)
			return null;

		return new FacetWS(f.getProvider().getId(), f.getXmlId(), f
				.getXmlName(), f.getXmlDocument(), f.getXmlName(), f
				.getXmlTimestamp());
	}

	public void removeFacetXml(@WebParam(name = "username") String username,
			@WebParam(name = "passwd") String password,
			@WebParam(name = "xmlId") String xmlId) throws RemoteException {
		User u = auth(username, password);
		Facet f = Facet.getByXmlId(em, xmlId);
		if (f == null)
			throw new RemoteException();

		if (!f.getProvider().equals(u))
			throw new AuthenticationException();

		try {
			getUpdate().deleteFacetSpecificationSchema(
					f.getService().getFullId(), f.getSchemaId(), f.isAddInfo(),
					f.getProvider().getId());
		} catch (Throwable t) {
			log
					.warn("Cannot contact the update.deleteFacetSpecificationSchema: "
							+ t.getMessage() + " due to: " + t.getCause());
		}

		f.setXmlId(null);
		f.setXmlName(null);
		f.setXmlDocument(null);
		f.setXmlTimestamp(null);
	}

	public Signature getFacetAddInfoSignature(@WebParam(name = "serviceId") String serviceId, @WebParam(name = "schemaId") String schemaId) throws RemoteException {
		return Facet.getBySchemaId(em, schemaId).getSignature();
	}
	
	public void setFacetAddInfoSignature(@WebParam(name = "username") String username, @WebParam(name = "password") String password, @WebParam(name = "serviceId") String serviceID, @WebParam(name = "schemaId") String schemaId, @WebParam(name = "signature") Signature signature) throws AuthenticationException, IllegalStateException, RemoteException {
		User u = auth(username, password);
		Facet f = Facet.getBySchemaId(em, schemaId);

		if (!f.isAddInfo())
			throw new IllegalStateException(
					"cannot add the signature to a specification facet: update the service's signature instead!!!");
		if (!f.getProvider().equals(u))
			throw new AuthenticationException();

		f.setSignature(signature);
	}

	public boolean isAdditionalInformation(@WebParam(name = "facetId") String id) {
		Facet f = Facet.getBySchemaId(em, id);
		return f.isAddInfo();
	}

	public String getServiceIdByFacetId(@WebParam(name = "facetId") String facetId) {
		Facet f = Facet.getBySchemaId(em, facetId);
		return f.getService().getFullId();
	}

	public Collection<FacetWS> getFacetSpecXSDs(
			@WebParam(name = "serviceId") String serviceID) {
		Collection<FacetWS> ret = new ArrayList<FacetWS>();
		Service s = Service.getByFullId(em, serviceID);

		for (Facet f : s.getFacets()) {
			if (!f.isAddInfo())
				ret.add(new FacetWS(f.getProvider().getId(), f.getSchemaId(), f
						.getSchemaName(), f.getSchemaDocument(), f
						.getSchemaTypeName(), f.getSchemaTimestamp()));
		}

		return ret;
	}

	public Collection<FacetWS> getFacetAddInfoXSDs(
			@WebParam(name = "serviceId") String serviceID) {
		Collection<FacetWS> ret = new ArrayList<FacetWS>();
		Service s = Service.getByFullId(em, serviceID);

		for (Facet f : s.getFacets()) {
			if (f.isAddInfo())
				ret.add(new FacetWS(f.getProvider().getId(), f.getSchemaId(), f
						.getSchemaName(), f.getSchemaDocument(), f
						.getSchemaTypeName(), f.getSchemaTimestamp()));
		}

		return ret;
	}

	private IUpdate getUpdate() throws NamingException {
		if (ctx == null)
			ctx = new InitialContext();
		return (IUpdate) ctx.lookup("deliverymanager/Update/local");
	}

	@SuppressWarnings("unchecked")
	public Collection<String> getAllServiceIds() {
		Collection<String> ret = new ArrayList<String>();
		Collection<String> sids = em.createQuery("SELECT fullId FROM Service")
				.getResultList();

		ret.addAll(sids);
		return ret;
	}
}
