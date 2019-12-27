package bgu.spl.mics.application.Events;

import bgu.spl.mics.Event;

import java.util.List;

/**
 * Event that checks if the Agents specified are available for a mission,Sent by M to MoneyPenny through Message Broker
 */
public class AgentAvailableEvent implements Event<Integer> {
    private List<String> serialAgentsNumbers;

    /**
     * Constructor of AgentAvailableEvent
     * @param serialAgentsNumbers-sets the field to the serialAgentsNumbers provided.
     */
    public AgentAvailableEvent(List<String> serialAgentsNumbers){
        this.serialAgentsNumbers= serialAgentsNumbers;
    }

    /**
     * Returns the serial numbers of agents required for the mission.
     * @return serialAgentsNumbers-
     */
    public List<String> getSerialAgentsNumbers() {
        return serialAgentsNumbers;
    }
}
