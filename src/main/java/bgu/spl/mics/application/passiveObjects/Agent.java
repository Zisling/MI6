package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Agent {

	private String serialNumber;
	private String name;
	private boolean available_flag;
	private final Object Lock = new Object();


	//Agent Constructor
	public Agent(){
		this.available_flag=true;
	}
	public Agent(String serialNumber,String agentName){
	this.serialNumber =serialNumber;
	this.name =agentName;
	this.available_flag=true;
	}

	/**
	 * Sets the serial number of an agent.
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber =serialNumber;
	}

	/**
     * Retrieves the serial number of an agent.
     * <p>
     * @return The serial number of an agent.
     */
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * Sets the name of the agent.
	 */
	public void setName(String name) {
		this.name =name;
	}

	/**
     * Retrieves the name of the agent.
     * <p>
     * @return the name of the agent.
     */
	public String getName() {
		return name;
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
		synchronized (this){
//			TODO: see this to solve
			while (!isAvailable()){
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		synchronized (Lock){
		if (isAvailable()){
			available_flag=false;
		}
		}
	}

	/**
	 * Releases an agent.
	 */
	public void release(){
			if (!isAvailable()){
			available_flag=true;
			synchronized (this){
			this.notify();
			}
			}
	}
}
