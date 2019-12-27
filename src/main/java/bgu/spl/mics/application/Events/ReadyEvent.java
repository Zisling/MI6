package bgu.spl.mics.application.Events;

import bgu.spl.mics.Event;

import java.util.List;


/**
 * ReadyEvent-Event that specifies Sending Agents on a mission , sent by M to Money penny through the MessageBroker to start a Mission due to Readiness of Agents.
 */

public class ReadyEvent implements Event<List<String>> {
    private  int duration;
    private List<String> serialAgentsNumbers;

    /**
     * ReadyEvent's Constructor
     * @param duration-sets the duration of the Mission.
     * @param serialAgentsNumbers-sets the serialAgentsNumber of required agents for a Mission.
     */
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

