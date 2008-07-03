/**
 * IDirectoryLookupWS.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.secse.federationDirectory.webservice;

public interface IDirectoryLookupWS extends java.rmi.Remote {
    public eu.secse.federationDirectory.webservice.WSFederationDataArray getAllFederations() throws java.rmi.RemoteException;
    public eu.secse.federationDirectory.webservice.WSFederationDataArray searchFederationByName(java.lang.String string_1) throws java.rmi.RemoteException;
    public eu.secse.federationDirectory.webservice.WSFederationData searchFederationByUid(java.lang.String string_1) throws java.rmi.RemoteException;
}
