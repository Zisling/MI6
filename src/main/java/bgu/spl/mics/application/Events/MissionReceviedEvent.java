package bgu.spl.mics.application.Events;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

public class MissionReceviedEvent implements Event<MissionInfo> {
    String missionName;
    MissionInfo mission;
    public MissionReceviedEvent(String name,MissionInfo info){
        missionName=name;
        mission=info;
    }

    public String getMissionName() {
        return missionName;
    }

    public MissionInfo getMission() {
        return mission;
    }
}
