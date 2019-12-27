package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
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

	public Intelligence() {
		super("default");
	}

	public Intelligence(String name, HashMap<Integer, Queue<MissionInfo>> MissionMap,CountDownLatch latch) {
		super(name);
		this.MissionMap = MissionMap;
		this.latch=latch;
	}

	@Override
	protected void initialize() {

		subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
			@Override
			public void call(TickBroadcast c) {
				Future<?> a= null;
				if (c!=null){
					int tick = c.getTick();
					if (tick!=-1&&MissionMap.containsKey(tick)){
						for (int i = 0; i <MissionMap.get(tick).size() ; i++) {
							MissionInfo toSend = MissionMap.get(tick).poll();
							a=getSimplePublisher().sendEvent(new MissionReceviedEvent(toSend.getName(),toSend));
						}
					}
				}
			}
		});
		latch.countDown();
	}
}
