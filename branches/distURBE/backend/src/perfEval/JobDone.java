package perfEval;

import eu.secse.deliveryManager.model.Deliverable;


public class JobDone implements Deliverable {

	private final long time; 

	public JobDone(long time) {
		this.time = time;
	}
	
	public String getType() {
		return "JobDone";
	}
	
	public long getTime() {
		return time;
	}
	
}

