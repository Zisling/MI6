package bgu.spl.mics.application.publishers;

import bgu.spl.mics.Publisher;
import bgu.spl.mics.application.Broadcasts.Terminating;
import bgu.spl.mics.application.Broadcasts.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TimeService is the global system timer There is only one instance of this Publisher.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other subscribers about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends Publisher {

	private boolean toStart= true;
	private int time;
	private int speed=100;
	private AtomicInteger timeTick;
	private Timer clock;
	public TimeService() {
		super("Clock");
		timeTick =new AtomicInteger(0);
		time = 1000;
	}

	public TimeService(int time) {
		super("Clock");
		timeTick =new AtomicInteger(0);
		this.time = time;
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
				if (timeTick.get()<time){
					getSimplePublisher().sendBroadcast(new TickBroadcast(timeTick));
				}
				else {
					timeTick.set(-1); //this is for stop any mission that run
					getSimplePublisher().sendBroadcast(new Terminating());
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
