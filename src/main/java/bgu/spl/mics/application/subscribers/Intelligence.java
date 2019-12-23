package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.Events.MissionReceviedEvent;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

import java.util.HashMap;
import java.util.Queue;

/**
 * A Publisher\Subscriber.
 * Holds a list of Info objects and sends them
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence extends Subscriber {
	HashMap<Integer, Queue<MissionInfo>> MissionMap;


	public Intelligence() {
		super("default");
	}

	public Intelligence(String name, HashMap<Integer, Queue<MissionInfo>> MissionMap) {
		super(name);
		this.MissionMap = MissionMap;
	}

	@Override
	protected void initialize() {

		subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
			@Override
			public void call(TickBroadcast c) {
				Future<?> a= null;
				int tick = c.getTimeTick().get();
				if (c!=null){
					if (MissionMap.containsKey(tick)){
						for (int i = 0; i <MissionMap.get(tick).size() ; i++) {
							MissionInfo toSend = MissionMap.get(tick).poll();
							a=getSimplePublisher().sendEvent(new MissionReceviedEvent(toSend.getName(),toSend));
						}
					}
				}
			}
		});
	}
}
