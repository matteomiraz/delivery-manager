package dire.registry.data;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Query;

import dire.registry.Signature;

@Entity
@NamedQuery(name=Service.GET_BY_ID, query="FROM Service WHERE fullId = :id")
public class Service {
	
	public static final String GET_BY_ID = "SERVICE_BY_ID";
	
	/** Internal registry's id */
	@Id
	@GeneratedValue
	private int id;
	
	/** full service id: */
	@Column(unique=true)
	private String fullId;

	@ManyToOne
	private User provider;

	private String name;

	private String timestamp;
	private boolean allowAdditionalInformation;

	private String version;
	private String previousVersionId;
	
	@OneToMany(mappedBy="service")
	private Collection<Facet> facets;

	@Lob
	private Signature signature;
	
	public Service() { }

	public Service(String fullId, User provider, String name, String timestamp, boolean allowAdditionalInformation, String version, String previousVersionId) {
		this.fullId = fullId;
		this.provider = provider;
		this.name = name;
		this.timestamp = timestamp;
		this.allowAdditionalInformation = allowAdditionalInformation;
		this.version = version;
		this.previousVersionId = previousVersionId;
	}

	public String getFullId() {
		return fullId;
	}

	public void setFullId(String fullId) {
		this.fullId = fullId;
	}

	public int getId() {
		return id;
	}

	public User getProvider() {
		return provider;
	}

	public String getName() {
		return name;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public boolean isAllowAdditionalInformation() {
		return allowAdditionalInformation;
	}

	public String getVersion() {
		return version;
	}

	public String getPreviousVersionId() {
		return previousVersionId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Service other = (Service) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public Collection<Facet> getFacets() {
		return facets;
	}
	
	public static final Service getByFullId(EntityManager em, String id) {
		Query q = em.createNamedQuery(Service.GET_BY_ID);
		q.setParameter("id", id);
		Service service = (Service) q.getSingleResult();
		return service;
	}

	public Signature getSignature() {
		return signature;
	}
	
	public void setSignature(Signature signature) {
		this.signature = signature;
	}
}
