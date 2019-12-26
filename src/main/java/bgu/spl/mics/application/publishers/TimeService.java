package bgu.spl.mics.application.publishers;

import bgu.spl.mics.Publisher;
import bgu.spl.mics.application.Broadcasts.Terminating;
import bgu.spl.mics.application.Broadcasts.TickBroadcast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TimeService is the global system timer There is only one instance of this Publisher.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other subscribers about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends Publisher {

	private boolean toStart= true;
	private int ProgramTime;// The TimeTick that the program is allowed to run up to.
	private int speed=100;
	private AtomicInteger timeTick;
	private Timer clock;

	//Default Constructor of TimeService
	public TimeService() {
		super("Clock");
		timeTick =new AtomicInteger(0);
		ProgramTime = 1000;
	}
	//TimeService's Constructor
	public TimeService(int time) {
		super("Clock");
		timeTick =new AtomicInteger(0);
		this.ProgramTime = time;
	}

	@Override
	protected void initialize() {
		clock = new Timer();
		clock.schedule(new TimerTask() {
			@Override
			public void run() {
				int val;
				do {
					val=timeTick.get();
				}while (!timeTick.compareAndSet(val,val+1));
				System.out.println(timeTick);
				if (timeTick.get()<ProgramTime){
					getSimplePublisher().sendBroadcast(new TickBroadcast(timeTick));
				}
				else {
					timeTick.set(-1); //value=-1 in order to stop a mission the runs
					try {
						Thread.sleep(speed);//one tick to Release all agent
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					getSimplePublisher().sendBroadcast(new Terminating());//Time's up, time to terminate the program.
					clock.cancel();
				}
			}
		}, 0, speed);

	}

	@Override
	public void run() {
		initialize();
	}

}
