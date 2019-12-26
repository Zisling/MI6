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

	//Squad Constructor
	private Squad(){
		agents = new HashMap<>();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
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
		for (String serial : serials) {
			agents.get(serial).release();
	}

	}

	/**
	 * simulates executing a mission by calling sleep.
	 * @param time   time ticks to sleep
	 */
	public void sendAgents(List<String> serials, int time){
		try {
			Thread.sleep(time* 100);// 100 milliseconds
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (String serial : serials) {
			agents.get(serial).release();
		}
		}

	/**
	 * acquires an agent, i.e. holds the agent until the caller is done with it
	 * @param serials   the serial numbers of the agents
	 * @return ‘false’ if an agent of serialNumber ‘serial’ is missing, and ‘true’ otherwise
	 */
	public boolean getAgents(List<String> serials){
		//if a serial number is missing returns false
		for (String serial : serials) {
			if (!agents.containsKey(serial)){
				return false;
			}
		}
		//sorting the list of agents serials to avoid locks while acquiring them
			serials.sort(String::compareTo);

		//acquires the agents from the serials list and returns true
			for (String serial : serials) {
				Agent s = agents.get(serial);
				s.acquire();
			}
		return true;
	}

    /**
     * gets the agents names
     * @param serials the serial numbers of the agents
     * @return a list of the names of the agents with the specified serials.
     */
    public List<String> getAgentsNames(List<String> serials){
    	List<String> toRet=new LinkedList<>();
		for (String serial : serials) {
			toRet.add(agents.get(serial).getName());
		}
		return toRet;
    }

}
