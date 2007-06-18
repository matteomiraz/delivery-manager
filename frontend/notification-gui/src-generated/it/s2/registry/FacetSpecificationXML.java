/**
 * FacetSpecificationXML.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package it.s2.registry;

public class FacetSpecificationXML  extends it.s2.registry.AbstractFacetSpecification  implements java.io.Serializable {
    private java.lang.String facetTypeName;

    private java.lang.String facetSpecificationXSDId;

    private java.lang.String facetSpecificationXMLId;

    public FacetSpecificationXML() {
    }

    public FacetSpecificationXML(
           long providerId,
           java.lang.String name,
           java.lang.String document,
           java.lang.String serviceId,
           java.lang.String facetTypeName,
           java.lang.String facetSpecificationXSDId,
           java.lang.String facetSpecificationXMLId) {
        super(
            providerId,
            name,
            document,
            serviceId);
        this.facetTypeName = facetTypeName;
        this.facetSpecificationXSDId = facetSpecificationXSDId;
        this.facetSpecificationXMLId = facetSpecificationXMLId;
    }


    /**
     * Gets the facetTypeName value for this FacetSpecificationXML.
     * 
     * @return facetTypeName
     */
    public java.lang.String getFacetTypeName() {
        return facetTypeName;
    }


    /**
     * Sets the facetTypeName value for this FacetSpecificationXML.
     * 
     * @param facetTypeName
     */
    public void setFacetTypeName(java.lang.String facetTypeName) {
        this.facetTypeName = facetTypeName;
    }


    /**
     * Gets the facetSpecificationXSDId value for this FacetSpecificationXML.
     * 
     * @return facetSpecificationXSDId
     */
    public java.lang.String getFacetSpecificationXSDId() {
        return facetSpecificationXSDId;
    }


    /**
     * Sets the facetSpecificationXSDId value for this FacetSpecificationXML.
     * 
     * @param facetSpecificationXSDId
     */
    public void setFacetSpecificationXSDId(java.lang.String facetSpecificationXSDId) {
        this.facetSpecificationXSDId = facetSpecificationXSDId;
    }


    /**
     * Gets the facetSpecificationXMLId value for this FacetSpecificationXML.
     * 
     * @return facetSpecificationXMLId
     */
    public java.lang.String getFacetSpecificationXMLId() {
        return facetSpecificationXMLId;
    }


    /**
     * Sets the facetSpecificationXMLId value for this FacetSpecificationXML.
     * 
     * @param facetSpecificationXMLId
     */
    public void setFacetSpecificationXMLId(java.lang.String facetSpecificationXMLId) {
        this.facetSpecificationXMLId = facetSpecificationXMLId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FacetSpecificationXML)) return false;
        FacetSpecificationXML other = (FacetSpecificationXML) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.facetTypeName==null && other.getFacetTypeName()==null) || 
             (this.facetTypeName!=null &&
              this.facetTypeName.equals(other.getFacetTypeName()))) &&
            ((this.facetSpecificationXSDId==null && other.getFacetSpecificationXSDId()==null) || 
             (this.facetSpecificationXSDId!=null &&
              this.facetSpecificationXSDId.equals(other.getFacetSpecificationXSDId()))) &&
            ((this.facetSpecificationXMLId==null && other.getFacetSpecificationXMLId()==null) || 
             (this.facetSpecificationXMLId!=null &&
              this.facetSpecificationXMLId.equals(other.getFacetSpecificationXMLId())));
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
        if (getFacetTypeName() != null) {
            _hashCode += getFacetTypeName().hashCode();
        }
        if (getFacetSpecificationXSDId() != null) {
            _hashCode += getFacetSpecificationXSDId().hashCode();
        }
        if (getFacetSpecificationXMLId() != null) {
            _hashCode += getFacetSpecificationXMLId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FacetSpecificationXML.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://registry.s2.it", "FacetSpecificationXML"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("facetTypeName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "facetTypeName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("facetSpecificationXSDId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "facetSpecificationXSDId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("facetSpecificationXMLId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "facetSpecificationXMLId"));
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
