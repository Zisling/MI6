package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.Broadcasts.Terminating;
import bgu.spl.mics.application.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.Events.GadgetAvailableEvent;
import bgu.spl.mics.application.passiveObjects.Inventory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Q is the only Subscriber\Publisher that has access to the {@link bgu.spl.mics.application.passiveObjects.Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Q extends Subscriber {
	Inventory myInventory;
	AtomicInteger tick;
	private CountDownLatch latch;
	public Q(String name,CountDownLatch latch) {
		super(name);
		myInventory=Inventory.getInstance();
		tick=new AtomicInteger(0);
		this.latch=latch;
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
					int receiveTime = tick.get();
					if (myInventory.getItem(c.getGadget())) {
						complete(c, receiveTime);
					} else {
						complete(c, -1);
					}
				}
			}});
		latch.countDown();
	}
}
