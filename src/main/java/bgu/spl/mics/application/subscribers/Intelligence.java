package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.Broadcasts.Terminating;
import bgu.spl.mics.application.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.Events.MissionReceviedEvent;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

import java.util.HashMap;

/**
 * A Publisher\Subscriber.
 * Holds a list of Info objects and sends them
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence extends Subscriber {
	HashMap<Integer, MissionInfo> MissionMap;
	int countTear;


	public Intelligence() {
		super("default");
	}

	public Intelligence(String name, HashMap<Integer, MissionInfo> MissionMap) {
		super(name);
		this.MissionMap = MissionMap;
		countTear=MissionMap.keySet().size();
	}

	@Override
	protected void initialize() {

		subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
			@Override
			public void call(TickBroadcast c) {
				Future<?> a= null;
				if (c!=null){
					if (MissionMap.containsKey(c.getTimeTick())){
						 a=getSimplePublisher().sendEvent(new MissionReceviedEvent(MissionMap.get(c.getTimeTick()).getMissionName(),MissionMap.get(c.getTimeTick())));
						 countTear--;
						 if (countTear==0){terminate();}
					}
				}
			}
		});
	}
}
