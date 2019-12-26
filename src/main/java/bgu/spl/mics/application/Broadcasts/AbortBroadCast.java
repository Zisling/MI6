package bgu.spl.mics.application.Broadcasts;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Event;

import java.util.List;

public class AbortBroadCast implements Event<Boolean> {
    List<String > agentToRelease;

    public AbortBroadCast(List<String> agentToRelease) {
        this.agentToRelease = agentToRelease;
    }

    public List<String> getAgentToRelease() {
        return agentToRelease;
    }
}
