/**
 * NotificationBean.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package eu.secse.deliveryManager.notify.jaws;

public interface NotificationBean extends java.rmi.Remote {
    public void changePassword(java.lang.String email, java.lang.String oldPassword, java.lang.String newPassword) throws java.rmi.RemoteException, eu.secse.deliveryManager.notify.jaws.LoginFailedException;
    public eu.secse.deliveryManager.notify.jaws.Interest[] getAllInterests(java.lang.String email, java.lang.String password) throws java.rmi.RemoteException, eu.secse.deliveryManager.notify.jaws.LoginFailedException;
    public int registerNotification(java.lang.String email, java.lang.String password, java.lang.String providerName, java.lang.String serviceId, java.lang.String baseServiceId, java.lang.String serviceNameRegex, boolean notifyService, boolean notifyFacetSchema, boolean notifyFacetXml) throws java.rmi.RemoteException, eu.secse.deliveryManager.notify.jaws.LoginFailedException;
    public void registerNotificationUser(java.lang.String email) throws java.rmi.RemoteException;
    public void resendPassword(java.lang.String email) throws java.rmi.RemoteException, eu.secse.deliveryManager.notify.jaws.LoginFailedException;
    public void unregisterNotification(java.lang.String email, java.lang.String password, int notificationID) throws java.rmi.RemoteException, eu.secse.deliveryManager.notify.jaws.LoginFailedException, eu.secse.deliveryManager.notify.jaws.NotFoundException;
    public void unregisterNotificationUser(java.lang.String email, java.lang.String password) throws java.rmi.RemoteException, eu.secse.deliveryManager.notify.jaws.LoginFailedException;
}
