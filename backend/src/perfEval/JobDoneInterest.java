package perfEval;

import eu.secse.deliveryManager.interest.Interest;
import eu.secse.deliveryManager.model.Deliverable;

public class JobDoneInterest implements Interest {

	private static final long serialVersionUID = -4809765037440534721L;

	public String getName() {
		return "JobDone";
	}

	public float getSimilarity(Deliverable msg) {
		return 0;
	}

	public boolean isCoveredBy(Interest other) {
		return false;
	}

	public boolean matches(Deliverable msg) {
		return (msg instanceof JobDone);
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
