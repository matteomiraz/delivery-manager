/**
 * SeCSERegistryServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.s2.registry;

public class SeCSERegistryServiceLocator extends org.apache.axis.client.Service implements it.s2.registry.SeCSERegistryService {

    public SeCSERegistryServiceLocator() {
    }


    public SeCSERegistryServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SeCSERegistryServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SeCSERegistry
    private java.lang.String SeCSERegistry_address = "http://momocs:8080/SeCSERegistry/services/SeCSERegistry";

    public java.lang.String getSeCSERegistryAddress() {
        return SeCSERegistry_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SeCSERegistryWSDDServiceName = "SeCSERegistry";

    public java.lang.String getSeCSERegistryWSDDServiceName() {
        return SeCSERegistryWSDDServiceName;
    }

    public void setSeCSERegistryWSDDServiceName(java.lang.String name) {
        SeCSERegistryWSDDServiceName = name;
    }

    public it.s2.registry.SeCSERegistry getSeCSERegistry() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SeCSERegistry_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSeCSERegistry(endpoint);
    }

    public it.s2.registry.SeCSERegistry getSeCSERegistry(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            it.s2.registry.SeCSERegistrySoapBindingStub _stub = new it.s2.registry.SeCSERegistrySoapBindingStub(portAddress, this);
            _stub.setPortName(getSeCSERegistryWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSeCSERegistryEndpointAddress(java.lang.String address) {
        SeCSERegistry_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (it.s2.registry.SeCSERegistry.class.isAssignableFrom(serviceEndpointInterface)) {
                it.s2.registry.SeCSERegistrySoapBindingStub _stub = new it.s2.registry.SeCSERegistrySoapBindingStub(new java.net.URL(SeCSERegistry_address), this);
                _stub.setPortName(getSeCSERegistryWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("SeCSERegistry".equals(inputPortName)) {
            return getSeCSERegistry();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://registry.s2.it", "SeCSERegistryService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://registry.s2.it", "SeCSERegistry"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("SeCSERegistry".equals(portName)) {
            setSeCSERegistryEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
