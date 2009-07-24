/**
 * ServiceWS.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package dire.registry.webservices;

public class ServiceWS  implements java.io.Serializable {
    private boolean allowAdditionalInformation;

    private java.lang.String fullId;

    private java.lang.String name;

    private java.lang.String previousVersionId;

    private int providerId;

    private java.lang.String timestamp;

    private java.lang.String version;

    public ServiceWS() {
    }

    public ServiceWS(
           boolean allowAdditionalInformation,
           java.lang.String fullId,
           java.lang.String name,
           java.lang.String previousVersionId,
           int providerId,
           java.lang.String timestamp,
           java.lang.String version) {
           this.allowAdditionalInformation = allowAdditionalInformation;
           this.fullId = fullId;
           this.name = name;
           this.previousVersionId = previousVersionId;
           this.providerId = providerId;
           this.timestamp = timestamp;
           this.version = version;
    }


    /**
     * Gets the allowAdditionalInformation value for this ServiceWS.
     * 
     * @return allowAdditionalInformation
     */
    public boolean isAllowAdditionalInformation() {
        return allowAdditionalInformation;
    }


    /**
     * Sets the allowAdditionalInformation value for this ServiceWS.
     * 
     * @param allowAdditionalInformation
     */
    public void setAllowAdditionalInformation(boolean allowAdditionalInformation) {
        this.allowAdditionalInformation = allowAdditionalInformation;
    }


    /**
     * Gets the fullId value for this ServiceWS.
     * 
     * @return fullId
     */
    public java.lang.String getFullId() {
        return fullId;
    }


    /**
     * Sets the fullId value for this ServiceWS.
     * 
     * @param fullId
     */
    public void setFullId(java.lang.String fullId) {
        this.fullId = fullId;
    }


    /**
     * Gets the name value for this ServiceWS.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this ServiceWS.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the previousVersionId value for this ServiceWS.
     * 
     * @return previousVersionId
     */
    public java.lang.String getPreviousVersionId() {
        return previousVersionId;
    }


    /**
     * Sets the previousVersionId value for this ServiceWS.
     * 
     * @param previousVersionId
     */
    public void setPreviousVersionId(java.lang.String previousVersionId) {
        this.previousVersionId = previousVersionId;
    }


    /**
     * Gets the providerId value for this ServiceWS.
     * 
     * @return providerId
     */
    public int getProviderId() {
        return providerId;
    }


    /**
     * Sets the providerId value for this ServiceWS.
     * 
     * @param providerId
     */
    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }


    /**
     * Gets the timestamp value for this ServiceWS.
     * 
     * @return timestamp
     */
    public java.lang.String getTimestamp() {
        return timestamp;
    }


    /**
     * Sets the timestamp value for this ServiceWS.
     * 
     * @param timestamp
     */
    public void setTimestamp(java.lang.String timestamp) {
        this.timestamp = timestamp;
    }


    /**
     * Gets the version value for this ServiceWS.
     * 
     * @return version
     */
    public java.lang.String getVersion() {
        return version;
    }


    /**
     * Sets the version value for this ServiceWS.
     * 
     * @param version
     */
    public void setVersion(java.lang.String version) {
        this.version = version;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ServiceWS)) return false;
        ServiceWS other = (ServiceWS) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.allowAdditionalInformation == other.isAllowAdditionalInformation() &&
            ((this.fullId==null && other.getFullId()==null) || 
             (this.fullId!=null &&
              this.fullId.equals(other.getFullId()))) &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.previousVersionId==null && other.getPreviousVersionId()==null) || 
             (this.previousVersionId!=null &&
              this.previousVersionId.equals(other.getPreviousVersionId()))) &&
            this.providerId == other.getProviderId() &&
            ((this.timestamp==null && other.getTimestamp()==null) || 
             (this.timestamp!=null &&
              this.timestamp.equals(other.getTimestamp()))) &&
            ((this.version==null && other.getVersion()==null) || 
             (this.version!=null &&
              this.version.equals(other.getVersion())));
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
        _hashCode += (isAllowAdditionalInformation() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getFullId() != null) {
            _hashCode += getFullId().hashCode();
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getPreviousVersionId() != null) {
            _hashCode += getPreviousVersionId().hashCode();
        }
        _hashCode += getProviderId();
        if (getTimestamp() != null) {
            _hashCode += getTimestamp().hashCode();
        }
        if (getVersion() != null) {
            _hashCode += getVersion().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ServiceWS.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://registry.dire/", "serviceWS"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("allowAdditionalInformation");
        elemField.setXmlName(new javax.xml.namespace.QName("", "allowAdditionalInformation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fullId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fullId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("previousVersionId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "previousVersionId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("providerId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "providerId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timestamp");
        elemField.setXmlName(new javax.xml.namespace.QName("", "timestamp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("version");
        elemField.setXmlName(new javax.xml.namespace.QName("", "version"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
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
