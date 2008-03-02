/**
 * SeCSELoggerServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.s2.logger;

public class SeCSELoggerServiceLocator extends org.apache.axis.client.Service implements it.s2.logger.SeCSELoggerService {

    public SeCSELoggerServiceLocator() {
    }


    public SeCSELoggerServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SeCSELoggerServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SeCSELogger
    private java.lang.String SeCSELogger_address = "http://localhost:8000/Logger/services/SeCSELogger";

    public java.lang.String getSeCSELoggerAddress() {
        return SeCSELogger_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SeCSELoggerWSDDServiceName = "SeCSELogger";

    public java.lang.String getSeCSELoggerWSDDServiceName() {
        return SeCSELoggerWSDDServiceName;
    }

    public void setSeCSELoggerWSDDServiceName(java.lang.String name) {
        SeCSELoggerWSDDServiceName = name;
    }

    public it.s2.logger.SeCSELogger getSeCSELogger() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SeCSELogger_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSeCSELogger(endpoint);
    }

    public it.s2.logger.SeCSELogger getSeCSELogger(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            it.s2.logger.SeCSELoggerSoapBindingStub _stub = new it.s2.logger.SeCSELoggerSoapBindingStub(portAddress, this);
            _stub.setPortName(getSeCSELoggerWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSeCSELoggerEndpointAddress(java.lang.String address) {
        SeCSELogger_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (it.s2.logger.SeCSELogger.class.isAssignableFrom(serviceEndpointInterface)) {
                it.s2.logger.SeCSELoggerSoapBindingStub _stub = new it.s2.logger.SeCSELoggerSoapBindingStub(new java.net.URL(SeCSELogger_address), this);
                _stub.setPortName(getSeCSELoggerWSDDServiceName());
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
        if ("SeCSELogger".equals(inputPortName)) {
            return getSeCSELogger();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://logger.s2.it", "SeCSELoggerService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://logger.s2.it", "SeCSELogger"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("SeCSELogger".equals(portName)) {
            setSeCSELoggerEndpointAddress(address);
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
