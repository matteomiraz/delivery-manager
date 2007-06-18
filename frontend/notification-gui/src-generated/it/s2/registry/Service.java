/**
 * Service.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.s2.registry;

public class Service  implements java.io.Serializable {
    private boolean allowAdditionalInformation;

    private long providerId;

    private java.lang.String previousVersionId;

    private java.lang.String name;

    private java.lang.String id;

    private java.lang.String registryId;

    private java.lang.String version;

    public Service() {
    }

    public Service(
           boolean allowAdditionalInformation,
           long providerId,
           java.lang.String previousVersionId,
           java.lang.String name,
           java.lang.String id,
           java.lang.String registryId,
           java.lang.String version) {
           this.allowAdditionalInformation = allowAdditionalInformation;
           this.providerId = providerId;
           this.previousVersionId = previousVersionId;
           this.name = name;
           this.id = id;
           this.registryId = registryId;
           this.version = version;
    }


    /**
     * Gets the allowAdditionalInformation value for this Service.
     * 
     * @return allowAdditionalInformation
     */
    public boolean isAllowAdditionalInformation() {
        return allowAdditionalInformation;
    }


    /**
     * Sets the allowAdditionalInformation value for this Service.
     * 
     * @param allowAdditionalInformation
     */
    public void setAllowAdditionalInformation(boolean allowAdditionalInformation) {
        this.allowAdditionalInformation = allowAdditionalInformation;
    }


    /**
     * Gets the providerId value for this Service.
     * 
     * @return providerId
     */
    public long getProviderId() {
        return providerId;
    }


    /**
     * Sets the providerId value for this Service.
     * 
     * @param providerId
     */
    public void setProviderId(long providerId) {
        this.providerId = providerId;
    }


    /**
     * Gets the previousVersionId value for this Service.
     * 
     * @return previousVersionId
     */
    public java.lang.String getPreviousVersionId() {
        return previousVersionId;
    }


    /**
     * Sets the previousVersionId value for this Service.
     * 
     * @param previousVersionId
     */
    public void setPreviousVersionId(java.lang.String previousVersionId) {
        this.previousVersionId = previousVersionId;
    }


    /**
     * Gets the name value for this Service.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this Service.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the id value for this Service.
     * 
     * @return id
     */
    public java.lang.String getId() {
        return id;
    }


    /**
     * Sets the id value for this Service.
     * 
     * @param id
     */
    public void setId(java.lang.String id) {
        this.id = id;
    }


    /**
     * Gets the registryId value for this Service.
     * 
     * @return registryId
     */
    public java.lang.String getRegistryId() {
        return registryId;
    }


    /**
     * Sets the registryId value for this Service.
     * 
     * @param registryId
     */
    public void setRegistryId(java.lang.String registryId) {
        this.registryId = registryId;
    }


    /**
     * Gets the version value for this Service.
     * 
     * @return version
     */
    public java.lang.String getVersion() {
        return version;
    }


    /**
     * Sets the version value for this Service.
     * 
     * @param version
     */
    public void setVersion(java.lang.String version) {
        this.version = version;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Service)) return false;
        Service other = (Service) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.allowAdditionalInformation == other.isAllowAdditionalInformation() &&
            this.providerId == other.getProviderId() &&
            ((this.previousVersionId==null && other.getPreviousVersionId()==null) || 
             (this.previousVersionId!=null &&
              this.previousVersionId.equals(other.getPreviousVersionId()))) &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.id==null && other.getId()==null) || 
             (this.id!=null &&
              this.id.equals(other.getId()))) &&
            ((this.registryId==null && other.getRegistryId()==null) || 
             (this.registryId!=null &&
              this.registryId.equals(other.getRegistryId()))) &&
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
        _hashCode += new Long(getProviderId()).hashCode();
        if (getPreviousVersionId() != null) {
            _hashCode += getPreviousVersionId().hashCode();
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getId() != null) {
            _hashCode += getId().hashCode();
        }
        if (getRegistryId() != null) {
            _hashCode += getRegistryId().hashCode();
        }
        if (getVersion() != null) {
            _hashCode += getVersion().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Service.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://registry.s2.it", "Service"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("allowAdditionalInformation");
        elemField.setXmlName(new javax.xml.namespace.QName("", "allowAdditionalInformation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("providerId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "providerId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("previousVersionId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "previousVersionId"));
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
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("registryId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "registryId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("version");
        elemField.setXmlName(new javax.xml.namespace.QName("", "version"));
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
