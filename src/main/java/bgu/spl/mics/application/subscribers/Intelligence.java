package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.Events.MissionReceviedEvent;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

/**
 * A Publisher\Subscriber.
 * Holds a list of Info objects and sends them
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence extends Subscriber {
	private HashMap<Integer, Queue<MissionInfo>> MissionMap;
	private CountDownLatch latch;

	/**
	 * Default Constructor of Intelligence
	 */
	public Intelligence() {
		super("default");
	}

	/**
	 * Intelligence's Constructor.
	 * @param name-number Id of Intelligence.
	 * @param MissionMap-Contains missions of Intelligence.
	 * @param latch-initializing latch in order to start the program after the initialisation phase is done.
	 */
	public Intelligence(String name, HashMap<Integer, Queue<MissionInfo>> MissionMap,CountDownLatch latch) {
		super(name);
		this.MissionMap = MissionMap;
		this.latch=latch;
	}

	@Override
	protected void initialize() {

		subscribeBroadcast(TickBroadcast.class, c -> {
			if (c!=null){
				int tick = c.getTick();
				if (tick!=-1&&MissionMap.containsKey(tick)){
					for (int i = 0; i <MissionMap.get(tick).size() ; i++) {
						MissionInfo toSend = MissionMap.get(tick).poll();
						try {
							getSimplePublisher().sendEvent(new MissionReceviedEvent(toSend.getName(),toSend));
						}catch (NullPointerException e){
							System.out.println(e.getMessage());
						}
					}
				}
			}
		});
		// for counting down to start timeServices
		latch.countDown();
	}
}
