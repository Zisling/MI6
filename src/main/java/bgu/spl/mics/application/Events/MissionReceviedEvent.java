package bgu.spl.mics.application.Events;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

/**
 * Event that specifies a specific Mission, sent by Intelligence to M through the MessageBroker.
 */
public class MissionReceviedEvent implements Event<MissionInfo> {
    String missionName;
    MissionInfo mission;

    /**
     * Constructor of MissionReceviedEvent
     * @param name-sets the name of the mission with provided name.
     * @param info-sets the mission info with the provided info.
     */
    public MissionReceviedEvent(String name,MissionInfo info){
        missionName=name;
        mission=info;
    }

    /**
     * Returns the name of the Mission.
     * @return missionName-returns the name of the Mission.
     */
    public String getMissionName() {
        return missionName;
    }

    /**
     * Returns the information of the Mission.
     * @return mission-returns the mission's info.
     */
    public MissionInfo getMission() {
        return mission;
    }
}
