/**
 * SeCSERegistry.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.s2.registry;

public interface SeCSERegistry extends java.rmi.Remote {
    public java.lang.String[] query(java.lang.String query) throws java.rmi.RemoteException, it.s2.registry.QueryErrorException1, it.s2.registry.QueryNotAllowedException1;
    public it.s2.registry.Service getService(java.lang.String serviceId) throws java.rmi.RemoteException;
    public long authenticate(it.s2.registry.Credentials credentials) throws java.rmi.RemoteException, it.s2.registry.CredentialsNotValidException1;
    public java.lang.String getRegistryId() throws java.rmi.RemoteException;
    public java.lang.String deployService(it.s2.registry.Service sd, it.s2.registry.Credentials credentials, boolean forceId) throws java.rmi.RemoteException, it.s2.registry.CredentialsNotValidException1, it.s2.registry.ServiceNotDeployedException1;
    public void undeployService(java.lang.String serviceId, it.s2.registry.Credentials credentials) throws java.rmi.RemoteException, it.s2.registry.ServiceNotUndeployedException1, it.s2.registry.CredentialsNotValidException1;
    public it.s2.registry.FacetSpecificationXSD[] getFacetSpecificationXSDs(java.lang.String serviceId, boolean includeAdditionalInformation) throws java.rmi.RemoteException;
    public long registerServiceProvider(it.s2.registry.ServiceProviderDescription spd, it.s2.registry.Credentials credentials) throws java.rmi.RemoteException, it.s2.registry.ServiceProviderNotRegisteredException1, it.s2.registry.LoginAlreadyExistingException1;
    public void unregisterServiceProvider(it.s2.registry.Credentials credentials) throws java.rmi.RemoteException, it.s2.registry.ServiceProviderNotUnregisteredException1;
    public java.lang.String[] getServiceIdsByProvider(long providerId) throws java.rmi.RemoteException;
    public java.lang.String[] getAdditionalInformation(long providerId) throws java.rmi.RemoteException;
    public void removeFacetSpecification(java.lang.String facetSpecificationXSDId, it.s2.registry.Credentials credentials) throws java.rmi.RemoteException, it.s2.registry.FacetSpecificationNotRemovedException1;
    public it.s2.registry.ServiceProviderDescription getServiceProviderDescriptionById(long id) throws java.rmi.RemoteException, it.s2.registry.ServiceProviderNotFoundException1;
    public long[] getServiceProviderDescriptionByName(java.lang.String nameRegExp) throws java.rmi.RemoteException, it.s2.registry.ServiceProviderNotFoundException1;
    public java.lang.String addFacetSpecificationXSD(java.lang.String serviceId, it.s2.registry.FacetSpecificationXSD fsXSD, it.s2.registry.Credentials credentials, boolean additionalInformation, java.lang.String facetType, boolean forceId) throws java.rmi.RemoteException, it.s2.registry.CredentialsNotValidException1, it.s2.registry.FacetSpecificationXSDNotAddedException1;
    public it.s2.registry.FacetSpecificationXSD getFacetSpecificationXSDById(java.lang.String facetSpecificationXSDId) throws java.rmi.RemoteException;
    public java.lang.String getFacetSpecificationXMLIdByFacetSpecificationXSDId(java.lang.String facetSpecificationXSDId) throws java.rmi.RemoteException;
    public java.lang.String addFacetSpecificationXML(java.lang.String serviceId, java.lang.String facetSpecificationXSDId, it.s2.registry.FacetSpecificationXML fsXML, it.s2.registry.Credentials credentials, boolean enableValidation, boolean forceId) throws java.rmi.RemoteException, it.s2.registry.FacetSpecificationXMLNotAddedException1;
    public java.lang.String addWellKnownFacetSpecificationXML(java.lang.String serviceId, java.lang.String wellKnownFacetSpecificationXSD, it.s2.registry.FacetSpecificationXML fsXML, it.s2.registry.Credentials credentials, boolean enableValidation, boolean forceId, java.lang.String forcedXSDId) throws java.rmi.RemoteException, it.s2.registry.FacetSpecificationXMLNotAddedException1;
    public java.lang.String getFacetType(java.lang.String facetSpecificationXSDId) throws java.rmi.RemoteException;
    public java.lang.String[] listWellKnownFacetTypes() throws java.rmi.RemoteException;
    public java.lang.String[] listWellKnownFacetSpecificationXSDs() throws java.rmi.RemoteException;
    public void removeFacetSpecificationXML(java.lang.String facetSpecificationXMLId, it.s2.registry.Credentials credentials) throws java.rmi.RemoteException, it.s2.registry.CredentialsNotValidException1, it.s2.registry.FacetSpecificationXMLNotRemovedException1;
    public it.s2.registry.FacetSpecificationXML getFacetSpecificationXMLById(java.lang.String facetSpecificationXMLId) throws java.rmi.RemoteException;
    public void removeFacetSpecificationXMLByFacetSpecificationXSDId(java.lang.String facetSpecificationXSDId, it.s2.registry.Credentials credentials) throws java.rmi.RemoteException, it.s2.registry.CredentialsNotValidException1, it.s2.registry.FacetSpecificationXMLNotRemovedException1;
    public java.lang.String[] getAllServiceIds() throws java.rmi.RemoteException;
    public java.lang.String[] getServiceIdsByName(java.lang.String serviceNameRegExp) throws java.rmi.RemoteException;
    public java.lang.String[] getServiceIdsByFacetSpecifications(it.s2.registry.FacetSpecificationXSD[] fsXSD) throws java.rmi.RemoteException;
    public boolean hasFacetSpecificationXSD(java.lang.String serviceId, it.s2.registry.FacetSpecificationXSD fsXSD) throws java.rmi.RemoteException;
    public it.s2.registry.FacetSpecificationXSD[] getFacetSpecificationXSDByName(java.lang.String serviceId, java.lang.String XSDNameRegExp) throws java.rmi.RemoteException;
    public java.lang.String[] getFacetSpecificationXSDIdsByServiceId(java.lang.String serviceId) throws java.rmi.RemoteException;
    public it.s2.registry.FacetSpecificationXML[] getFacetSpecificationXMLsByServiceId(java.lang.String serviceId, boolean includeAdditionalInformation) throws java.rmi.RemoteException;
    public java.lang.String[] getFacetSpecificationXMLIdsByServiceId(java.lang.String serviceId, boolean includeAdditionalInformation) throws java.rmi.RemoteException;
    public it.s2.registry.FacetSpecificationXML getFacetSpecificationXMLByFacetSpecificationXSDId(java.lang.String facetSpecificationXSDId) throws java.rmi.RemoteException;
    public it.s2.registry.FacetSpecificationXSD[] getAllFacetSpecificationXSDs(java.lang.String[] serviceIds, boolean includeAdditionalInformation) throws java.rmi.RemoteException;
    public it.s2.registry.FacetSpecificationXSD[] getCommonFacetSpecificationXSDs(java.lang.String[] serviceIds, boolean includeAdditionalInformation) throws java.rmi.RemoteException;
    public it.s2.registry.FacetSpecificationXML[] getFacetSpecificationXMLByName(java.lang.String serviceId, java.lang.String XMLNameRegExp) throws java.rmi.RemoteException;
    public java.lang.String[] getServiceIdsByFacetTypeName(java.lang.String facetTypeNameRegExp) throws java.rmi.RemoteException;
    public boolean hasFacetType(java.lang.String serviceId, java.lang.String facetTypeName) throws java.rmi.RemoteException;
    public java.lang.String[] getFacetTypeNamesByServiceId(java.lang.String serviceId) throws java.rmi.RemoteException;
    public java.lang.String[] getAllFacetTypes(java.lang.String[] serviceIds) throws java.rmi.RemoteException;
    public java.lang.String[] getCommonFacetTypes(java.lang.String[] serviceIds) throws java.rmi.RemoteException;
}
