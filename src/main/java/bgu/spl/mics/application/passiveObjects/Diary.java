package bgu.spl.mics.application.passiveObjects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the diary where all reports are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Diary {
	private final List<Report> reports;
	private AtomicInteger total;

	private Diary() {
		reports= new LinkedList<>();
		total= new AtomicInteger(0);
	}

    /**
     * Holder class of the singleton instance of Diary
     */
	private static class DiaryHolder
	{
		private static final Diary instance=new Diary();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static Diary getInstance() {
		return DiaryHolder.instance;
	}

	/**
	 * Retrieves the reports List of this Diary
	 * @return reports- list of reports of Diary singleton
	 */
	public List<Report> getReports() {
		return reports;
	}

	/**
	 * adds a report to the diary
	 * @param reportToAdd - the report to add
	 */
	public void addReport(Report reportToAdd){
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
		if (filename.length()-5>0&&filename.substring(filename.length()-5).equals(".json")){filename=filename.substring(0,filename.length()-5);}
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


	/**
	 * Increments the total number of received missions by 1
	 */
	public void incrementTotal(){
		int val;
		do {
			val = total.get();
		}while (!total.compareAndSet(val,val+1));
	}
}
