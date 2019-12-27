package bgu.spl.mics.application.passiveObjects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *  That's where Q holds his gadget (e.g. an explosive pen was used in GoldenEye, a geiger counter in Dr. No, etc).
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {
	private List<String> gadgets;
	private static final Object lock = new Object();

	//Inventory's default constructor
	private Inventory(){}

	/**
	 * Holder class of the singleton instance of Inventory
	 */
	private static class InventoryHolder{
		private static final Inventory instance=new Inventory();
	}
	/**
     * Retrieves the single instance of this class.
     */
	public static Inventory getInstance() {
		return InventoryHolder.instance;
	}


	/**
     * Initializes the inventory. This method adds all the items given to the gadget
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (String[] inventory) {
			if(gadgets==null)
			{
				gadgets=new LinkedList<>();
			}
			gadgets.addAll(Arrays.asList(inventory));
	}
	
	/**
     * acquires a gadget and returns 'true' if it exists.
     * <p>
     * @param gadget 		Name of the gadget to check if available
     * @return 	‘false’ if the gadget is missing, and ‘true’ otherwise
     */
	public boolean getItem(String gadget) {
		synchronized (lock) {
			return gadgets.remove(gadget);
		}
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object List<Gadget> which is a
	 * List of all the reports in the diary.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printToFile(String filename){
		if (filename.length()-5>0&&filename.substring(filename.length()-5).equals(".json")){filename=filename.substring(0,filename.length()-5);}
		try (Writer writer = new FileWriter(filename+".json")) {
			Gson gson = new GsonBuilder().create();
			gson.toJson(gadgets, writer);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}
