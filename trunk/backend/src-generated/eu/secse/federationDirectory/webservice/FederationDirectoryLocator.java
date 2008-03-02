/**
 * FederationDirectoryLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package eu.secse.federationDirectory.webservice;

public class FederationDirectoryLocator extends org.apache.axis.client.Service implements eu.secse.federationDirectory.webservice.FederationDirectory {

    public FederationDirectoryLocator() {
    }


    public FederationDirectoryLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public FederationDirectoryLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for IDirectoryLookupWSPort
    private java.lang.String IDirectoryLookupWSPort_address = "http://Gemini:8080/directory/DirectoryLookupWS";

    public java.lang.String getIDirectoryLookupWSPortAddress() {
        return IDirectoryLookupWSPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String IDirectoryLookupWSPortWSDDServiceName = "IDirectoryLookupWSPort";

    public java.lang.String getIDirectoryLookupWSPortWSDDServiceName() {
        return IDirectoryLookupWSPortWSDDServiceName;
    }

    public void setIDirectoryLookupWSPortWSDDServiceName(java.lang.String name) {
        IDirectoryLookupWSPortWSDDServiceName = name;
    }

    public eu.secse.federationDirectory.webservice.IDirectoryLookupWS getIDirectoryLookupWSPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(IDirectoryLookupWSPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getIDirectoryLookupWSPort(endpoint);
    }

    public eu.secse.federationDirectory.webservice.IDirectoryLookupWS getIDirectoryLookupWSPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            eu.secse.federationDirectory.webservice.IDirectoryLookupWSBindingStub _stub = new eu.secse.federationDirectory.webservice.IDirectoryLookupWSBindingStub(portAddress, this);
            _stub.setPortName(getIDirectoryLookupWSPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setIDirectoryLookupWSPortEndpointAddress(java.lang.String address) {
        IDirectoryLookupWSPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (eu.secse.federationDirectory.webservice.IDirectoryLookupWS.class.isAssignableFrom(serviceEndpointInterface)) {
                eu.secse.federationDirectory.webservice.IDirectoryLookupWSBindingStub _stub = new eu.secse.federationDirectory.webservice.IDirectoryLookupWSBindingStub(new java.net.URL(IDirectoryLookupWSPort_address), this);
                _stub.setPortName(getIDirectoryLookupWSPortWSDDServiceName());
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
        if ("IDirectoryLookupWSPort".equals(inputPortName)) {
            return getIDirectoryLookupWSPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://secse.eu/federationDirectory/webservice", "FederationDirectory");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://secse.eu/federationDirectory/webservice", "IDirectoryLookupWSPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("IDirectoryLookupWSPort".equals(portName)) {
            setIDirectoryLookupWSPortEndpointAddress(address);
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
