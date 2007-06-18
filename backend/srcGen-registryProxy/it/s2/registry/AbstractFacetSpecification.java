/**
 * AbstractFacetSpecification.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.s2.registry;

public abstract class AbstractFacetSpecification  implements java.io.Serializable {
    private long providerId;

    private java.lang.String document;

    private java.lang.String name;

    private java.lang.String timestamp;

    private java.lang.String ISOTimestamp;

    private java.lang.String serviceId;

    public AbstractFacetSpecification() {
    }

    public AbstractFacetSpecification(
           long providerId,
           java.lang.String document,
           java.lang.String name,
           java.lang.String timestamp,
           java.lang.String ISOTimestamp,
           java.lang.String serviceId) {
           this.providerId = providerId;
           this.document = document;
           this.name = name;
           this.timestamp = timestamp;
           this.ISOTimestamp = ISOTimestamp;
           this.serviceId = serviceId;
    }


    /**
     * Gets the providerId value for this AbstractFacetSpecification.
     * 
     * @return providerId
     */
    public long getProviderId() {
        return providerId;
    }


    /**
     * Sets the providerId value for this AbstractFacetSpecification.
     * 
     * @param providerId
     */
    public void setProviderId(long providerId) {
        this.providerId = providerId;
    }


    /**
     * Gets the document value for this AbstractFacetSpecification.
     * 
     * @return document
     */
    public java.lang.String getDocument() {
        return document;
    }


    /**
     * Sets the document value for this AbstractFacetSpecification.
     * 
     * @param document
     */
    public void setDocument(java.lang.String document) {
        this.document = document;
    }


    /**
     * Gets the name value for this AbstractFacetSpecification.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this AbstractFacetSpecification.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the timestamp value for this AbstractFacetSpecification.
     * 
     * @return timestamp
     */
    public java.lang.String getTimestamp() {
        return timestamp;
    }


    /**
     * Sets the timestamp value for this AbstractFacetSpecification.
     * 
     * @param timestamp
     */
    public void setTimestamp(java.lang.String timestamp) {
        this.timestamp = timestamp;
    }


    /**
     * Gets the ISOTimestamp value for this AbstractFacetSpecification.
     * 
     * @return ISOTimestamp
     */
    public java.lang.String getISOTimestamp() {
        return ISOTimestamp;
    }


    /**
     * Sets the ISOTimestamp value for this AbstractFacetSpecification.
     * 
     * @param ISOTimestamp
     */
    public void setISOTimestamp(java.lang.String ISOTimestamp) {
        this.ISOTimestamp = ISOTimestamp;
    }


    /**
     * Gets the serviceId value for this AbstractFacetSpecification.
     * 
     * @return serviceId
     */
    public java.lang.String getServiceId() {
        return serviceId;
    }


    /**
     * Sets the serviceId value for this AbstractFacetSpecification.
     * 
     * @param serviceId
     */
    public void setServiceId(java.lang.String serviceId) {
        this.serviceId = serviceId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AbstractFacetSpecification)) return false;
        AbstractFacetSpecification other = (AbstractFacetSpecification) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.providerId == other.getProviderId() &&
            ((this.document==null && other.getDocument()==null) || 
             (this.document!=null &&
              this.document.equals(other.getDocument()))) &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.timestamp==null && other.getTimestamp()==null) || 
             (this.timestamp!=null &&
              this.timestamp.equals(other.getTimestamp()))) &&
            ((this.ISOTimestamp==null && other.getISOTimestamp()==null) || 
             (this.ISOTimestamp!=null &&
              this.ISOTimestamp.equals(other.getISOTimestamp()))) &&
            ((this.serviceId==null && other.getServiceId()==null) || 
             (this.serviceId!=null &&
              this.serviceId.equals(other.getServiceId())));
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
        _hashCode += new Long(getProviderId()).hashCode();
        if (getDocument() != null) {
            _hashCode += getDocument().hashCode();
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getTimestamp() != null) {
            _hashCode += getTimestamp().hashCode();
        }
        if (getISOTimestamp() != null) {
            _hashCode += getISOTimestamp().hashCode();
        }
        if (getServiceId() != null) {
            _hashCode += getServiceId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AbstractFacetSpecification.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://registry.s2.it", "AbstractFacetSpecification"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("providerId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "providerId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("document");
        elemField.setXmlName(new javax.xml.namespace.QName("", "document"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timestamp");
        elemField.setXmlName(new javax.xml.namespace.QName("", "timestamp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ISOTimestamp");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ISOTimestamp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "serviceId"));
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
