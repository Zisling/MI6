package bgu.spl.mics.application.Broadcasts;

import bgu.spl.mics.Broadcast;

import java.util.concurrent.atomic.AtomicInteger;

public class TickBroadcast implements Broadcast{
    private AtomicInteger TimeTick;
    private int Tick;

    public TickBroadcast(AtomicInteger timeTick) {
        TimeTick = timeTick;
        Tick = timeTick.get();
    }

    public AtomicInteger getTimeTick() {
        return TimeTick;
    }

    public int getTick() {
        return Tick;
    }
}
