package dire.registry.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import dire.registry.webservices.Signature;

@Entity
@NamedQueries(value={
		@NamedQuery(name=Facet.BY_SCHEMA_ID, query="FROM Facet WHERE schemaId = :schemaId"),
		@NamedQuery(name=Facet.BY_XML_ID, query="FROM Facet WHERE xmlId = :xmlId")
})
public class Facet {

	public static final String BY_SCHEMA_ID = "FACET_BY_SCHEMA_ID";
	public static final String BY_XML_ID = "FACET_BY_XML_ID";
	
	@Id
	@GeneratedValue
	private int id; 
	
	@ManyToOne
	private User provider;

	@ManyToOne
	private Service service;

	private boolean addInfo;
	
	@Column(unique=true)
	private String schemaId;
	private String schemaName;
	@Lob private String schemaDocument;
	private String schemaTypeName;
	private String schemaTimestamp;
	
	@Column(unique=true, nullable=true)
	private String xmlId;
	private String xmlName;
	@Lob private String xmlDocument;
	private String xmlTimestamp;
	
	@Lob
	private Signature signature;
	
	public Facet() { }

	public Facet(String schemaId, String schemaName, String schemaDocument, String schemaTypeName, String schemaTimestamp, String xmlId, String xmlName, String xmlDocument, String xmlTimestamp, Service service, User provider, boolean addInfo) {
		super();
		this.schemaId = schemaId;
		this.schemaName = schemaName;
		this.schemaDocument = schemaDocument;
		this.schemaTypeName = schemaTypeName;
		this.schemaTimestamp = schemaTimestamp;
		this.xmlId = xmlId;
		this.xmlName = xmlName;
		this.xmlDocument = xmlDocument;
		this.xmlTimestamp = xmlTimestamp;
		this.provider = provider;
		this.service = service;
		this.addInfo = addInfo;
	}

	public String getXmlId() {
		return xmlId;
	}

	public void setXmlId(String xmlId) {
		this.xmlId = xmlId;
	}

	public String getXmlName() {
		return xmlName;
	}

	public void setXmlName(String xmlName) {
		this.xmlName = xmlName;
	}

	public String getXmlDocument() {
		return xmlDocument;
	}

	public void setXmlDocument(String xmlDocument) {
		this.xmlDocument = xmlDocument;
	}

	public String getXmlTimestamp() {
		return xmlTimestamp;
	}

	public void setXmlTimestamp(String xmlTimestamp) {
		this.xmlTimestamp = xmlTimestamp;
	}

	public int getId() {
		return id;
	}

	public String getSchemaId() {
		return schemaId;
	}

	public void setSchemaId(String schemaId) {
		this.schemaId = schemaId;
	}
	
	public String getSchemaName() {
		return schemaName;
	}

	public String getSchemaDocument() {
		return schemaDocument;
	}

	public String getSchemaTypeName() {
		return schemaTypeName;
	}

	public String getSchemaTimestamp() {
		return schemaTimestamp;
	}
	
	public User getProvider() {
		return provider;
	}

	public boolean isAddInfo() {
		return addInfo;
	}

	public Service getService() {
		return service;
	}

	public void setSignature(Signature signature) {
		this.signature = signature;
	}
	
	public Signature getSignature() {
		return signature;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		final Facet other = (Facet) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	public static Facet getBySchemaId(EntityManager em, String schemaId) {
		try {
			return (Facet) em.createNamedQuery(Facet.BY_SCHEMA_ID).setParameter("schemaId", schemaId).getSingleResult();
		} catch (Throwable t) {
			return null;
		}
	}

	public static Facet getByXmlId(EntityManager em, String xmlId) {
		try {
			return (Facet) em.createNamedQuery(Facet.BY_XML_ID).setParameter("xmlId", xmlId).getSingleResult();
		} catch (Throwable t) {
			return null;
		}
	}
}
