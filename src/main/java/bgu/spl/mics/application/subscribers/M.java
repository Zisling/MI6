package bgu.spl.mics.application.subscribers;

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

	/**
	 * M's Constructor
	 * @param name-number Id of M.
	 * @param latch-initializing latch in order to start the program after the initialisation phase is done.
	 */
	public M(String name,CountDownLatch latch) {
		super(name);
		id = Integer.parseInt(name);
		myDiary = Diary.getInstance();
		this.latch=latch;
	}

	/**
	 * Creates a Report of a specific Mission that is Executed successfully.
	 * @param missionName-Mission name of a Mission that was Executed Successfully.
	 * @param MoneyPenny-Money penny's Id that handled assigning Agents to the Mission.
	 * @param SerialNumber-List of Serial numbers of agents that was sent on this Mission.
	 * @param AgentsNames-List of the Names of agents that was sent on this Mission.
	 * @param gadgetName-The Name of the gadget that the agents used in the Mission.
	 * @param timeIssued-The time of Issuing the Mission to M.
	 * @param QTime-The time that Q has gotten a GadgetAvailableEvent request.
	 * @param duration-The time to Execute the Mission,if passed and Mission is yet to Complete, it is Aborted.
	 * @return out -The Complete report of the Mission with all of the Information stated above.
	 */
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
	 * Adds a completed report to the Diary.
	 * @param toDoc-a Report to add to the Diary.
	 */

	private void docReport(Report toDoc){
		myDiary.addReport(toDoc);
	}

	/**
	 * Sends an Event to abort a Mission
	 * Mission abortion may happen according to the two following reasons:
	 * 1. Start of Program Termination before agents are ready.
	 * 2. The duration+current time is Greater the Mission's TimeExpired.
	 * @param SerialAgentsNumbers-List of Serial numbers of the agents to release due to abortion of a Mission.
	 */

	private void missionAbort(List<String> SerialAgentsNumbers){
		getSimplePublisher().sendEvent(new ReadyEvent(0,SerialAgentsNumbers));
	}

	/**
	* a Check if the Program is in Termination Phase.
	* @return the State of the Program (Terminating or Running).
	 * */
	private boolean timeCheck(){
		return tick.get() != -1;
	}

	/**
	 * Checks if the time of the Mission hasn't expired yet.
	 * @param timeExpired Expiration time of the Mission handled.
	 * @param timeEnd the last tick + duration of the Mission.
	 * @return if Handled Mission can be Executed.
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
				Future<Integer> MoneyPennyId;
				Future<Integer> QTimeTick;
				Future<List<String>> AgentsNames;
				complete(c, null);
				MissionInfo mission = c.getMission();
				if (isTimeExpired(mission.getTimeExpired(),startTick+mission.getDuration()) && timeCheck()) { //see if can M start presses the Mission
					MoneyPennyId = getSimplePublisher().sendEvent(new AgentAvailableEvent(c.getMission().getSerialAgentsNumbers()));
					if (MoneyPennyId != null && MoneyPennyId.get()!=null&&MoneyPennyId.get() != -1 && MoneyPennyId.isDone() && timeCheck()) { // see if the agent are available
						QTimeTick = getSimplePublisher().sendEvent(new GadgetAvailableEvent(c.getMission().getGadget()));
						// see if gadget is available and three is still time to execute the mission
						if (QTimeTick != null && QTimeTick.get() != -1 & isTimeExpired(mission.getTimeExpired(),QTimeTick.get()+mission.getDuration())& timeCheck()) {
							//send the mission and wait to report it
							AgentsNames = getSimplePublisher().sendEvent(new ReadyEvent(mission.getDuration(), mission.getSerialAgentsNumbers()));
							if (AgentsNames != null && AgentsNames.get() != null && AgentsNames.isDone()) {//wait for the report after the mission is done
								docReport(createReport(c.getMissionName(), MoneyPennyId.get(), mission.getSerialAgentsNumbers(), AgentsNames.get(), mission.getGadget(), mission.getTimeIssued(), QTimeTick.get(),mission.getDuration()));
							} else {// in case that the mission is needed to abort send a massage to Abort and the agent are released
								missionAbort(mission.getSerialAgentsNumbers());
							}
						} else {// in case that the mission is needed to abort send a massage to Abort and the agent are released
							missionAbort(mission.getSerialAgentsNumbers());
						}
					} else if (MoneyPennyId != null && MoneyPennyId.get() != -1) {//see if agent were acquired for the mission if yes release them for other Missions
						// in case that the mission is needed to abort send a massage to Abort and the agent are released
						missionAbort(mission.getSerialAgentsNumbers());
					}
				}
		});
// for counting down to start timeServices
		latch.countDown();
	}

}
