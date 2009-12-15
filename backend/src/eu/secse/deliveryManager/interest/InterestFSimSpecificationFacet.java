/* This file is part of Delivery Manager.
 * (c) 2007 Matteo Miraz et al., Politecnico di Milano
 *
 * Delivery Manager is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation; either version 2 of the License, or 
 * (at your option) any later version.
 *
 * Delivery Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Delivery Manager; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package eu.secse.deliveryManager.interest;

import it.polimi.elet.si.urbe.similarity.SimilarityException;
import it.polimi.elet.si.urbe.similarity.WSDLSimilarityEngine;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;

import javax.wsdl.Definition;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.model.FacetSpec;
import eu.secse.deliveryManager.model.FacetSpecXML;
import eu.secse.deliveryManager.model.XMLCommons;

public class InterestFSimSpecificationFacet implements Interest {

	private static final long serialVersionUID = 8684903789784507923L;

	public static final String WSDL_SCHEMA="<schema xmlns=\"http://www.w3.org/2000/10/XMLSchema\"         xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\"         targetNamespace=\"http://schemas.xmlsoap.org/wsdl/\"         elementFormDefault=\"qualified\">    <element name=\"documentation\">       <complexType mixed=\"true\">          <choice minOccurs=\"0\" maxOccurs=\"unbounded\">             <any minOccurs=\"0\" maxOccurs=\"unbounded\"/>          </choice>          <anyAttribute/>       </complexType>    </element>    <complexType name=\"documented\" abstract=\"true\">       <sequence>          <element ref=\"wsdl:documentation\" minOccurs=\"0\"/>       </sequence>    </complexType>    <complexType name=\"openAtts\" abstract=\"true\">       <annotation>          <documentation>          This type is extended by  component types          to allow attributes from other namespaces to be added.          </documentation>       </annotation>       <sequence>          <element ref=\"wsdl:documentation\" minOccurs=\"0\"/>       </sequence>       <anyAttribute namespace=\"##other\"/>    </complexType>    <element name=\"definitions\" type=\"wsdl:definitionsType\">       <key name=\"message\">          <selector xpath=\"message\"/>          <field xpath=\"@name\"/>       </key>       <key name=\"portType\">          <selector xpath=\"portType\"/>          <field xpath=\"@name\"/>       </key>       <key name=\"binding\">          <selector xpath=\"binding\"/>          <field xpath=\"@name\"/>       </key>       <key name=\"service\">          <selector xpath=\"service\"/>          <field xpath=\"@name\"/>       </key>       <key name=\"import\">             <selector xpath=\"import\"/>             <field xpath=\"@namespace\"/>          </key>       <key name=\"port\">          <selector xpath=\"service/port\"/>          <field xpath=\"@name\"/>       </key>    </element>    <complexType name=\"definitionsType\">       <complexContent>          <extension base=\"wsdl:documented\">             <sequence>                <element ref=\"wsdl:import\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>                <element ref=\"wsdl:types\" minOccurs=\"0\"/>                <element ref=\"wsdl:message\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>                <element ref=\"wsdl:portType\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>                <element ref=\"wsdl:binding\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>                <element ref=\"wsdl:service\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>                <any namespace=\"##other\" minOccurs=\"0\" maxOccurs=\"unbounded\">                   <annotation>                      <documentation>to support extensibility elements </documentation>                   </annotation>                </any>             </sequence>             <attribute name=\"targetNamespace\" type=\"uriReference\" use=\"optional\"/>             <attribute name=\"name\" type=\"NMTOKEN\" use=\"optional\"/>          </extension>       </complexContent>   </complexType>    <element name=\"import\" type=\"wsdl:importType\"/>    <complexType name=\"importType\">       <complexContent>    <extension base=\"wsdl:documented\">    <attribute name=\"namespace\" type=\"uriReference\" use=\"required\"/>       <attribute name=\"location\" type=\"uriReference\" use=\"required\"/>    </extension>   </complexContent>   </complexType>    <element name=\"types\" type=\"wsdl:typesType\"/>    <complexType name=\"typesType\">       <complexContent>    <extension base=\"wsdl:documented\">    <sequence>    <any namespace=\"##other\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>   </sequence>    </extension>   </complexContent>   </complexType>    <element name=\"message\" type=\"wsdl:messageType\">       <unique name=\"part\">          <selector xpath=\"part\"/>          <field xpath=\"@name\"/>       </unique>    </element>    <complexType name=\"messageType\">       <complexContent>    <extension base=\"wsdl:documented\">    <sequence>    <element ref=\"wsdl:part\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>   </sequence>       <attribute name=\"name\" type=\"NCName\" use=\"required\"/>    </extension>   </complexContent>   </complexType>    <element name=\"part\" type=\"wsdl:partType\"/>    <complexType name=\"partType\">       <complexContent>    <extension base=\"wsdl:openAtts\">    <attribute name=\"name\" type=\"NMTOKEN\" use=\"optional\"/>       <attribute name=\"type\" type=\"QName\" use=\"optional\"/>       <attribute name=\"element\" type=\"QName\" use=\"optional\"/>    </extension>   </complexContent>   </complexType>    <element name=\"portType\" type=\"wsdl:portTypeType\"/>    <complexType name=\"portTypeType\">       <complexContent>    <extension base=\"wsdl:documented\">    <sequence>    <element ref=\"wsdl:operation\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>   </sequence>       <attribute name=\"name\" type=\"NCName\" use=\"required\"/>    </extension>   </complexContent>   </complexType>    <element name=\"operation\" type=\"wsdl:operationType\"/>    <complexType name=\"operationType\">       <complexContent>    <extension base=\"wsdl:documented\">       <choice>          <group ref=\"wsdl:one-way-operation\"/>          <group ref=\"wsdl:request-response-operation\"/>          <group ref=\"wsdl:solicit-response-operation\"/>          <group ref=\"wsdl:notification-operation\"/>       </choice>       <attribute name=\"name\" type=\"NCName\" use=\"required\"/>    </extension>   </complexContent>   </complexType>    <group name=\"one-way-operation\">       <sequence>          <element ref=\"wsdl:input\"/>       </sequence>    </group>    <group name=\"request-response-operation\">       <sequence>          <element ref=\"wsdl:input\"/>          <element ref=\"wsdl:output\"/>          <element ref=\"wsdl:fault\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>       </sequence>    </group>    <group name=\"solicit-response-operation\">       <sequence>          <element ref=\"wsdl:output\"/>          <element ref=\"wsdl:input\"/>          <element ref=\"wsdl:fault\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>       </sequence>    </group>    <group name=\"notification-operation\">       <sequence>          <element ref=\"wsdl:output\"/>       </sequence>    </group>    <element name=\"input\" type=\"wsdl:paramType\"/>    <element name=\"output\" type=\"wsdl:paramType\"/>    <element name=\"fault\" type=\"wsdl:faultType\"/>    <complexType name=\"paramType\">       <complexContent>    <extension base=\"wsdl:documented\">    <attribute name=\"name\" type=\"NMTOKEN\" use=\"optional\"/>       <attribute name=\"message\" type=\"QName\" use=\"required\"/>    </extension>   </complexContent>   </complexType>    <complexType name=\"faultType\">       <complexContent>    <extension base=\"wsdl:documented\">    <attribute name=\"name\" type=\"NMTOKEN\" use=\"required\"/>       <attribute name=\"message\" type=\"QName\" use=\"required\"/>    </extension>   </complexContent>   </complexType>    <complexType name=\"startWithExtensionsType\" abstract=\"true\">       <complexContent>    <extension base=\"wsdl:documented\">    <sequence>    <any namespace=\"##other\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>   </sequence>    </extension>   </complexContent>   </complexType>    <element name=\"binding\" type=\"wsdl:bindingType\"/>    <complexType name=\"bindingType\">       <complexContent>    <extension base=\"wsdl:startWithExtensionsType\">    <sequence>    <element name=\"operation\" type=\"wsdl:binding_operationType\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>   </sequence>       <attribute name=\"name\" type=\"NCName\" use=\"required\"/>       <attribute name=\"type\" type=\"QName\" use=\"required\"/>    </extension>   </complexContent>   </complexType>    <complexType name=\"binding_operationType\">       <complexContent>    <extension base=\"wsdl:startWithExtensionsType\">    <sequence>    <element name=\"input\" type=\"wsdl:startWithExtensionsType\" minOccurs=\"0\"/>       <element name=\"output\" type=\"wsdl:startWithExtensionsType\" minOccurs=\"0\"/>       <element name=\"fault\" minOccurs=\"0\" maxOccurs=\"unbounded\">          <complexType>             <complexContent>    <extension base=\"wsdl:startWithExtensionsType\">    <attribute name=\"name\" type=\"NMTOKEN\" use=\"required\"/>          </extension>   </complexContent>   </complexType>       </element>   </sequence>       <attribute name=\"name\" type=\"NCName\" use=\"required\"/>    </extension>   </complexContent>   </complexType>    <element name=\"service\" type=\"wsdl:serviceType\"/>    <complexType name=\"serviceType\">       <complexContent>    <extension base=\"wsdl:documented\">    <sequence>    <element ref=\"wsdl:port\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>       <any namespace=\"##other\" minOccurs=\"0\"/>   </sequence>       <a" +
	"ttribute name=\"name\" type=\"NCName\" use=\"required\"/>    </extension>   </complexContent>   </complexType>    <element name=\"port\" type=\"wsdl:portType\"/>    <complexType name=\"portType\">       <complexContent>    <extension base=\"wsdl:documented\">    <sequence>    <any namespace=\"##other\" minOccurs=\"0\"/>   </sequence>       <attribute name=\"name\" type=\"NCName\" use=\"required\"/>       <attribute name=\"binding\" type=\"QName\" use=\"required\"/>    </extension>   </complexContent>   </complexType>   <attribute name=\"arrayType\" type=\"string\"/> </schema>";;

	private static transient Document WSDL_DOM;

	public static Document getWSDL_DOM() {
		if(WSDL_DOM == null) {
			try {
				WSDL_DOM = XMLCommons.newDocumentBuilder().parse(new InputSource(new StringReader(WSDL_SCHEMA)));
			} catch(SAXException e) {
				// Non dovrebbe mai capitare!
				assert(false);
			} catch (IOException e) {
				// Non dovrebbe mai capitare!
				assert(false);
			} catch (ParserConfigurationException e) {
				// Non dovrebbe mai capitare!
				assert(false);
			}

		}
		return WSDL_DOM;
	}
	
	private final Definition query;
	private final double threshold;
	
	private transient WSDLSimilarityEngine simEngine;

	private final String name;
	
	public InterestFSimSpecificationFacet(String name, Definition WSDLquery, double threshold) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		this.name = name;
		this.query = WSDLquery;
		this.threshold = threshold;
	}

	public String getName() {
		return name;
	}
	
	public boolean matches(Deliverable elem) {
		if (elem instanceof DService) {
			DService srv = (DService) elem;

			if(srv.getSpecType() == null) return false;

			for (FacetSpec facet : srv.getSpecType()) {
				FacetSpecXML dfs = facet.getFacetSpecificationXML();

				if (XMLCommons.compare(getWSDL_DOM(), facet.getDocumentDOM())) {
					try {
						
						double value = (Double) getSimEngine().fSim(query, dfs.getWsdlDefinition()).get(0);
						System.out.println("fSim = " + value);

						if(value >= threshold) return true;
					} catch (SimilarityException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return false;
	}
	
	public float getSimilarity(Deliverable elem) {
		if (elem instanceof DService) {
			DService srv = (DService) elem;

			if(srv.getSpecType() == null) return 0;

			double max = 0;
			
			for (FacetSpec facet : srv.getSpecType()) {
				FacetSpecXML dfs = facet.getFacetSpecificationXML();

				if (XMLCommons.compare(getWSDL_DOM(), facet.getDocumentDOM())) {
					try {
						double value = (Double) getSimEngine().fSim(query, dfs.getWsdlDefinition()).get(0);
						
						if(value > max) max = value;
					} catch (SimilarityException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			return (float) max;
		}
		return 0;
	}
	
	private WSDLSimilarityEngine getSimEngine() throws SimilarityException {
		if(simEngine == null) simEngine = WSDLSimilarityEngine.getInstance();
		
		return simEngine;
	}
	
	public boolean isCoveredBy(Interest filter) {
		return this.equals(filter);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": " + name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((query == null) ? 0 : query.toString().hashCode());
		long temp;
		temp = Double.doubleToLongBits(threshold);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof InterestFSimSpecificationFacet))
			return false;
		InterestFSimSpecificationFacet other = (InterestFSimSpecificationFacet) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (query == null) {
			if (other.query != null)
				return false;
		} else if (!query.toString().equals(other.query.toString()))
			return false;
		if (Double.doubleToLongBits(threshold) != Double
				.doubleToLongBits(other.threshold))
			return false;
		return true;
	}

	
}
