package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.Broadcasts.AbortBroadCast;
import bgu.spl.mics.application.Broadcasts.Terminating;
import bgu.spl.mics.application.Events.AgentAvailableEvent;
import bgu.spl.mics.application.Events.ReadyEvent;
import bgu.spl.mics.application.passiveObjects.Squad;

/**
 * Only this type of Subscriber can access the squad.
 * Three are several Moneypenny-instances - each of them holds a unique serial number that will later be printed on the report.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Moneypenny extends Subscriber {
	int id;
	Squad mySquad;
	public Moneypenny(String name) {
		super(name);
		id=Integer.parseInt(name);
		mySquad=Squad.getInstance();

	}

	@Override
	protected void initialize() {
		subscribeEvent(ReadyEvent.class, new Callback<ReadyEvent>() {
			@Override
			public void call(ReadyEvent c) {
				if (c!=null){
					System.out.println("Money sent");
					mySquad.sendAgents(c.getSerialAgentsNumbers(), c.getDuration());
					System.out.println("Money after sent");
					complete(c, mySquad.getAgentsNames(c.getSerialAgentsNumbers()));
				}
				complete(c, null);
			}
		});
		subscribeEvent(AgentAvailableEvent.class, new Callback<AgentAvailableEvent>() {
			@Override
			public void call(AgentAvailableEvent c) {
				if (c!=null){
					if (mySquad.getAgents(c.getSerialAgentsNumbers())){
						complete(c,id);
					}
					else {
						complete(c, -1);
					}
				}
			}
		});

		subscribeEvent(AbortBroadCast.class, new Callback<AbortBroadCast>() {
			@Override
			public void call(AbortBroadCast c) {
				if (c!=null){
					mySquad.releaseAgents(c.getAgentToRelease());
					complete(c, true);
				}
				complete(c, false);
			}
		});
	}

}
