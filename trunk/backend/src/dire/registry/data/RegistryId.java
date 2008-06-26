package dire.registry.data;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Query;

@Entity
@NamedQuery(name=RegistryId.GET_REG_ID, query="FROM RegistryId")
public class RegistryId {
	public static final String GET_REG_ID = "REGISTRY_ID_GET_FROM_REGISTRYID";
	
	private static String regId = null;
	
	@Id
	private String id;
	
	public RegistryId() { }

	public RegistryId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	@SuppressWarnings("unchecked")
	public static synchronized final String getRegistryId(EntityManager em) {
		if(regId != null) return regId;
		
		try {
			Query q = em.createNamedQuery(GET_REG_ID);
			q.setMaxResults(1);
			List<RegistryId> r = (List<RegistryId>) q.getResultList();
			
			if(r.size() == 1) {
				regId = r.get(0).getId(); 
				return regId;
			}
		} catch (Throwable e) {
		}
		
		RegistryId regId = new RegistryId(Long.toHexString(System.nanoTime()));
		em.persist(regId);
		
		RegistryId.regId = regId.getId();
		return regId.getId();
	}
}
