package perfEval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import eu.secse.deliveryManager.interest.Interest;
import eu.secse.deliveryManager.interest.MultipleANDInterest;
import eu.secse.deliveryManager.interest.MultipleORInterest;
import eu.secse.deliveryManager.interest.SingleInterestSpecificationFacet;

public class XSim implements Sim {

	public Collection<Interest> generate() {
		try {
			return Arrays.asList(new Interest[]{
					new MultipleANDInterest("book_price_service", 
							new MultipleORInterest(null,
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//portType/operation[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'getprice']"),
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//portType/operation[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'get_price']")
							),
							new MultipleORInterest(null, 
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'book']"),
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '_book']")
							),
							new MultipleORInterest(null, 
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'price']"),
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '_price']")
							)
					),

					new MultipleANDInterest("car_price_service", 
							new MultipleORInterest(null, 
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//portType/operation[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'getprice']"),
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//portType/operation[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'get_price']")
							),
							new MultipleORInterest(null, 
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'car']"),
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '_car']")
							),
							new MultipleORInterest(null, 
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'price']"),
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '_price']")
							)
					),

					new MultipleANDInterest("surfing_destination_service", 
							new MultipleORInterest(null, 
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//portType/operation[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'getdestination']"),
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//portType/operation[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'get_destination']")
							),
							new MultipleORInterest(null, 
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'surfing']"),
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '_surfing']")
							),
							new MultipleORInterest(null, 
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'destination']"),
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '_destination']")
							)
					),

					new MultipleANDInterest("surfinghiking_destination_service", 
							new MultipleORInterest(null, 
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//portType/operation[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'getdestination']"),
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//portType/operation[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'get_destination']")
							),
							new MultipleORInterest(null, 
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'surfing']"),
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '_surfing']")
							),
							new MultipleORInterest(null, 
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'hiking']"),
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '_hiking']")
							),
							new MultipleORInterest(null, 
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'destination']"),
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '_destination']")
							)
					),

					new MultipleANDInterest("surfingorganization_destination_service", 
							new MultipleORInterest(null, 
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//portType/operation[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'getdestination']"),
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//portType/operation[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'get_destination']")
							),
							new MultipleORInterest(null, 
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'surfing']"),
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '_surfing']")
							),

							new MultipleORInterest(null, 
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'organization']"),
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '_organization']")
							),
							new MultipleORInterest(null, 
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'destination']"),
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '_destination']")
							)
					),

					new MultipleANDInterest("bookpersoncreditcardaccount_price_service", 
							new MultipleORInterest(null, 
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//portType/operation[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'getprice']"),
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//portType/operation[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'get_price']")
							),
							new MultipleORInterest(null, 
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'person']"),
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '_person']")
							),
							new MultipleORInterest(null, 
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'book']"),
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '_book']")
							),
							new MultipleORInterest(null, 
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'creditcardaccount']"),
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '_creditcardaccount']")
							),
							new MultipleORInterest(null, 
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'price']"),
									new SingleInterestSpecificationFacet(null, Common.WSDL_SCHEMA, "//message/part[translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '_price']")
							)
					),

			});
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Interest>(0);
		}
	}
	
}
