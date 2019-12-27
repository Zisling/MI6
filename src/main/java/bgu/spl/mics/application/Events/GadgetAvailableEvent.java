package bgu.spl.mics.application.Events;

import bgu.spl.mics.Event;

/**
 * Gadget
 */
public class GadgetAvailableEvent implements Event<Integer> {
    private String gadget;
    public GadgetAvailableEvent(String gadget){
        this.gadget=gadget;
    }

    public String getGadget() {
        return gadget;
    }
}
