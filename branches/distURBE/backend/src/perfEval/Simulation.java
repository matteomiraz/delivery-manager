package perfEval;

import java.util.ArrayList;
import java.util.Collection;

import javax.wsdl.WSDLException;

import polimi.reds.DispatchingService;
import polimi.reds.Message;
import polimi.reds.TCPDispatchingService;
import eu.secse.deliveryManager.interest.Interest;
import eu.secse.deliveryManager.model.DService;
import eu.secse.deliveryManager.model.Deliverable;
import eu.secse.deliveryManager.reds.Envelope;
import eu.secse.deliveryManager.reds.InterestEnvelope;

public class Simulation {
 		
	private static final boolean CALC_SIMS = false;
	
	private final DispatchingService sender;
	private final DispatchingService receiver;
	private final Thread t;
	
	private volatile boolean active = true;
	private volatile long received = -1;
	
	public Simulation(String senderUrl, String receiverUrl) {
		this.sender = connect(senderUrl);
		this.receiver = connect(receiverUrl);

		t = new Thread(new Runnable() {
			public void run() {
				while(active) {
					try {
						Message msg = receiver.getNextMessage();
						if(msg==null) continue;
						
						if(msg instanceof Envelope) {
							Deliverable d = ((Envelope)msg).getObject();
							if(d instanceof JobDone) {
								synchronized (Simulation.this) {
									received = ((JobDone)d).getTime();
									Simulation.this.notifyAll();
								}
								System.out.print("!" + (System.currentTimeMillis() - received) + "!");
							} else if(d instanceof DeleteTables || d instanceof FlushLog) {
								System.out.print("_");
							} else 
								System.out.print(".");
						}
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		});
		t.setDaemon(true);
		t.start();

		receiver.unsubscribeAll();

		System.out.print("RESET");
		
		receiver.subscribe(new InterestEnvelope(new FlushLogInterest(), "receiver"));
		receiver.subscribe(new InterestEnvelope(new DeleteTablesInterest(), "receiver"));
		receiver.subscribe(new InterestEnvelope(new JobDoneInterest(), "receiver"));
		dormi(2500);
		
		sender.publish(new Envelope(new DeleteTables()));
		sender.publish(new Envelope(new FlushLog()));
		dormi(2500);

		System.out.println("done");
		
	}

	public Simulation simula(final Collection<Envelope> deliverables, final Collection<InterestEnvelope> intEnv) {
		System.out.println();
		System.out.println();

		// 1. p.o. interest: sottoscrizione, invio tutti i msg, desottoscrizione, reset
		sim1(deliverables, intEnv);
		
		// 2. p.o. interest: sottoscrizione, invio tutti i msg, desottoscrizione
		//    alla fine: reset
		sim2(deliverables, intEnv);

		// 3. inizio: sottoscrizione
		//	          invio tutti i servizi
		//    		  desottoscrizione
		//            reset

		sim3(deliverables, intEnv);

		sim4(deliverables, intEnv);

		
		return this;
	}

	@SuppressWarnings("deprecation")
	private void sim4(final Collection<Envelope> deliverables,
			final Collection<InterestEnvelope> intEnv) {
		System.out.println("\n phase 4:  subscribing...");
		for (InterestEnvelope i : intEnv)
			receiver.subscribe(i.getCustomNode("-phase4"));
		System.out.print(" done");
		
		dormi(2500);

		for (int i = 0; i < 10; i++) {
			System.out.println("\n  sending... (" + i + "/ 10)");
			sendAll(deliverables);
			aspetta();
		}
		System.out.println(" done");
		aspetta();
			
		for (InterestEnvelope i : intEnv)
			receiver.unsubscribe(i.getCustomNode("-phase4"));
		
		dormi(2500);
	}

	@SuppressWarnings("deprecation")
	private void sim3(final Collection<Envelope> deliverables,
			final Collection<InterestEnvelope> intEnv) {
		System.out.println("\n phase 3:  subscribing...");
		for (InterestEnvelope i : intEnv)
			receiver.subscribe(i.getCustomNode("-phase3"));
		System.out.println(" done");
		
		dormi(2500);

		System.out.println("  sending...");
		sendAll(deliverables);
		System.out.println(" done");
		aspetta();
			
		for (InterestEnvelope i : intEnv)
			receiver.unsubscribe(i.getCustomNode("-phase3"));
		
		dormi(2500);
	}

	@SuppressWarnings("deprecation")
	private void sim2(final Collection<Envelope> deliverables,
			final Collection<InterestEnvelope> intEnv) {
		int n = 0;
		int tot = intEnv.size();
		resetTables();
		for (InterestEnvelope i : intEnv) {
			i = i.getCustomNode("-phase2");
			System.out.print("\n phase2 interest " + n++ + "/" + tot );
			receiver.subscribe(i);
			dormi(1000);

			sendAll(deliverables);
			aspetta();

			System.out.println();
			receiver.unsubscribe(i);
			dormi(1000);
		}
		aspetta();
		resetTables();
	}

	@SuppressWarnings("deprecation")
	private void sim1(final Collection<Envelope> deliverables,
			final Collection<InterestEnvelope> intEnv) {
		resetTables();
		int n = 0, tot = intEnv.size();
		for (InterestEnvelope i : intEnv) {
			i = i.getCustomNode("-phase1");
			System.out.print("\nphase1 interest " + n++ + "/" + tot );
			receiver.subscribe(i);
			dormi(1000);

			sendAll(deliverables);
			System.out.println();
			aspetta();

			receiver.unsubscribe(i);
			resetTables();
			dormi(1000);
		}
		aspetta();
	}

	public void aspetta() {
		this.received = -1;
		sender.publish(new Envelope(new JobDone(System.currentTimeMillis())));

		synchronized (this) {
			while (received < 0) {
				try {
					this.wait(10000);
				} catch (InterruptedException e) {
				}
			}
		}
	}
	
	public void done() {
		aspetta();

		sender.close();
		receiver.close();
		
		dormi(2000);
	}
	
	public static void main(String[] args) throws WSDLException {
		
		try {
			if(CALC_SIMS)
				calcSim(new Sim[] {
						new NSim(),
						new FSim(),
						new XSim()
				});
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		if(args.length != 2) {
			System.out.println("Specify the sender and the receiver!");
			System.exit(1);
		}

		String senderUrl = args[0];
		String receiverUrl = args[1];
		
		final Collection<Envelope> deliverables = convertServices(Services.get());

		try {
			warmUp(senderUrl, receiverUrl, deliverables);
		} catch (Throwable e) {
			System.err.println(e.getMessage());
		}
		
		try {
			xSim(senderUrl, receiverUrl, deliverables);
		} catch (Throwable e) {
			System.err.println(e.getMessage());
		}

		try {
			nSim(senderUrl, receiverUrl, deliverables);
		} catch (Throwable e) {
			System.err.println(e.getMessage());
		}

		try {
			fSim(senderUrl, receiverUrl, deliverables);
		} catch (Throwable e) {
			System.err.println(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private static void warmUp(String senderUrl, String receiverUrl, Collection<Envelope> deliverables) throws WSDLException {
		System.out.println(" --- WARM UP  ---");
		Collection<InterestEnvelope> warmUp = WarmUpIinterest.create(
				new XSim().generate(),
				new NSim().generate(), 
				new FSim().generate()
				);
		new Simulation(senderUrl, receiverUrl).simula(deliverables, warmUp).done();	
	}

	private static void fSim(String senderUrl, String receiverUrl, final Collection<Envelope> deliverables) throws WSDLException {
		final Collection<InterestEnvelope> fsim = convertInterest(new FSim().generate(), "f");
		for (int i = 0; i < 10; i++) {
			System.out.println();
			System.out.println("\n --- SIMULATION " + i + " / 10 fSim ---");
			new Simulation(senderUrl, receiverUrl).simula(deliverables, fsim).done();	
		}
	}

	private static void nSim(String senderUrl, String receiverUrl, final Collection<Envelope> deliverables) {
		final Collection<InterestEnvelope> nsim = convertInterest(new NSim().generate(), "n");
		for (int i = 0; i < 10; i++) {
			System.out.println();
			System.out.println("\n --- SIMULATION " + i + " / 10 nSim ---");
			new Simulation(senderUrl, receiverUrl).simula(deliverables, nsim).done();	
		}
	}

	private static void xSim(String senderUrl, String receiverUrl, final Collection<Envelope> deliverables) {
		final Collection<InterestEnvelope> xsim = convertInterest(new XSim().generate(), "x");
		for (int i = 0; i < 10; i++) {
			System.out.println();
			System.out.println("\n --- SIMULATION " + i + " / 10 XPath ---");
			new Simulation(senderUrl, receiverUrl).simula(deliverables, xsim).done();	
		}
	}

	private static DispatchingService connect(String address) {
		try {
			String[] parts = address.split(":");
			TCPDispatchingService d = new TCPDispatchingService(parts[1], Integer.parseInt(parts[2]));
			d.open();
			return d;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}


	private void sendAll(final Collection<Envelope> deliverables) {
		
		int n = 0, tot = deliverables.size();
		for (Envelope d : deliverables) {
			if(n++ % 100 == 0)
				System.out.print("\n    sending " + n + "/" + tot);
			
			sender.publish(d);
			dormi(10);
		}
	}

	private void resetTables() {
		
		sender.publish(new Envelope(new DeleteTables()));
		sender.publish(new Envelope(new FlushLog()));
		
		dormi(2500);
	}

	private static void calcSim(Sim[] sims) throws WSDLException, Exception {
		Collection<DService> services = Services.get();

		for (Sim sim : sims) {
			try {
				System.err.println("----");
				System.err.println();
				System.err.println(sim.getClass().getCanonicalName());
				System.err.println();
				
				for (Interest i : sim.generate()) {
					for (DService s : services) {
//					if(i.matches(s)) {
							System.err.println(i.getName() + ";" + s.getName() + ";" + i.getSimilarity(s));
//					}
					}
				}
			} catch (Throwable e) {
				System.err.println(e.toString());
			}
		}
	}

	private static Collection<InterestEnvelope> convertInterest(Collection<Interest> interests, String str) {
		Collection<InterestEnvelope> ret = new ArrayList<InterestEnvelope>();
		
		for (Interest i : interests) 
			ret.add(new InterestEnvelope(i, str));

		return ret;
	}

	private static Collection<Envelope> convertServices(Collection<DService> services) {
		Collection<Envelope> ret = new ArrayList<Envelope>();
		
		for (DService s : services) 
			ret.add(new Envelope(s));
		
		return ret;
	}

	private static void dormi(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
	}
}
