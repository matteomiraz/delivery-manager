package it.s2.logger;

public class SeCSELoggerProxy implements it.s2.logger.SeCSELogger {
  private String _endpoint = null;
  private it.s2.logger.SeCSELogger seCSELogger = null;
  
  public SeCSELoggerProxy() {
    _initSeCSELoggerProxy();
  }
  
  public SeCSELoggerProxy(String endpoint) {
    _endpoint = endpoint;
    _initSeCSELoggerProxy();
  }
  
  private void _initSeCSELoggerProxy() {
    try {
      seCSELogger = (new it.s2.logger.SeCSELoggerServiceLocator()).getSeCSELogger();
      if (seCSELogger != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)seCSELogger)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)seCSELogger)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (seCSELogger != null)
      ((javax.xml.rpc.Stub)seCSELogger)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public it.s2.logger.SeCSELogger getSeCSELogger() {
    if (seCSELogger == null)
      _initSeCSELoggerProxy();
    return seCSELogger;
  }
  
  public java.lang.String getVersion() throws java.rmi.RemoteException{
    if (seCSELogger == null)
      _initSeCSELoggerProxy();
    return seCSELogger.getVersion();
  }
  
  public long event(java.lang.String registryId, java.lang.String applicationId, java.lang.String applicationName, java.lang.String userId, java.lang.String registryUserId, java.lang.String event) throws java.rmi.RemoteException, it.s2.logger.EventNotAddedException{
    if (seCSELogger == null)
      _initSeCSELoggerProxy();
    return seCSELogger.event(registryId, applicationId, applicationName, userId, registryUserId, event);
  }
  
  public java.lang.String generateId() throws java.rmi.RemoteException{
    if (seCSELogger == null)
      _initSeCSELoggerProxy();
    return seCSELogger.generateId();
  }
  
  public long startEvent(java.lang.String registryId, java.lang.String applicationId, java.lang.String applicationName, java.lang.String userId, java.lang.String registryUserId, java.lang.String event) throws java.rmi.RemoteException, it.s2.logger.EventNotAddedException{
    if (seCSELogger == null)
      _initSeCSELoggerProxy();
    return seCSELogger.startEvent(registryId, applicationId, applicationName, userId, registryUserId, event);
  }
  
  public void endEvent(java.lang.String applicationId, long eventId) throws java.rmi.RemoteException, it.s2.logger.EndEventNotAddedException{
    if (seCSELogger == null)
      _initSeCSELoggerProxy();
    seCSELogger.endEvent(applicationId, eventId);
  }
  
  public void deleteEvent(java.lang.String applicationId, long eventId) throws java.rmi.RemoteException, it.s2.logger.EventNotDeletedException{
    if (seCSELogger == null)
      _initSeCSELoggerProxy();
    seCSELogger.deleteEvent(applicationId, eventId);
  }
  
  
}