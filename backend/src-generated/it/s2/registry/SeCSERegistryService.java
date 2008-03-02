/**
 * SeCSERegistryService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.s2.registry;

public interface SeCSERegistryService extends javax.xml.rpc.Service {
    public java.lang.String getSeCSERegistryAddress();

    public it.s2.registry.SeCSERegistry getSeCSERegistry() throws javax.xml.rpc.ServiceException;

    public it.s2.registry.SeCSERegistry getSeCSERegistry(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
