package bgu.spl.mics.application.Events;

import bgu.spl.mics.Event;

import java.util.List;


/**
 * ReadyEvent-Send agents to the mission
 */

public class ReadyEvent implements Event<List<String>> {
    private  int duration;
    private List<String> serialAgentsNumbers;

    //ReadyEvent's Constructor
    public ReadyEvent(int duration, List<String> serialAgentsNumbers) {
        this.duration = duration;
        this.serialAgentsNumbers= serialAgentsNumbers;
    }

    /**
     * Returns the serial numbers of agents required for the mission.
     * @return serialAgentsNumbers- returns the serial numbers of agents.
     */
    public List<String> getSerialAgentsNumbers() {
        return serialAgentsNumbers;
    }

    /**
     * returns the duration of the mission
     * @return duration-Duration of the mission
     */

    public int getDuration() {
        return duration;
    }
}

