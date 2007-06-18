package eu.secse.deliveryManager.notify.jaws;

public class NotificationBeanProxy implements eu.secse.deliveryManager.notify.jaws.NotificationBean {
  private String _endpoint = null;
  private eu.secse.deliveryManager.notify.jaws.NotificationBean notificationBean = null;
  
  public NotificationBeanProxy() {
    _initNotificationBeanProxy();
  }
  
  private void _initNotificationBeanProxy() {
    try {
      notificationBean = (new eu.secse.deliveryManager.notify.jaws.NotificationBeanServiceLocator()).getNotificationBeanPort();
      if (notificationBean != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)notificationBean)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)notificationBean)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (notificationBean != null)
      ((javax.xml.rpc.Stub)notificationBean)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public eu.secse.deliveryManager.notify.jaws.NotificationBean getNotificationBean() {
    if (notificationBean == null)
      _initNotificationBeanProxy();
    return notificationBean;
  }
  
  public void changePassword(java.lang.String email, java.lang.String oldPassword, java.lang.String newPassword) throws java.rmi.RemoteException, eu.secse.deliveryManager.notify.jaws.LoginFailedException{
    if (notificationBean == null)
      _initNotificationBeanProxy();
    notificationBean.changePassword(email, oldPassword, newPassword);
  }
  
  public eu.secse.deliveryManager.notify.jaws.Interest[] getAllInterests(java.lang.String email, java.lang.String password) throws java.rmi.RemoteException, eu.secse.deliveryManager.notify.jaws.LoginFailedException{
    if (notificationBean == null)
      _initNotificationBeanProxy();
    return notificationBean.getAllInterests(email, password);
  }
  
  public int registerNotification(java.lang.String email, java.lang.String password, java.lang.String providerName, java.lang.String serviceId, java.lang.String baseServiceId, java.lang.String serviceNameRegex, boolean notifyService, boolean notifyFacetSchema, boolean notifyFacetXml) throws java.rmi.RemoteException, eu.secse.deliveryManager.notify.jaws.LoginFailedException{
    if (notificationBean == null)
      _initNotificationBeanProxy();
    return notificationBean.registerNotification(email, password, providerName, serviceId, baseServiceId, serviceNameRegex, notifyService, notifyFacetSchema, notifyFacetXml);
  }
  
  public void registerNotificationUser(java.lang.String email) throws java.rmi.RemoteException{
    if (notificationBean == null)
      _initNotificationBeanProxy();
    notificationBean.registerNotificationUser(email);
  }
  
  public void resendPassword(java.lang.String email) throws java.rmi.RemoteException, eu.secse.deliveryManager.notify.jaws.LoginFailedException{
    if (notificationBean == null)
      _initNotificationBeanProxy();
    notificationBean.resendPassword(email);
  }
  
  public void unregisterNotification(java.lang.String email, java.lang.String password, int notificationID) throws java.rmi.RemoteException, eu.secse.deliveryManager.notify.jaws.LoginFailedException, eu.secse.deliveryManager.notify.jaws.NotFoundException{
    if (notificationBean == null)
      _initNotificationBeanProxy();
    notificationBean.unregisterNotification(email, password, notificationID);
  }
  
  public void unregisterNotificationUser(java.lang.String email, java.lang.String password) throws java.rmi.RemoteException, eu.secse.deliveryManager.notify.jaws.LoginFailedException{
    if (notificationBean == null)
      _initNotificationBeanProxy();
    notificationBean.unregisterNotificationUser(email, password);
  }
  
  
}