package bgu.spl.mics.application.Broadcasts;

import bgu.spl.mics.Broadcast;

import java.util.concurrent.atomic.AtomicInteger;

public class TickBroadcast implements Broadcast{
    private AtomicInteger TimeTick;

    public TickBroadcast(AtomicInteger timeTick) {
        TimeTick = timeTick;
    }

    public AtomicInteger getTimeTick() {
        return TimeTick;
    }
}
