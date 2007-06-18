/**
 * NotificationBeanServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package eu.secse.deliveryManager.notify.jaws;

public class NotificationBeanServiceLocator extends org.apache.axis.client.Service implements eu.secse.deliveryManager.notify.jaws.NotificationBeanService {

    public NotificationBeanServiceLocator() {
    }


    public NotificationBeanServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public NotificationBeanServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for NotificationBeanPort
    private java.lang.String NotificationBeanPort_address = "http://SeCSE:8080/notification/NotificationBean";

    public java.lang.String getNotificationBeanPortAddress() {
        return NotificationBeanPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String NotificationBeanPortWSDDServiceName = "NotificationBeanPort";

    public java.lang.String getNotificationBeanPortWSDDServiceName() {
        return NotificationBeanPortWSDDServiceName;
    }

    public void setNotificationBeanPortWSDDServiceName(java.lang.String name) {
        NotificationBeanPortWSDDServiceName = name;
    }

    public eu.secse.deliveryManager.notify.jaws.NotificationBean getNotificationBeanPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(NotificationBeanPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getNotificationBeanPort(endpoint);
    }

    public eu.secse.deliveryManager.notify.jaws.NotificationBean getNotificationBeanPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            eu.secse.deliveryManager.notify.jaws.NotificationBeanBindingStub _stub = new eu.secse.deliveryManager.notify.jaws.NotificationBeanBindingStub(portAddress, this);
            _stub.setPortName(getNotificationBeanPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setNotificationBeanPortEndpointAddress(java.lang.String address) {
        NotificationBeanPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (eu.secse.deliveryManager.notify.jaws.NotificationBean.class.isAssignableFrom(serviceEndpointInterface)) {
                eu.secse.deliveryManager.notify.jaws.NotificationBeanBindingStub _stub = new eu.secse.deliveryManager.notify.jaws.NotificationBeanBindingStub(new java.net.URL(NotificationBeanPort_address), this);
                _stub.setPortName(getNotificationBeanPortWSDDServiceName());
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
        if ("NotificationBeanPort".equals(inputPortName)) {
            return getNotificationBeanPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://notify.deliveryManager.secse.eu/jaws", "NotificationBeanService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://notify.deliveryManager.secse.eu/jaws", "NotificationBeanPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("NotificationBeanPort".equals(portName)) {
            setNotificationBeanPortEndpointAddress(address);
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
