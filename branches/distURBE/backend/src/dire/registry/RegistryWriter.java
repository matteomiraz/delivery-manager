package dire.registry;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import dire.registry.data.Facet;
import dire.registry.data.RegistryId;
import dire.registry.data.Service;
import dire.registry.data.User;
import dire.registry.ws.FacetWS;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class RegistryWriter implements IRegistryWriter {

	@PersistenceContext(unitName="registry")
	protected EntityManager em;

	public String reallyDeployService(String serviceID, String name, boolean allowAdditionalInformation, String version, String previousVersionId, String timestamp, int userId) {
		User u = em.find(User.class, userId);
		
		Service s = new Service(serviceID, u, name, timestamp, allowAdditionalInformation, version, previousVersionId);
		em.persist(s);
	
		if(serviceID == null) 
			s.setFullId(RegistryId.getRegistryId(em) +  "." + s.getId());
		
		return s.getFullId();
	}

	public String reallyStoreFacetXml(FacetWS xml, int internalFacetId) {
		Facet f = em.find(Facet.class, internalFacetId);
		
		if(xml.getId() != null) f.setXmlId(xml.getId());
		else f.setXmlId(f.getSchemaId() + "." + System.nanoTime());
		f.setXmlName(xml.getName());
		f.setXmlDocument(xml.getDocument());
		f.setXmlTimestamp(xml.getTimestamp());
		
		return f.getXmlId();
	}

	public String reallyStoreFacetXsd(FacetWS facet, boolean addInfo, int userId, int internalServiceId) {
		User u = em.find(User.class, userId);
		Service s = em.find(Service.class, internalServiceId);
		
		Facet f = new Facet(facet.getId(), facet.getName(), facet.getDocument(), facet.getTypeName(), facet.getTimestamp(), null, null, null, null, s, u, addInfo);
		em.persist(f);

		if(f.getSchemaId() == null) f.setSchemaId(RegistryId.getRegistryId(em) + "." + f.getId());
		return f.getSchemaId();
	}

}
