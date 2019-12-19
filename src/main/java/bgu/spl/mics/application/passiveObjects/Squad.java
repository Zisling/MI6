package bgu.spl.mics.application.passiveObjects;
import java.util.*;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Squad {

	private Map<String, Agent> agents;
	private static Squad instance;
	/**
	 * Retrieves the single instance of this class.
	 */
	//Squad Constructor
	private Squad(){}

	public static Squad getInstance() {
		if(instance==null)
		{
			instance=new Squad();
		}

		return instance;
	}

	/**
	 * Initializes the squad. This method adds all the agents to the squad.
	 * <p>
	 * @param inventory 	Data structure containing all data necessary for initialization
	 * 						of the squad.
	 */
	public void load (Agent[] inventory) {
		if (agents==null){agents=new HashMap<>();}
		for (Agent anAgent : inventory) {
			agents.put(anAgent.getSerialNumber(),anAgent);
		}
	}

	/**
	 * Releases agents.
	 */
	public void releaseAgents(List<String> serials){
		// TODO Implement this
	}

	/**
	 * simulates executing a mission by calling sleep.
	 * @param time   milliseconds to sleep
	 */
	public void sendAgents(List<String> serials, int time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * acquires an agent, i.e. holds the agent until the caller is done with it
	 * @param serials   the serial numbers of the agents
	 * @return ‘false’ if an agent of serialNumber ‘serial’ is missing, and ‘true’ otherwise
	 */
	public boolean getAgents(List<String> serials){
		//while wait for agent to return
//		TODO
		return true;
	}

    /**
     * gets the agents names
     * @param serials the serial numbers of the agents
     * @return a list of the names of the agents with the specified serials.
     */
    public List<String> getAgentsNames(List<String> serials){
    	List<String> to_Ret=new LinkedList<>();
		for (String serial : serials) {
			to_Ret.add(agents.get(serial).getName());
		}
		return to_Ret;
    }

}
