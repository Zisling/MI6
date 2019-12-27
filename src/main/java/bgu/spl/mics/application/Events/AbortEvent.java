package bgu.spl.mics.application.Events;

import bgu.spl.mics.Event;

import java.util.List;

/**
 * Event that represent Aborting a mission, Sent from M to MoneyPenny through the MessageBroker
 */
public class AbortEvent implements Event<Boolean> {
    private List<String > agentToRelease;

    /**
     * AbortEvent's Constructor
     * @param agentToRelease-sets the agentsToRelease to the agents that are meant to release due to aborting a mission
     *                      from the provided list.
     */
    public AbortEvent(List<String> agentToRelease) {
        this.agentToRelease = agentToRelease;
    }

    /**
     * Returns the agents serial Numbers that are meant to be released due to abortion.
     * @return agentToRelease-returns the agents that are meant to be released.
     */
    public List<String> getAgentToRelease() {
        return agentToRelease;
    }
}
