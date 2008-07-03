package dire.registry.ws;

import java.io.Serializable;

import dire.registry.data.Service;

public class ServiceWS implements Serializable {
	
	private static final long serialVersionUID = -2775155895177432957L;

	private String fullId;

	private int providerId;

	private String name;

	private String timestamp;
	private boolean allowAdditionalInformation;

	private String version;
	private String previousVersionId;

	public ServiceWS() { }

	public ServiceWS(String fullId, int providerId, String name, String timestamp, boolean allowAdditionalInformation, String version, String previousVersionId) {
		this.fullId = fullId;
		this.providerId = providerId;
		this.name = name;
		this.timestamp = timestamp;
		this.allowAdditionalInformation = allowAdditionalInformation;
		this.version = version;
		this.previousVersionId = previousVersionId;
	}
	
	public ServiceWS(Service s) {
		this(s.getFullId(), s.getProvider().getId(), s.getName(), s.getTimestamp(), s.isAllowAdditionalInformation(), s.getVersion(), s.getPreviousVersionId());
	}

	public String getFullId() {
		return fullId;
	}

	public void setFullId(String fullId) {
		this.fullId = fullId;
	}

	public int getProviderId() {
		return providerId;
	}

	public void setProviderId(int providerId) {
		this.providerId = providerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isAllowAdditionalInformation() {
		return allowAdditionalInformation;
	}

	public void setAllowAdditionalInformation(boolean allowAdditionalInformation) {
		this.allowAdditionalInformation = allowAdditionalInformation;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPreviousVersionId() {
		return previousVersionId;
	}

	public void setPreviousVersionId(String previousVersionId) {
		this.previousVersionId = previousVersionId;
	}
}
