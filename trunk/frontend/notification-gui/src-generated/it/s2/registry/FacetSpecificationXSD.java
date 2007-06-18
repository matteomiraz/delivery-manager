/**
 * FacetSpecificationXSD.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.s2.registry;

public class FacetSpecificationXSD  extends it.s2.registry.AbstractFacetSpecification  implements java.io.Serializable {
    private java.lang.String facetSpecificationXSDId;

    private boolean additionalInformation;

    public FacetSpecificationXSD() {
    }

    public FacetSpecificationXSD(
           long providerId,
           java.lang.String name,
           java.lang.String document,
           java.lang.String serviceId,
           java.lang.String facetSpecificationXSDId,
           boolean additionalInformation) {
        super(
            providerId,
            name,
            document,
            serviceId);
        this.facetSpecificationXSDId = facetSpecificationXSDId;
        this.additionalInformation = additionalInformation;
    }


    /**
     * Gets the facetSpecificationXSDId value for this FacetSpecificationXSD.
     * 
     * @return facetSpecificationXSDId
     */
    public java.lang.String getFacetSpecificationXSDId() {
        return facetSpecificationXSDId;
    }


    /**
     * Sets the facetSpecificationXSDId value for this FacetSpecificationXSD.
     * 
     * @param facetSpecificationXSDId
     */
    public void setFacetSpecificationXSDId(java.lang.String facetSpecificationXSDId) {
        this.facetSpecificationXSDId = facetSpecificationXSDId;
    }


    /**
     * Gets the additionalInformation value for this FacetSpecificationXSD.
     * 
     * @return additionalInformation
     */
    public boolean isAdditionalInformation() {
        return additionalInformation;
    }


    /**
     * Sets the additionalInformation value for this FacetSpecificationXSD.
     * 
     * @param additionalInformation
     */
    public void setAdditionalInformation(boolean additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FacetSpecificationXSD)) return false;
        FacetSpecificationXSD other = (FacetSpecificationXSD) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.facetSpecificationXSDId==null && other.getFacetSpecificationXSDId()==null) || 
             (this.facetSpecificationXSDId!=null &&
              this.facetSpecificationXSDId.equals(other.getFacetSpecificationXSDId()))) &&
            this.additionalInformation == other.isAdditionalInformation();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getFacetSpecificationXSDId() != null) {
            _hashCode += getFacetSpecificationXSDId().hashCode();
        }
        _hashCode += (isAdditionalInformation() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FacetSpecificationXSD.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://registry.s2.it", "FacetSpecificationXSD"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("facetSpecificationXSDId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "facetSpecificationXSDId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("additionalInformation");
        elemField.setXmlName(new javax.xml.namespace.QName("", "additionalInformation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
