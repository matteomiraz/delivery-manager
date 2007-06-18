package interest;

import java.util.Date;

import junit.framework.TestCase;
import eu.secse.deliveryManager.interest.InterestAdditionalInformation;
import eu.secse.deliveryManager.interest.InterestService;
import eu.secse.deliveryManager.model.DFacetSpecificationSchema;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.EFacetSpecificationSchema;
import eu.secse.deliveryManager.model.EFacetSpecificationXML;

public class FilterTest extends TestCase {

	private DService srv;
	private DFacetSpecificationSchema dft;
	private DFacetSpecificationSchema dft2;

	@Override
	protected void setUp() throws Exception {
		srv = new DService("123", "name", "versione", "nessuna", false, new Date(), "timestamp", "isoTimestamp");
		srv.addSpecType(new EFacetSpecificationSchema("schemaID", "facetTypeSpec1", "<a>uno</a>", "typeName", new EFacetSpecificationXML("xmlID", "xmlName", "<a>xmlDocument1</a>", "timestamp", "isoTimestamp"), "timestamp", "isoTimestamp"));
		srv.addSpecType(new EFacetSpecificationSchema("schemaID2", "facetTypeSpec2", "<a>due</a>", "typeName", new EFacetSpecificationXML("xmlID2", "xmlName2", "<a>xmlDocument2</a>", "timestamp", "isoTimestamp"), "timestamp", "isoTimestamp"));

		dft = new DFacetSpecificationSchema("dft1", "dft name 1", "<dft>uno</dft>", "dfttype1", new EFacetSpecificationXML("dftXml1", "dftName1", "<dft>xml1</dft>", "timestamp", "isoTimestamp"), srv.getServiceID(), new Date(), "timestamp", "isoTimestamp");
		dft2 = new DFacetSpecificationSchema("dft1", "dft name 1", "<dft>uno</dft>", "dfttype1", new EFacetSpecificationXML("dftXml1", "dftName1", "<dft>xml1</dft>", "timestamp", "isoTimestamp"), "nessun servizio", new Date(), "timestamp", "isoTimestamp");
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		srv = null;
		dft = null;
		dft2 = null;
	}
	
	public void testService() throws Exception {
		InterestService filter = new InterestService(srv.getServiceID());
		
		assertTrue(filter.matches(srv));
		assertFalse(filter.matches(dft));
		assertFalse(filter.matches(dft2));
	}
	
	public void testAddInfo() throws Exception {
		InterestAdditionalInformation filter = new InterestAdditionalInformation(srv.getServiceID(), null, null);
		
		assertTrue(filter.matches(srv));
		assertTrue(filter.matches(dft));
		assertFalse(filter.matches(dft2));
	}
}
