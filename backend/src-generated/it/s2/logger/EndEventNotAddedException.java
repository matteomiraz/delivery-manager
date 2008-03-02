/**
 * EndEventNotAddedException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.s2.logger;

public class EndEventNotAddedException extends org.apache.axis.AxisFault {
    public java.lang.Object fault;
    public java.lang.Object getFault() {
        return this.fault;
    }

    public EndEventNotAddedException() {
    }

    public EndEventNotAddedException(java.lang.Exception target) {
        super(target);
    }

    public EndEventNotAddedException(java.lang.String message, java.lang.Throwable t) {
        super(message, t);
    }

      public EndEventNotAddedException(java.lang.Object fault) {
        this.fault = fault;
    }

    /**
     * Writes the exception data to the faultDetails
     */
    public void writeDetails(javax.xml.namespace.QName qname, org.apache.axis.encoding.SerializationContext context) throws java.io.IOException {
        context.serialize(qname, null, fault);
    }
}
