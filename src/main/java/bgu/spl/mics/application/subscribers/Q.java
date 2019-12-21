package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.Broadcasts.Terminating;
import bgu.spl.mics.application.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.Events.GadgetAvailableEvent;
import bgu.spl.mics.application.passiveObjects.Inventory;

/**
 * Q is the only Subscriber\Publisher that has access to the {@link bgu.spl.mics.application.passiveObjects.Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Q extends Subscriber {
	Inventory myInventory;
	int tick;
	public Q(String name) {
		super(name);
		myInventory=Inventory.getInstance();
		tick=0;
	}

	@Override
	protected void initialize() {

		subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
			@Override
			public void call(TickBroadcast c) {
				if (c!=null){
					tick=c.getTimeTick();
				}
			}
		});
		subscribeEvent(GadgetAvailableEvent.class, new Callback<GadgetAvailableEvent>() {
			@Override
			public void call(GadgetAvailableEvent c) {
				if (c != null) {
					int receiveTime = tick;
					System.out.println(c.getGadget());
					if (myInventory.getItem(c.getGadget())) {
						complete(c, receiveTime);
					} else {
						complete(c, -1);
					}
				}
			}});

		
	}

}
