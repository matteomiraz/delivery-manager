/**
 * FederationDirectory.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.secse.federationDirectory.webservice;

public interface FederationDirectory extends javax.xml.rpc.Service {
    public java.lang.String getIDirectoryLookupWSPortAddress();

    public eu.secse.federationDirectory.webservice.IDirectoryLookupWS getIDirectoryLookupWSPort() throws javax.xml.rpc.ServiceException;

    public eu.secse.federationDirectory.webservice.IDirectoryLookupWS getIDirectoryLookupWSPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
