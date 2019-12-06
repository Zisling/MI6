package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Agent {


	private String Serial_Number;
	private String agent_Name;
	private boolean available_flag;

	//Agent Constructor
	public Agent(String serialNumber,String agentName){
	this.Serial_Number=serialNumber;
	this.agent_Name=agentName;
	this.available_flag=true;
	}

	/**
	 * Sets the serial number of an agent.
	 */
	public void setSerialNumber(String serialNumber) {
		this.Serial_Number=serialNumber;
	}

	/**
     * Retrieves the serial number of an agent.
     * <p>
     * @return The serial number of an agent.
     */
	public String getSerialNumber() {
		return Serial_Number;
	}

	/**
	 * Sets the name of the agent.
	 */
	public void setName(String name) {
		this.agent_Name=name;
	}

	/**
     * Retrieves the name of the agent.
     * <p>
     * @return the name of the agent.
     */
	public String getName() {
		return agent_Name;
	}

	/**
     * Retrieves if the agent is available.
     * <p>
     * @return if the agent is available.
     */
	public boolean isAvailable() {
		return available_flag;
	}

	/**
	 * Acquires an agent.
	 */
	public void acquire(){
		available_flag=false;
	}

	/**
	 * Releases an agent.
	 */
	public void release(){
		available_flag=true;
	}
}
