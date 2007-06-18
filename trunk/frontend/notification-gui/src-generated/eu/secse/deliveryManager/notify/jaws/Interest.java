/**
 * Interest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package eu.secse.deliveryManager.notify.jaws;

public class Interest  implements java.io.Serializable {
    private java.lang.String baseServiceId;

    private int id;

    private boolean notifyFacetSpecificationSchema;

    private boolean notifyFacetSpecificationXml;

    private boolean notifyService;

    private java.lang.String providerName;

    private java.lang.String serviceId;

    private java.lang.String serviceNameRegex;

    public Interest() {
    }

    public Interest(
           java.lang.String baseServiceId,
           int id,
           boolean notifyFacetSpecificationSchema,
           boolean notifyFacetSpecificationXml,
           boolean notifyService,
           java.lang.String providerName,
           java.lang.String serviceId,
           java.lang.String serviceNameRegex) {
           this.baseServiceId = baseServiceId;
           this.id = id;
           this.notifyFacetSpecificationSchema = notifyFacetSpecificationSchema;
           this.notifyFacetSpecificationXml = notifyFacetSpecificationXml;
           this.notifyService = notifyService;
           this.providerName = providerName;
           this.serviceId = serviceId;
           this.serviceNameRegex = serviceNameRegex;
    }


    /**
     * Gets the baseServiceId value for this Interest.
     * 
     * @return baseServiceId
     */
    public java.lang.String getBaseServiceId() {
        return baseServiceId;
    }


    /**
     * Sets the baseServiceId value for this Interest.
     * 
     * @param baseServiceId
     */
    public void setBaseServiceId(java.lang.String baseServiceId) {
        this.baseServiceId = baseServiceId;
    }


    /**
     * Gets the id value for this Interest.
     * 
     * @return id
     */
    public int getId() {
        return id;
    }


    /**
     * Sets the id value for this Interest.
     * 
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }


    /**
     * Gets the notifyFacetSpecificationSchema value for this Interest.
     * 
     * @return notifyFacetSpecificationSchema
     */
    public boolean isNotifyFacetSpecificationSchema() {
        return notifyFacetSpecificationSchema;
    }


    /**
     * Sets the notifyFacetSpecificationSchema value for this Interest.
     * 
     * @param notifyFacetSpecificationSchema
     */
    public void setNotifyFacetSpecificationSchema(boolean notifyFacetSpecificationSchema) {
        this.notifyFacetSpecificationSchema = notifyFacetSpecificationSchema;
    }


    /**
     * Gets the notifyFacetSpecificationXml value for this Interest.
     * 
     * @return notifyFacetSpecificationXml
     */
    public boolean isNotifyFacetSpecificationXml() {
        return notifyFacetSpecificationXml;
    }


    /**
     * Sets the notifyFacetSpecificationXml value for this Interest.
     * 
     * @param notifyFacetSpecificationXml
     */
    public void setNotifyFacetSpecificationXml(boolean notifyFacetSpecificationXml) {
        this.notifyFacetSpecificationXml = notifyFacetSpecificationXml;
    }


    /**
     * Gets the notifyService value for this Interest.
     * 
     * @return notifyService
     */
    public boolean isNotifyService() {
        return notifyService;
    }


    /**
     * Sets the notifyService value for this Interest.
     * 
     * @param notifyService
     */
    public void setNotifyService(boolean notifyService) {
        this.notifyService = notifyService;
    }


    /**
     * Gets the providerName value for this Interest.
     * 
     * @return providerName
     */
    public java.lang.String getProviderName() {
        return providerName;
    }


    /**
     * Sets the providerName value for this Interest.
     * 
     * @param providerName
     */
    public void setProviderName(java.lang.String providerName) {
        this.providerName = providerName;
    }


    /**
     * Gets the serviceId value for this Interest.
     * 
     * @return serviceId
     */
    public java.lang.String getServiceId() {
        return serviceId;
    }


    /**
     * Sets the serviceId value for this Interest.
     * 
     * @param serviceId
     */
    public void setServiceId(java.lang.String serviceId) {
        this.serviceId = serviceId;
    }


    /**
     * Gets the serviceNameRegex value for this Interest.
     * 
     * @return serviceNameRegex
     */
    public java.lang.String getServiceNameRegex() {
        return serviceNameRegex;
    }


    /**
     * Sets the serviceNameRegex value for this Interest.
     * 
     * @param serviceNameRegex
     */
    public void setServiceNameRegex(java.lang.String serviceNameRegex) {
        this.serviceNameRegex = serviceNameRegex;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Interest)) return false;
        Interest other = (Interest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.baseServiceId==null && other.getBaseServiceId()==null) || 
             (this.baseServiceId!=null &&
              this.baseServiceId.equals(other.getBaseServiceId()))) &&
            this.id == other.getId() &&
            this.notifyFacetSpecificationSchema == other.isNotifyFacetSpecificationSchema() &&
            this.notifyFacetSpecificationXml == other.isNotifyFacetSpecificationXml() &&
            this.notifyService == other.isNotifyService() &&
            ((this.providerName==null && other.getProviderName()==null) || 
             (this.providerName!=null &&
              this.providerName.equals(other.getProviderName()))) &&
            ((this.serviceId==null && other.getServiceId()==null) || 
             (this.serviceId!=null &&
              this.serviceId.equals(other.getServiceId()))) &&
            ((this.serviceNameRegex==null && other.getServiceNameRegex()==null) || 
             (this.serviceNameRegex!=null &&
              this.serviceNameRegex.equals(other.getServiceNameRegex())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getBaseServiceId() != null) {
            _hashCode += getBaseServiceId().hashCode();
        }
        _hashCode += getId();
        _hashCode += (isNotifyFacetSpecificationSchema() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isNotifyFacetSpecificationXml() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isNotifyService() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getProviderName() != null) {
            _hashCode += getProviderName().hashCode();
        }
        if (getServiceId() != null) {
            _hashCode += getServiceId().hashCode();
        }
        if (getServiceNameRegex() != null) {
            _hashCode += getServiceNameRegex().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Interest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://notify.deliveryManager.secse.eu/jaws", "Interest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("baseServiceId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://notify.deliveryManager.secse.eu/jaws", "baseServiceId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://notify.deliveryManager.secse.eu/jaws", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("notifyFacetSpecificationSchema");
        elemField.setXmlName(new javax.xml.namespace.QName("http://notify.deliveryManager.secse.eu/jaws", "notifyFacetSpecificationSchema"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("notifyFacetSpecificationXml");
        elemField.setXmlName(new javax.xml.namespace.QName("http://notify.deliveryManager.secse.eu/jaws", "notifyFacetSpecificationXml"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("notifyService");
        elemField.setXmlName(new javax.xml.namespace.QName("http://notify.deliveryManager.secse.eu/jaws", "notifyService"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("providerName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://notify.deliveryManager.secse.eu/jaws", "providerName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://notify.deliveryManager.secse.eu/jaws", "serviceId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceNameRegex");
        elemField.setXmlName(new javax.xml.namespace.QName("http://notify.deliveryManager.secse.eu/jaws", "serviceNameRegex"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
