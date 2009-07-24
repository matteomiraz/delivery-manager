package dire.registry;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.Remote;

import dire.registry.exception.AuthenticationException;
import dire.registry.webservices.Signature;
import dire.registry.ws.FacetWS;
import dire.registry.ws.ServiceWS;

@Remote
public interface IRegistry {

	public String getRegistryId() throws RemoteException;

	public long authenticate(String username, String password) throws AuthenticationException, RemoteException;
	public long registerServiceProvider(String username, String password, String nickname)  throws RemoteException;

	public String deployService(String username, String password, String serviceID, String name, boolean allowAdditionalInformation, String version, String previousVersionId, String timestamp) throws AuthenticationException, RemoteException;
	public ServiceWS getService(String serviceId) throws RemoteException;
	public Collection<String> getAllServiceIds() throws RemoteException;
	public void undeployService(String username, String password, String serviceId) throws AuthenticationException, RemoteException;
	
	public String storeFacetXSD(String username, String password, String serviceId, FacetWS facet, boolean addInfo) throws AuthenticationException, RemoteException;
	public FacetWS getFacetXsdById(String id);
	public void removeFacetXSD(String username, String password, String xsdID) throws AuthenticationException, RemoteException;
	
	public String storeFacetXML(String username, String password, String serviceId, String xsdId, FacetWS xml) throws RemoteException;
	public FacetWS getFacetXmlBySchemaId(String facetSchemaId);
	public void removeFacetXml(String username, String password, String xmlId) throws AuthenticationException, RemoteException;

	public boolean isAdditionalInformation(String id) throws RemoteException;
	public String getServiceIdByFacetId(String facetId) throws RemoteException;
	public Collection<FacetWS> getFacetSpecXSDs(String serviceID) throws RemoteException;
	public Collection<FacetWS> getFacetAddInfoXSDs(String serviceID) throws RemoteException;
	
	public void setServiceSignature(String username, String password, String serviceID, Signature signature) throws RemoteException, AuthenticationException;
	public Signature getServiceSignature(String serviceID) throws RemoteException;

	public void setFacetAddInfoSignature(String username, String password, String serviceID, String xsdId, Signature signature) throws AuthenticationException, RemoteException;
	public Signature getFacetAddInfoSignature(String serviceID, String xsdId) throws RemoteException;
}