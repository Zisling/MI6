package bgu.spl.mics.application.Events;

import bgu.spl.mics.Event;

import java.util.List;

public class ReadyEvent implements Event<List<String>> {
    private  int duration;
    private List<String> serialAgentsNumbers;
    public ReadyEvent(int duration, List<String> serialAgentsNumbers) {
        this.duration = duration;
        this.serialAgentsNumbers= serialAgentsNumbers;
    }

    public List<String> getSerialAgentsNumbers() {
        return serialAgentsNumbers;
    }

    public int getDuration() {
        return duration;
    }
}

