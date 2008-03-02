package it.s2.registry;

public class SeCSERegistryProxy implements it.s2.registry.SeCSERegistry {
  private String _endpoint = null;
  private it.s2.registry.SeCSERegistry seCSERegistry = null;
  
  public SeCSERegistryProxy() {
    _initSeCSERegistryProxy();
  }
  
  private void _initSeCSERegistryProxy() {
    try {
      seCSERegistry = (new it.s2.registry.SeCSERegistryServiceLocator()).getSeCSERegistry();
      if (seCSERegistry != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)seCSERegistry)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)seCSERegistry)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (seCSERegistry != null)
      ((javax.xml.rpc.Stub)seCSERegistry)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public it.s2.registry.SeCSERegistry getSeCSERegistry() {
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry;
  }
  
  public java.lang.String[] query(java.lang.String query) throws java.rmi.RemoteException, it.s2.registry.QueryErrorException1, it.s2.registry.QueryNotAllowedException1{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.query(query);
  }
  
  public it.s2.registry.Service getService(java.lang.String serviceId) throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.getService(serviceId);
  }
  
  public long authenticate(it.s2.registry.Credentials credentials) throws java.rmi.RemoteException, it.s2.registry.CredentialsNotValidException1{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.authenticate(credentials);
  }
  
  public java.lang.String getRegistryId() throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.getRegistryId();
  }
  
  public java.lang.String deployService(it.s2.registry.Service sd, it.s2.registry.Credentials credentials, boolean forceId) throws java.rmi.RemoteException, it.s2.registry.CredentialsNotValidException1, it.s2.registry.ServiceNotDeployedException1{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.deployService(sd, credentials, forceId);
  }
  
  public void undeployService(java.lang.String serviceId, it.s2.registry.Credentials credentials) throws java.rmi.RemoteException, it.s2.registry.CredentialsNotValidException1, it.s2.registry.ServiceNotUndeployedException1{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    seCSERegistry.undeployService(serviceId, credentials);
  }
  
  public it.s2.registry.FacetSpecificationXSD[] getFacetSpecificationXSDs(java.lang.String serviceId, boolean includeAdditionalInformation) throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.getFacetSpecificationXSDs(serviceId, includeAdditionalInformation);
  }
  
  public long registerServiceProvider(it.s2.registry.ServiceProviderDescription spd, it.s2.registry.Credentials credentials) throws java.rmi.RemoteException, it.s2.registry.LoginAlreadyExistingException1, it.s2.registry.ServiceProviderNotRegisteredException1{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.registerServiceProvider(spd, credentials);
  }
  
  public void unregisterServiceProvider(it.s2.registry.Credentials credentials) throws java.rmi.RemoteException, it.s2.registry.ServiceProviderNotUnregisteredException1{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    seCSERegistry.unregisterServiceProvider(credentials);
  }
  
  public java.lang.String[] getServiceIdsByProvider(long providerId) throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.getServiceIdsByProvider(providerId);
  }
  
  public java.lang.String[] getAdditionalInformation(long providerId) throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.getAdditionalInformation(providerId);
  }
  
  public void removeFacetSpecification(java.lang.String facetSpecificationXSDId, it.s2.registry.Credentials credentials) throws java.rmi.RemoteException, it.s2.registry.FacetSpecificationNotRemovedException1{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    seCSERegistry.removeFacetSpecification(facetSpecificationXSDId, credentials);
  }
  
  public it.s2.registry.ServiceProviderDescription getServiceProviderDescriptionById(long id) throws java.rmi.RemoteException, it.s2.registry.ServiceProviderNotFoundException1{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.getServiceProviderDescriptionById(id);
  }
  
  public long[] getServiceProviderDescriptionByName(java.lang.String nameRegExp) throws java.rmi.RemoteException, it.s2.registry.ServiceProviderNotFoundException1{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.getServiceProviderDescriptionByName(nameRegExp);
  }
  
  public java.lang.String addFacetSpecificationXSD(java.lang.String serviceId, it.s2.registry.FacetSpecificationXSD fsXSD, it.s2.registry.Credentials credentials, boolean additionalInformation, java.lang.String facetType, boolean forceId) throws java.rmi.RemoteException, it.s2.registry.CredentialsNotValidException1, it.s2.registry.FacetSpecificationXSDNotAddedException1{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.addFacetSpecificationXSD(serviceId, fsXSD, credentials, additionalInformation, facetType, forceId);
  }
  
  public it.s2.registry.FacetSpecificationXSD getFacetSpecificationXSDById(java.lang.String facetSpecificationXSDId) throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.getFacetSpecificationXSDById(facetSpecificationXSDId);
  }
  
  public java.lang.String getFacetSpecificationXMLIdByFacetSpecificationXSDId(java.lang.String facetSpecificationXSDId) throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.getFacetSpecificationXMLIdByFacetSpecificationXSDId(facetSpecificationXSDId);
  }
  
  public java.lang.String addFacetSpecificationXML(java.lang.String serviceId, java.lang.String facetSpecificationXSDId, it.s2.registry.FacetSpecificationXML fsXML, it.s2.registry.Credentials credentials, boolean enableValidation, boolean forceId) throws java.rmi.RemoteException, it.s2.registry.FacetSpecificationXMLNotAddedException1{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.addFacetSpecificationXML(serviceId, facetSpecificationXSDId, fsXML, credentials, enableValidation, forceId);
  }
  
  public it.s2.registry.FacetSpecificationXML getFacetSpecificationXMLById(java.lang.String facetSpecificationXMLId) throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.getFacetSpecificationXMLById(facetSpecificationXMLId);
  }
  
  public java.lang.String addWellKnownFacetSpecificationXML(java.lang.String serviceId, java.lang.String wellKnownFacetSpecificationXSD, it.s2.registry.FacetSpecificationXML fsXML, it.s2.registry.Credentials credentials, boolean enableValidation, boolean forceId, java.lang.String forcedXSDId, boolean additionalInformation) throws java.rmi.RemoteException, it.s2.registry.FacetSpecificationXMLNotAddedException1{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.addWellKnownFacetSpecificationXML(serviceId, wellKnownFacetSpecificationXSD, fsXML, credentials, enableValidation, forceId, forcedXSDId, additionalInformation);
  }
  
  public java.lang.String getFacetType(java.lang.String facetSpecificationXSDId) throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.getFacetType(facetSpecificationXSDId);
  }
  
  public java.lang.String[] listWellKnownFacetTypes() throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.listWellKnownFacetTypes();
  }
  
  public java.lang.String[] listWellKnownFacetSpecificationXSDs() throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.listWellKnownFacetSpecificationXSDs();
  }
  
  public void removeFacetSpecificationXML(java.lang.String facetSpecificationXMLId, it.s2.registry.Credentials credentials) throws java.rmi.RemoteException, it.s2.registry.FacetSpecificationXMLNotRemovedException1, it.s2.registry.CredentialsNotValidException1{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    seCSERegistry.removeFacetSpecificationXML(facetSpecificationXMLId, credentials);
  }
  
  public void removeFacetSpecificationXMLByFacetSpecificationXSDId(java.lang.String facetSpecificationXSDId, it.s2.registry.Credentials credentials) throws java.rmi.RemoteException, it.s2.registry.FacetSpecificationXMLNotRemovedException1, it.s2.registry.CredentialsNotValidException1{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    seCSERegistry.removeFacetSpecificationXMLByFacetSpecificationXSDId(facetSpecificationXSDId, credentials);
  }
  
  public java.lang.String[] getAllServiceIds() throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.getAllServiceIds();
  }
  
  public java.lang.String[] getServiceIdsByName(java.lang.String serviceNameRegExp) throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.getServiceIdsByName(serviceNameRegExp);
  }
  
  public java.lang.String[] getServiceIdsByFacetSpecifications(it.s2.registry.FacetSpecificationXSD[] fsXSD) throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.getServiceIdsByFacetSpecifications(fsXSD);
  }
  
  public boolean hasFacetSpecificationXSD(java.lang.String serviceId, it.s2.registry.FacetSpecificationXSD fsXSD) throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.hasFacetSpecificationXSD(serviceId, fsXSD);
  }
  
  public it.s2.registry.FacetSpecificationXSD[] getFacetSpecificationXSDByName(java.lang.String serviceId, java.lang.String XSDNameRegExp) throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.getFacetSpecificationXSDByName(serviceId, XSDNameRegExp);
  }
  
  public java.lang.String[] getFacetSpecificationXSDIdsByServiceId(java.lang.String serviceId) throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.getFacetSpecificationXSDIdsByServiceId(serviceId);
  }
  
  public it.s2.registry.FacetSpecificationXML[] getFacetSpecificationXMLsByServiceId(java.lang.String serviceId, boolean includeAdditionalInformation) throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.getFacetSpecificationXMLsByServiceId(serviceId, includeAdditionalInformation);
  }
  
  public java.lang.String[] getFacetSpecificationXMLIdsByServiceId(java.lang.String serviceId, boolean includeAdditionalInformation) throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.getFacetSpecificationXMLIdsByServiceId(serviceId, includeAdditionalInformation);
  }
  
  public it.s2.registry.FacetSpecificationXML getFacetSpecificationXMLByFacetSpecificationXSDId(java.lang.String facetSpecificationXSDId) throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.getFacetSpecificationXMLByFacetSpecificationXSDId(facetSpecificationXSDId);
  }
  
  public it.s2.registry.FacetSpecificationXSD[] getAllFacetSpecificationXSDs(java.lang.String[] serviceIds, boolean includeAdditionalInformation) throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.getAllFacetSpecificationXSDs(serviceIds, includeAdditionalInformation);
  }
  
  public it.s2.registry.FacetSpecificationXSD[] getCommonFacetSpecificationXSDs(java.lang.String[] serviceIds, boolean includeAdditionalInformation) throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.getCommonFacetSpecificationXSDs(serviceIds, includeAdditionalInformation);
  }
  
  public it.s2.registry.FacetSpecificationXML[] getFacetSpecificationXMLByName(java.lang.String serviceId, java.lang.String XMLNameRegExp) throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.getFacetSpecificationXMLByName(serviceId, XMLNameRegExp);
  }
  
  public java.lang.String[] getServiceIdsByFacetTypeName(java.lang.String facetTypeNameRegExp) throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.getServiceIdsByFacetTypeName(facetTypeNameRegExp);
  }
  
  public boolean hasFacetType(java.lang.String serviceId, java.lang.String facetTypeName) throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.hasFacetType(serviceId, facetTypeName);
  }
  
  public java.lang.String[] getFacetTypeNamesByServiceId(java.lang.String serviceId) throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.getFacetTypeNamesByServiceId(serviceId);
  }
  
  public java.lang.String[] getAllFacetTypes(java.lang.String[] serviceIds) throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.getAllFacetTypes(serviceIds);
  }
  
  public java.lang.String[] getCommonFacetTypes(java.lang.String[] serviceIds) throws java.rmi.RemoteException{
    if (seCSERegistry == null)
      _initSeCSERegistryProxy();
    return seCSERegistry.getCommonFacetTypes(serviceIds);
  }
  
  
}