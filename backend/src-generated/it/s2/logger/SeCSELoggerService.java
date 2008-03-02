/**
 * SeCSELoggerService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.s2.logger;

public interface SeCSELoggerService extends javax.xml.rpc.Service {
    public java.lang.String getSeCSELoggerAddress();

    public it.s2.logger.SeCSELogger getSeCSELogger() throws javax.xml.rpc.ServiceException;

    public it.s2.logger.SeCSELogger getSeCSELogger(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
