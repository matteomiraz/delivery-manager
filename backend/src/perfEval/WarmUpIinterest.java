package perfEval;

import java.util.ArrayList;
import java.util.Collection;

import eu.secse.deliveryManager.interest.Interest;
import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.reds.InterestEnvelope;

public class WarmUpIinterest implements Interest {

	private static final long serialVersionUID = -6081313286992116308L;

	private final Collection<Interest>[] interests;

	public WarmUpIinterest(Collection<Interest>[] interests) {
		this.interests = interests;
	}

	public String getName() {
		return "WarmUp";
	}

	public boolean isCoveredBy(Interest other) {
		return false;
	}

	public boolean matches(Deliverable msg) {
		for (Collection<Interest> is : interests)
			for (Interest i : is)
				i.matches(msg);
		
		return true;
	}

	public float getSimilarity(Deliverable msg) {
		return 1;
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
		if (!(obj instanceof WarmUpIinterest))
			return false;

		return true;
	}

	public static Collection<InterestEnvelope> create(Collection<Interest> ... interests) {
		Collection<InterestEnvelope> w = new ArrayList<InterestEnvelope>();
		w.add(new InterestEnvelope(new WarmUpIinterest(interests), "warmup"));
		return w;
	}

}
