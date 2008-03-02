/**
 * ServiceProviderNotUnregisteredException1.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.s2.registry;

public class ServiceProviderNotUnregisteredException1 extends org.apache.axis.AxisFault {
    public java.lang.Object fault;
    public java.lang.Object getFault() {
        return this.fault;
    }

    public ServiceProviderNotUnregisteredException1() {
    }

    public ServiceProviderNotUnregisteredException1(java.lang.Exception target) {
        super(target);
    }

    public ServiceProviderNotUnregisteredException1(java.lang.String message, java.lang.Throwable t) {
        super(message, t);
    }

      public ServiceProviderNotUnregisteredException1(java.lang.Object fault) {
        this.fault = fault;
    }

    /**
     * Writes the exception data to the faultDetails
     */
    public void writeDetails(javax.xml.namespace.QName qname, org.apache.axis.encoding.SerializationContext context) throws java.io.IOException {
        context.serialize(qname, null, fault);
    }
}
