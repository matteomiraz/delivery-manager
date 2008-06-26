package dire.registry;

import javax.ejb.Local;

import dire.registry.ws.FacetWS;

@Local
public interface IRegistryWriter {

	public String reallyDeployService(String serviceID, String name, boolean allowAdditionalInformation, String version, String previousVersionId, String timestamp, int userId);

	public String reallyStoreFacetXml(FacetWS xml, int internalFacetId);

	public String reallyStoreFacetXsd(FacetWS facet, boolean addInfo, int userId, int internalServiceId);

}