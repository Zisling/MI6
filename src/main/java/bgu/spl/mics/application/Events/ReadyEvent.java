package bgu.spl.mics.application.Events;

import bgu.spl.mics.Event;

import java.util.List;

public class ReadyEvent implements Event<List<String>> {
    int duration;

    public ReadyEvent(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }
}
