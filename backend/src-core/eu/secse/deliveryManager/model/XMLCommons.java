/* This file is part of Delivery Manager.
 * (c) 2006 Matteo Miraz, Politecnico di Milano
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


package eu.secse.deliveryManager.model;

import java.io.Serializable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.chiba.xml.util.DOMComparator;
import org.w3c.dom.Document;

public class XMLCommons implements Serializable {

	private static final long serialVersionUID = 6269673973184789730L;

	private static transient XPath xpath;
	private static transient DocumentBuilderFactory documentBuilderFactory;

	public static XPath newXPath() {
		if (xpath == null) xpath = XPathFactory.newInstance().newXPath();
		return xpath;
	}

	public static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
		if(documentBuilderFactory == null) documentBuilderFactory = DocumentBuilderFactory.newInstance();
		return documentBuilderFactory.newDocumentBuilder();
	}

	public static boolean compare(Document a, Document b) {
		DOMComparator dc = new DOMComparator(DOMComparator.COMPARISON_BY_EQUALS);
		dc.setIgnoreComments(true);
		dc.setIgnoreNamespaceDeclarations(false);
		dc.setIgnoreWhitespace(true);
		dc.setNamespaceAware(true);

		return dc.compare(a, b);
	}
}
