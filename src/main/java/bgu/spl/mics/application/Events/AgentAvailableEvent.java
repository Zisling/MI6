package bgu.spl.mics.application.Events;

import bgu.spl.mics.Event;

import java.util.List;

public class AgentAvailableEvent implements Event<Boolean> {
    private List<String> serialAgentsNumbers;
    public AgentAvailableEvent(List<String> serialAgentsNumbers){
        this.serialAgentsNumbers= serialAgentsNumbers;
    }

    public List<String> getSerialAgentsNumbers() {
        return serialAgentsNumbers;
    }
}
