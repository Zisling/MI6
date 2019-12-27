package bgu.spl.mics.application.Events;

import bgu.spl.mics.Event;

/**
 * Event that checks if a Gadget is available sent by M to Q through the Message Broker
 */
public class GadgetAvailableEvent implements Event<Integer> {
    private String gadget;

    /**
     * Constructor of GadgetAvailableEvent
     * @param gadget-sets the required gadget for the mission by the provided gadget.
     */
    public GadgetAvailableEvent(String gadget){
        this.gadget=gadget;
    }

    /**
     * Returns the gadget required for the execution of the Mission.
     * @return gadget-Gadget required for the mission.
     */
    public String getGadget() {
        return gadget;
    }
}
