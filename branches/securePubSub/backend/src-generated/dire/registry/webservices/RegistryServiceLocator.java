/**
 * RegistryServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package dire.registry.webservices;

public class RegistryServiceLocator extends org.apache.axis.client.Service implements dire.registry.webservices.RegistryService {

    public RegistryServiceLocator() {
    }


    public RegistryServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public RegistryServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for RegistryPort
    private java.lang.String RegistryPort_address = "http://BlackMamba:8080/registry/Registry";

    public java.lang.String getRegistryPortAddress() {
        return RegistryPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String RegistryPortWSDDServiceName = "RegistryPort";

    public java.lang.String getRegistryPortWSDDServiceName() {
        return RegistryPortWSDDServiceName;
    }

    public void setRegistryPortWSDDServiceName(java.lang.String name) {
        RegistryPortWSDDServiceName = name;
    }

    public dire.registry.webservices.Registry getRegistryPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(RegistryPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getRegistryPort(endpoint);
    }

    public dire.registry.webservices.Registry getRegistryPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            dire.registry.webservices.RegistryBindingStub _stub = new dire.registry.webservices.RegistryBindingStub(portAddress, this);
            _stub.setPortName(getRegistryPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setRegistryPortEndpointAddress(java.lang.String address) {
        RegistryPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (dire.registry.webservices.Registry.class.isAssignableFrom(serviceEndpointInterface)) {
                dire.registry.webservices.RegistryBindingStub _stub = new dire.registry.webservices.RegistryBindingStub(new java.net.URL(RegistryPort_address), this);
                _stub.setPortName(getRegistryPortWSDDServiceName());
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
        if ("RegistryPort".equals(inputPortName)) {
            return getRegistryPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://registry.dire/", "RegistryService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://registry.dire/", "RegistryPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("RegistryPort".equals(portName)) {
            setRegistryPortEndpointAddress(address);
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
