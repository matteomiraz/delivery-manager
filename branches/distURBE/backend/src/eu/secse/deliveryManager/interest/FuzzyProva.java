package eu.secse.deliveryManager.interest;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.FacetSpec;
import eu.secse.deliveryManager.model.FacetSpecXML;

public class FuzzyProva {
	public static void main(String[] args) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException {
//		FuzzySingleInterestSpecificationFacet i = new FuzzySingleInterestSpecificationFacet(null, "//foo", "self::node()/attribute::ver");
		InterestNameSimSpecificationFacet i = new InterestNameSimSpecificationFacet(null, "//foo/attribute::ver", new String[] {"c"}, 0.5);
		
		DService srv = new DService("a", "name", "uno", "null", true, "now", "isoNow", null);
		String doc = "<a><b><foo ver=\"a\">one</foo><foo ver=\"b\">two</foo></b><foo ver=\"c\">three</foo></a>";
		srv.addSpecType(new FacetSpec("facet", "prova", "<foo/>", "foo", new FacetSpecXML("xml", "xmlName", doc, "now", "isoNow"), "now", "isoNow"));
		
		i.matches(srv);
	}
}
