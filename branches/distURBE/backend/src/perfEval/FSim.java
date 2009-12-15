package perfEval;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import eu.secse.deliveryManager.interest.Interest;
import eu.secse.deliveryManager.interest.InterestFSimSpecificationFacet;

public class FSim implements Sim {
	private static final double MIN_SIM = 0.4;
	
	private final File BASE=new File(Common.BASE + "queries/");
	
	public Collection<Interest> generate() throws WSDLException {
		Collection<Interest> ret = new ArrayList<Interest>();
		
		File[] files = BASE.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".wsdl");
			}
		});
		
        WSDLReader lettoreWSDL = WSDLFactory.newInstance().newWSDLReader();

        for (File file : files) {
			try {
				System.out.println(file.toURI().toURL().toString());
				Definition WSDLquery = lettoreWSDL.readWSDL(file.toURI().toURL().toString());
				ret.add(new InterestFSimSpecificationFacet(file.getName(), WSDLquery, MIN_SIM));
			} catch (Exception e) {
				System.err.println("Error with " + file.getAbsolutePath());
				e.printStackTrace();
				System.err.println("--------");
			}
		}
		
		return ret;
	}

	public static void main(String[] args) throws WSDLException {
		FSim f = new FSim();
		for (Interest i : f.generate()) {
			System.out.println("---");
			System.out.println(i);
		}
	}
	
}
