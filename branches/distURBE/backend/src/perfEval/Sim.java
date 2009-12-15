package perfEval;

import java.util.Collection;

import eu.secse.deliveryManager.interest.Interest;

public interface Sim {
	public Collection<Interest> generate() throws Exception;
}
