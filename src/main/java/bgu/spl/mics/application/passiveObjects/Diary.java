package bgu.spl.mics.application.passiveObjects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Passive object representing the diary where all reports are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Diary {
	private static Diary instance;
	private final List<Report> reports;
	private AtomicInteger total;

	private Diary() {
		reports= new LinkedList<Report>();
		total= new AtomicInteger(0);
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static Diary getInstance() {
		if (instance==null){
			synchronized (Diary.class){
			if (instance==null){instance=new Diary();}}}
		return instance;
	}

	public List<Report> getReports() {
		return reports;
	}

	/**
	 * adds a report to the diary
	 * @param reportToAdd - the report to add
	 */
	public void addReport(Report reportToAdd){
		int val;
		do {
			val = total.get();
		}while (!total.compareAndSet(val,val+1));
		synchronized (reports){
		reports.add(reportToAdd);}
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object List<Report> which is a
	 * List of all the reports in the diary.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printToFile(String filename){
			try (Writer writer = new FileWriter(filename+".json")) {
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				gson.toJson(this, writer);
			}catch (IOException e) {
				e.printStackTrace();
			}
	}

	/**
	 * Gets the total number of received missions (executed / aborted) be all the M-instances.
	 * @return the total number of received missions (executed / aborted) be all the M-instances.
	 */
	public int getTotal(){
		return total.get();
	}
}
