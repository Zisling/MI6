package bgu.spl.mics.application.publishers;

import bgu.spl.mics.Publisher;
import bgu.spl.mics.application.Broadcasts.Terminating;
import bgu.spl.mics.application.Broadcasts.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;

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

	private int time;
	private int speed=100;
	private int timeTick;
	private Timer clock;
	public TimeService() {
		super("Clock");
		timeTick =0;
		time = 1000;
	}

	public TimeService(int time) {
		super("Clock");
		timeTick =0;
		this.time = time;
	}

	@Override
	protected void initialize() {
		clock = new Timer();
		clock.schedule(new TimerTask() {
			@Override
			public void run() {
				timeTick++;
				if (timeTick <time){
					getSimplePublisher().sendBroadcast(new TickBroadcast(timeTick));
				}
				else {
					getSimplePublisher().sendBroadcast(new Terminating());
					clock.cancel();
				}
			}
		}, 0, 100);

	}

	@Override
	public void run() {
		initialize();
	}

}
