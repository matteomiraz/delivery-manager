/**
 * Registry.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package dire.registry.webservices;


public interface Registry extends java.rmi.Remote {
    public long authenticate(java.lang.String username, java.lang.String passwd) throws java.rmi.RemoteException;
    public java.lang.String deployService(java.lang.String username, java.lang.String passwd, java.lang.String serviceId, java.lang.String name, boolean allowAddInfo, java.lang.String version, java.lang.String previousVersionId, java.lang.String timestamp) throws java.rmi.RemoteException;
    public java.lang.String[] getAllServiceIds() throws java.rmi.RemoteException;
    public dire.registry.webservices.Signature getFacetAddInfoSignature(java.lang.String serviceId, java.lang.String schemaId) throws java.rmi.RemoteException;
    public dire.registry.webservices.FacetWS[] getFacetAddInfoXSDs(java.lang.String serviceId) throws java.rmi.RemoteException;
    public dire.registry.webservices.FacetWS[] getFacetSpecXSDs(java.lang.String serviceId) throws java.rmi.RemoteException;
    public dire.registry.webservices.FacetWS getFacetXmlBySchemaId(java.lang.String facetSchemaId) throws java.rmi.RemoteException;
    public dire.registry.webservices.FacetWS getFacetXsdById(java.lang.String id) throws java.rmi.RemoteException;
    public java.lang.String getRegistryId() throws java.rmi.RemoteException;
    public dire.registry.webservices.ServiceWS getService(java.lang.String serviceId) throws java.rmi.RemoteException;
    public java.lang.String getServiceIdByFacetId(java.lang.String facetId) throws java.rmi.RemoteException;
    public dire.registry.webservices.Signature getServiceSignature(java.lang.String serviceId) throws java.rmi.RemoteException;
    public boolean isAdditionalInformation(java.lang.String facetId) throws java.rmi.RemoteException;
    public long registerServiceProvider(java.lang.String username, java.lang.String passwd, java.lang.String nickname) throws java.rmi.RemoteException;
    public void removeFacetXSD(java.lang.String username, java.lang.String passwd, java.lang.String facetId) throws java.rmi.RemoteException;
    public void removeFacetXml(java.lang.String username, java.lang.String passwd, java.lang.String xmlId) throws java.rmi.RemoteException;
    public void setFacetAddInfoSignature(java.lang.String username, java.lang.String password, java.lang.String serviceId, java.lang.String schemaId, dire.registry.webservices.Signature signature) throws java.rmi.RemoteException, dire.registry.webservices.IllegalStateException;
    public void setServiceSignature(java.lang.String username, java.lang.String passwd, java.lang.String serviceId, dire.registry.webservices.Signature signature) throws java.rmi.RemoteException;
    public java.lang.String storeFacetXML(java.lang.String username, java.lang.String passwd, java.lang.String serviceId, java.lang.String facetSchemaId, dire.registry.webservices.FacetWS facetXml) throws java.rmi.RemoteException;
    public java.lang.String storeFacetXSD(java.lang.String username, java.lang.String passwd, java.lang.String serviceId, dire.registry.webservices.FacetWS facet, boolean addInfo) throws java.rmi.RemoteException;
    public void undeployService(java.lang.String username, java.lang.String passwd, java.lang.String serviceId) throws java.rmi.RemoteException;
}
