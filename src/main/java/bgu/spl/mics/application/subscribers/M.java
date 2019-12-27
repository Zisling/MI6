package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.Events.AgentAvailableEvent;
import bgu.spl.mics.application.Events.GadgetAvailableEvent;
import bgu.spl.mics.application.Events.MissionReceviedEvent;
import bgu.spl.mics.application.Events.ReadyEvent;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Report;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * M handles ReadyEvent - fills a report and sends agents to mission.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class M extends Subscriber {
	private Diary myDiary;
	private int id;
	private AtomicInteger tick;
	private CountDownLatch latch;

	public M(String name,CountDownLatch latch) {
		super(name);
		id = Integer.parseInt(name);
		myDiary = Diary.getInstance();
		this.latch=latch;
	}
	/**
	 * crate A Report
	 * @param missionName MoneyPenny Id , SerialNumbers of agents, AgentsNames , gadget Name , timeIssued , Qtime , and duration for time crated
	 * @return Report
	 **/
	private Report createReport(String missionName,int MoneyPenny,List<String> SerialNumber, List<String> AgentsNames,String gadgetName,int timeIssued,int QTime,int duration){
		Report out = new Report();
		out.setMissionName(missionName);
		out.setM(id);
		out.setMoneypenny(MoneyPenny);
		out.setAgentsSerialNumbersNumber(SerialNumber);
		out.setAgentsNames(AgentsNames);
		out.setGadgetName(gadgetName);
		out.setTimeIssued(timeIssued);
		out.setQTime(QTime);
		out.setTimeCreated(QTime+duration);
		return out;
	}

	/**
	 * add Report to the doc to myDiary
	 * @param toDoc at myDiary
	 * */
	private void docReport(Report toDoc){
		myDiary.addReport(toDoc);
	}
	/**
	 * send a Event to Abort a Mission
	 * Mission is Aborted can happen in two ways:
	 * 1. start of termination before agents are ready
	 * 2. the duration + current time is more then Mission TimeExpired
	 * @param SerialAgentsNumbers is a List of Agents to release
	* */

	private void missionAbort(List<String> SerialAgentsNumbers){
		getSimplePublisher().sendEvent(new ReadyEvent(0,SerialAgentsNumbers));
	}

	/**
	* check if the program is Terminated
	* @return the state of the Program
	 * */
	private boolean timeCheck(){
		return tick.get() != -1;
	}

	/**
	 * check if there is time for mission to be done
	 * @param timeExpired is the time the mission expired
	 * @param timeEnd the last tick + duration of the mission
	 * @return if mission can be executed
	 * */
	private boolean isTimeExpired(int timeExpired ,int timeEnd){
		return timeEnd<=timeExpired;
	}

	@Override
	protected void initialize() {

		subscribeBroadcast(TickBroadcast.class, c -> {
			if (c!=null){
				tick=c.getTimeTick();
			}
		});
		subscribeEvent(MissionReceviedEvent.class, c -> {
			myDiary.incrementTotal();
			int startTick = tick.get();
				System.out.println(c.getMission().getName() + " " + c.getMission().getGadget() + " " + getName());
				Future<Integer> MoneyPennyId;
				Future<Integer> QTimeTick;
				Future<List<String>> AgentsNames;
				complete(c, null);
				MissionInfo mission = c.getMission();
				if (isTimeExpired(mission.getTimeExpired(),startTick+mission.getDuration()) && timeCheck()) {
					MoneyPennyId = getSimplePublisher().sendEvent(new AgentAvailableEvent(c.getMission().getSerialAgentsNumbers()));
					if (MoneyPennyId != null && MoneyPennyId.get()!=null&&MoneyPennyId.get() != -1 && MoneyPennyId.isDone() && timeCheck()) {
						QTimeTick = getSimplePublisher().sendEvent(new GadgetAvailableEvent(c.getMission().getGadget()));
						if (QTimeTick != null && QTimeTick.get() != -1 & isTimeExpired(mission.getTimeExpired(),QTimeTick.get()+mission.getDuration())& timeCheck()) {
							AgentsNames = getSimplePublisher().sendEvent(new ReadyEvent(mission.getDuration(), mission.getSerialAgentsNumbers()));
							if (AgentsNames != null && AgentsNames.get() != null && AgentsNames.isDone()) {
								System.out.println("look at me " + AgentsNames.get() + " " + tick.get() + " M" + getName());
								docReport(createReport(c.getMissionName(), MoneyPennyId.get(), mission.getSerialAgentsNumbers(), AgentsNames.get(), mission.getGadget(), mission.getTimeIssued(), QTimeTick.get(),mission.getDuration()));
							} else {
								missionAbort(mission.getSerialAgentsNumbers());
							}
						} else {
							missionAbort(mission.getSerialAgentsNumbers());
						}
					} else if (MoneyPennyId != null && MoneyPennyId.get() != -1) {
						missionAbort(mission.getSerialAgentsNumbers());
					}
				}
		});

		latch.countDown();
	}

}
