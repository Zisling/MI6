package bgu.spl.mics.application.Broadcasts;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast{
    private int TimeTick;

    public TickBroadcast(int timeTick) {
        TimeTick = timeTick;
    }

    public int getTimeTick() {
        return TimeTick;
    }
}
