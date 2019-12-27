package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.Broadcasts.AbortBroadCast;
import bgu.spl.mics.application.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.Events.AgentAvailableEvent;
import bgu.spl.mics.application.Events.ReadyEvent;
import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Squad;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Only this type of Subscriber can access the squad.
 * Three are several Moneypenny-instances - each of them holds a unique serial number that will later be printed on the report.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Moneypenny extends Subscriber {
	private int id;
	private Squad mySquad;
	private CountDownLatch latch;
	public Moneypenny(String name,CountDownLatch latch) {
		super(name);
		id=Integer.parseInt(name);
		mySquad=Squad.getInstance();
		this.latch=latch;

	}

	@Override
	protected void initialize() {
//		Monneypenny for Getting Agents for missions
		if(id%2==1){
		subscribeEvent(AgentAvailableEvent.class, c -> {
			if (c!=null){
				if (mySquad.getAgents(c.getSerialAgentsNumbers())){
					complete(c,id);
				}
				else {
					complete(c, -1);
				}
			}
		});

		}
//		different type of Monneypenny to releaseAgents
		if (id%2==0){
//			one Monneypenny to close the program and release all the agent when the programs is terminated
			if (id==0){
			subscribeBroadcast(TickBroadcast.class, c -> {
				if (c.getTick()==-1){
					Map<String, Agent> goHome=mySquad.getAgentsMap();
					for (Agent value : goHome.values()) {
						value.release();
					}
				}
			});
			}
			subscribeEvent(AbortBroadCast.class, c -> {
			if (c!=null){
				mySquad.releaseAgents(c.getAgentToRelease());
				complete(c, true);
			}
			complete(c, false);
		});
			subscribeEvent(ReadyEvent.class, c -> {
				if (c!=null){
					mySquad.sendAgents(c.getSerialAgentsNumbers(), c.getDuration());
					complete(c, mySquad.getAgentsNames(c.getSerialAgentsNumbers()));
				}
			});
		}
		latch.countDown();
	}

}
