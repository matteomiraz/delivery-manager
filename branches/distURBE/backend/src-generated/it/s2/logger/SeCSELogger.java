/**
 * SeCSELogger.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.s2.logger;

public interface SeCSELogger extends java.rmi.Remote {
    public java.lang.String getVersion() throws java.rmi.RemoteException;
    public long event(java.lang.String registryId, java.lang.String applicationId, java.lang.String applicationName, java.lang.String userId, java.lang.String registryUserId, java.lang.String event) throws java.rmi.RemoteException, it.s2.logger.EventNotAddedException;
    public java.lang.String generateId() throws java.rmi.RemoteException;
    public long startEvent(java.lang.String registryId, java.lang.String applicationId, java.lang.String applicationName, java.lang.String userId, java.lang.String registryUserId, java.lang.String event) throws java.rmi.RemoteException, it.s2.logger.EventNotAddedException;
    public void endEvent(java.lang.String applicationId, long eventId) throws java.rmi.RemoteException, it.s2.logger.EndEventNotAddedException;
    public void deleteEvent(java.lang.String applicationId, long eventId) throws java.rmi.RemoteException, it.s2.logger.EventNotDeletedException;
}
