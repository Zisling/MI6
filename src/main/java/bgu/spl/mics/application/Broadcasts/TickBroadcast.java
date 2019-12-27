package bgu.spl.mics.application.Broadcasts;

import bgu.spl.mics.Broadcast;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Broadcast that represent the program time.
 */
public class TickBroadcast implements Broadcast{
    private AtomicInteger TimeTicker;
    private int Tick;

    /**
     * TickBroadcast's Constructor
     * @param timeTick-Sets TimeTicker of TickBroadcast to Provided timeTick value.
     */
    public TickBroadcast(AtomicInteger timeTick) {
        TimeTicker = timeTick;
        Tick = timeTick.get();
    }

    /**
     *Returns TimerTicker Reference.
     * @return TimeTick-returns the TimeTick
     */
    public AtomicInteger getTimeTick() {
        return TimeTicker;
    }

    /**
     * Returns the current Tick value of TickBroadcast.
     * @return tick-returns the tick of TickBroadcast.
     */
    public int getTick() {
        return Tick;
    }
}
