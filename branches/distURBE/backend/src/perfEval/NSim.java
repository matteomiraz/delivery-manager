package perfEval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import eu.secse.deliveryManager.interest.Interest;
import eu.secse.deliveryManager.interest.InterestNameSimSpecificationFacet;
import eu.secse.deliveryManager.interest.MultipleANDInterest;


// usa solo optiDB
public class NSim implements Sim {

	private static final double MIN_SIM = 0.4;

	public Collection<Interest> generate() {
		try {
			return Arrays.asList(new Interest[]{
					new MultipleANDInterest("book_price_service", 
							new InterestNameSimSpecificationFacet(null, "//operation/attribute::name", new String[] {"getPrice"}, MIN_SIM),
							new InterestNameSimSpecificationFacet(null, "//message/part/attribute::name", new String[] {"Book", "_Book"}, MIN_SIM),
							new InterestNameSimSpecificationFacet(null, "//message/part/attribute::name", new String[] {"Price", "_Price"}, MIN_SIM)
					),

					new MultipleANDInterest("car_price_service", 
							new InterestNameSimSpecificationFacet(null, "//operation/attribute::name", new String[] {"getPrice", "get_Price"}, MIN_SIM),
							new InterestNameSimSpecificationFacet(null, "//message/part/attribute::name", new String[] {"Car", "_Car"}, MIN_SIM),
							new InterestNameSimSpecificationFacet(null, "//message/part/attribute::name", new String[] {"Price", "_Price"}, MIN_SIM)
					),
					
					new MultipleANDInterest("surfing_destination_service", 
							new InterestNameSimSpecificationFacet(null, "//operation/attribute::name", new String[] {"getDestination", "get_Destination"}, MIN_SIM),
							new InterestNameSimSpecificationFacet(null, "//message/part/attribute::name", new String[] {"surfing", "_surfing"}, MIN_SIM),
							new InterestNameSimSpecificationFacet(null, "//message/part/attribute::name", new String[] {"destination", "_destination"}, MIN_SIM)
					),


					new MultipleANDInterest("surfinghiking_destination_service", 
							new InterestNameSimSpecificationFacet(null, "//operation/attribute::name", new String[] {"getDestination", "get_Destination"}, MIN_SIM),
							new InterestNameSimSpecificationFacet(null, "//message/part/attribute::name", new String[] {"surfing", "_surfing"}, MIN_SIM),
							new InterestNameSimSpecificationFacet(null, "//message/part/attribute::name", new String[] {"hiking", "_hiking"}, MIN_SIM),
							new InterestNameSimSpecificationFacet(null, "//message/part/attribute::name", new String[] {"destination", "_destination"}, MIN_SIM)
					),

					new MultipleANDInterest("surfingorganization_destination_service", 
							new InterestNameSimSpecificationFacet(null, "//operation/attribute::name", new String[] {"getDestination", "get_Destination"}, MIN_SIM),
							new InterestNameSimSpecificationFacet(null, "//message/part/attribute::name", new String[] {"surfing", "_surfing"}, MIN_SIM),
							new InterestNameSimSpecificationFacet(null, "//message/part/attribute::name", new String[] {"organization", "_organization"}, MIN_SIM),
							new InterestNameSimSpecificationFacet(null, "//message/part/attribute::name", new String[] {"destination", "_destination"}, MIN_SIM)
					),

					new MultipleANDInterest("bookpersoncreditcardaccount_price_service", 
							new InterestNameSimSpecificationFacet(null, "//operation/attribute::name", new String[] {"getPrice", "get_Price"}, MIN_SIM),
							new InterestNameSimSpecificationFacet(null, "//message/part/attribute::name", new String[] {"person", "_Person"}, MIN_SIM),
							new InterestNameSimSpecificationFacet(null, "//message/part/attribute::name", new String[] {"book", "_book"}, MIN_SIM),
							new InterestNameSimSpecificationFacet(null, "//message/part/attribute::name", new String[] {"creditcardaccount", "_creditcardaccount"}, MIN_SIM),
							new InterestNameSimSpecificationFacet(null, "//message/part/attribute::name", new String[] {"price", "_Price"}, MIN_SIM)
					),
			});
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Interest>(0);
		}
	}
	
}
