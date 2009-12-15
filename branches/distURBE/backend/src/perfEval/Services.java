package perfEval;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;

import javax.wsdl.WSDLException;

import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.FacetSpec;
import eu.secse.deliveryManager.model.FacetSpecXML;

public class Services {
	private static final File BASE=new File(Common.BASE + "services/");

	public static Collection<DService> get() throws WSDLException {
		Collection<DService> ret = new ArrayList<DService>();
		
		File[] files = BASE.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".wsdl");
				//return name.equals("BookPrice.wsdl");
			}
		});

		for (File file : files) {
			try {
				String wsdl = Common.read(file).toString();
				DService srv = new DService(file.getName(), file.getName(), "versione", "prevVersion", false, "15-10-1981 15.34.00", "15-10-1981 15.34.00", null);
				srv.addSpecType(new FacetSpec(file.getName(), "wsdl", Common.WSDL_SCHEMA, "wsdl", new FacetSpecXML(file.getName(), "wsdl", wsdl, "15-10-1981 15.34.00", "15-10-1981 15.34.00"), "15-10-1981 15.34.00", "15-10-1981 15.34.00"));
				ret.add(srv);
			} catch (Exception e) {
				System.err.println("Error with " + file.getAbsolutePath());
				e.printStackTrace();
				System.err.println("--------");
			}
		}
		
		return ret;
	}
}
