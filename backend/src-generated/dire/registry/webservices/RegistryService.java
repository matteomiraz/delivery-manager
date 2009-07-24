/**
 * RegistryService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package dire.registry.webservices;

public interface RegistryService extends javax.xml.rpc.Service {
    public java.lang.String getRegistryPortAddress();

    public dire.registry.webservices.Registry getRegistryPort() throws javax.xml.rpc.ServiceException;

    public dire.registry.webservices.Registry getRegistryPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
