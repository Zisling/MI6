package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.Broadcasts.AbortBroadCast;
import bgu.spl.mics.application.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.Events.AgentAvailableEvent;
import bgu.spl.mics.application.Events.GadgetAvailableEvent;
import bgu.spl.mics.application.Events.MissionReceviedEvent;
import bgu.spl.mics.application.Events.ReadyEvent;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Report;

import java.util.LinkedList;
import java.util.List;
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

	public M(String name) {
		super(name);
		id = Integer.parseInt(name);
		myDiary = Diary.getInstance();
	}

	public Report createReport(String missionName,int MoneyPenny,List<String> SerialNumber, List<String> AgentsNames,String gadgetName,int timeIssued,int QTime){
		Report out = new Report();
		out.setMissionName(missionName);
		out.setM(id);
		out.setMoneypenny(MoneyPenny);
		out.setAgentsSerialNumbersNumber(SerialNumber);
		out.setAgentsNames(AgentsNames);
		out.setGadgetName(gadgetName);
		out.setTimeIssued(timeIssued);
		out.setQTime(QTime);
		out.setTimeCreated(tick.get());
		return out;
	}

	public void docReport(Report toDoc){
		myDiary.addReport(toDoc);
	}
	public void missionAbort(List<String> SerialAgentsNumbers){
		getSimplePublisher().sendBroadcast(new AbortBroadCast(SerialAgentsNumbers));
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
		subscribeEvent(MissionReceviedEvent.class, new Callback<MissionReceviedEvent>() {
			@Override
			public void call(MissionReceviedEvent c) {
				System.out.println(c.getMission().getName()+" "+c.getMission().getGadget()+" "+getName());
				myDiary.incrementTotal();
				Future<Integer> MoneyPennyId= null;
				Future<Integer> QTimeTick= null;
				Future<List<String >> AgentsNames= null;
				complete(c, null);
				if (c.getMission().getTimeExpired()>tick.get()){
					MissionInfo mission = c.getMission();
					MoneyPennyId=getSimplePublisher().sendEvent(new AgentAvailableEvent(c.getMission().getSerialAgentsNumbers()));
					if (MoneyPennyId!=null&&MoneyPennyId.get()!=-1&&MoneyPennyId.isDone()&&tick.get()<mission.getTimeExpired()&tick.get()!=-1){
						QTimeTick=getSimplePublisher().sendEvent(new GadgetAvailableEvent(c.getMission().getGadget()));
						if (QTimeTick!=null&&QTimeTick.get()!=-1&tick.get()<mission.getTimeExpired()&tick.get()!=-1){
							AgentsNames=getSimplePublisher().sendEvent(new ReadyEvent(mission.getDuration(),mission.getSerialAgentsNumbers()));
							if (AgentsNames!=null&&AgentsNames.get()!=null&&AgentsNames.isDone()&&tick.get()!=-1){
								System.out.println("look at me "+AgentsNames.get()+" "+tick.get()+ " M"+getName());
								docReport(createReport(c.getMissionName(),MoneyPennyId.get(),mission.getSerialAgentsNumbers(),AgentsNames.get(),mission.getGadget(),mission.getTimeIssued(),QTimeTick.get()));
							}
						}
						else {
							missionAbort(mission.getSerialAgentsNumbers());
						}
					}
					else if (MoneyPennyId!=null&&MoneyPennyId.get()!=-1){
						missionAbort(mission.getSerialAgentsNumbers());
					}
				}
			}
		});

		// TODO Implement this

	}

}
