package perfEval;

import eu.secse.RedsPerformanceLogger;
import eu.secse.deliveryManager.interest.Interest;
import eu.secse.deliveryManager.model.Deliverable;

public class FlushLogInterest implements Interest {

	private static final long serialVersionUID = 7560758089290984543L;

	public String getName() {
		return "FlushLogInterest";
	}

	public float getSimilarity(Deliverable msg) {
		return 0;
	}

	public boolean isCoveredBy(Interest other) {
		return false;
	}

	public boolean matches(Deliverable msg) {
		if(msg instanceof FlushLog) {
			
			System.err.println("Flushing performance logger");
			RedsPerformanceLogger.getSingleton().flush();
			System.gc();
			System.err.println("Performance logger flushed");
			
			return true;
		}
		
		return false;
	}

	@Override
	public int hashCode() {
		return 31;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}
}
