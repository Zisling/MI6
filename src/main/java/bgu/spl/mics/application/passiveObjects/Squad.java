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
	private static final Object lock = new Object();
	/**
	 * Retrieves the single instance of this class.
	 */
	//Squad Constructor
	private Squad(){
		agents = new HashMap<>();
	}

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
		synchronized (lock){
		for (String serial : serials) {
			agents.get(serial).release();
			lock.notify();
		}}
	}

	/**
	 * simulates executing a mission by calling sleep.
	 * @param time   time ticks to sleep
	 */
	public void sendAgents(List<String> serials, int time){
		try {
			Thread.sleep(time*100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		synchronized (lock){
		for (String serial : serials) {
			agents.get(serial).release();
			lock.notify();
		}}
		}

	/**
	 * acquires an agent, i.e. holds the agent until the caller is done with it
	 * @param serials   the serial numbers of the agents
	 * @return ‘false’ if an agent of serialNumber ‘serial’ is missing, and ‘true’ otherwise
	 */
	public boolean getAgents(List<String> serials){
		for (String serial : serials) {
			if (!agents.containsKey(serial)){
				return false;
			}
		}
		synchronized (lock){
		for (int i = 0; i < serials.size(); i++) {
			Agent s = agents.get(serials.get(i));
			if (s.isAvailable()){
				s.acquire();
			}else {
				do {
					try {
						lock.wait(100); //100 millsec is one tick
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}while (!s.isAvailable());
				s.acquire();
			}
		}
		}
		return false;
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
