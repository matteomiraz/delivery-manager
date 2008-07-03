/**
 * EventNotDeletedException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.s2.logger;

public class EventNotDeletedException extends org.apache.axis.AxisFault {
    public java.lang.Object fault;
    public java.lang.Object getFault() {
        return this.fault;
    }

    public EventNotDeletedException() {
    }

    public EventNotDeletedException(java.lang.Exception target) {
        super(target);
    }

    public EventNotDeletedException(java.lang.String message, java.lang.Throwable t) {
        super(message, t);
    }

      public EventNotDeletedException(java.lang.Object fault) {
        this.fault = fault;
    }

    /**
     * Writes the exception data to the faultDetails
     */
    public void writeDetails(javax.xml.namespace.QName qname, org.apache.axis.encoding.SerializationContext context) throws java.io.IOException {
        context.serialize(qname, null, fault);
    }
}
