package bgu.spl.mics.application;

import bgu.spl.mics.MessageBroker;
import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.publishers.TimeService;
import bgu.spl.mics.application.subscribers.Intelligence;
import bgu.spl.mics.application.subscribers.M;
import bgu.spl.mics.application.subscribers.Moneypenny;
import bgu.spl.mics.application.subscribers.Q;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner {
    public static void main(String[] args) {
        Inventory myInventory = Inventory.getInstance();
        Squad mySquad = Squad.getInstance();
        MessageBroker myBroker = MessageBrokerImpl.getInstance();
        Diary mydiary = Diary.getInstance();
        Gson gson = new Gson();
        Thread[] oof=null;

        try {
            JsonReader read = new JsonReader(new FileReader(args[0]));
            JsonObject inputJson = gson.fromJson(read, JsonObject.class);
            String [] inventoryGadget =gson.fromJson(inputJson.get("inventory"), String[].class);
            myInventory.load(inventoryGadget);
            Agent[] squad = gson.fromJson(inputJson.get("squad"), Agent[].class);
            mySquad.load(squad);
            JsonObject services = gson.fromJson(inputJson.get("services"), JsonObject.class);
            TimeService myTimeService = new TimeService(services.get("time").getAsInt()); //todo replace name
            Q myQ = new Q("nanoMachineSon"); //todo replace name
            int numberOfM = services.get("M").getAsInt();
            int numberOfMoneypenny = services.get("Moneypenny").getAsInt();
            M[] MArr = new M[numberOfM];
            Moneypenny[] MoneypennyArr = new Moneypenny[numberOfMoneypenny];
            for (int i = 0; i < numberOfM; i++) {
                MArr[i]= new M(Integer.toString(i));
            }
            for (int i = 0; i < numberOfMoneypenny; i++) {
                MoneypennyArr[i]= new Moneypenny(Integer.toString(i));
            }
            JsonArray myIntelligence = gson.fromJson(services.get("intelligence"), JsonArray.class);
            Intelligence[] intelligenceArr= new Intelligence[myIntelligence.size()];
            for (int i = 0; i < myIntelligence.size(); i++) {
                JsonObject m = gson.fromJson(myIntelligence.get(i), JsonObject.class);
                MissionInfo[] missions = gson.fromJson(m.get("missions"), MissionInfo[].class);
                HashMap<Integer , Queue<MissionInfo>> map = new HashMap<>();
                for (MissionInfo mission : missions) {
                    if (!map.containsKey(mission.getTimeIssued())){map.put(mission.getTimeIssued(),new LinkedList<>());}
                    map.get(mission.getTimeIssued()).add(mission);
                }
                intelligenceArr[i] = new Intelligence(Integer.toString(i),map);
            }
            int numbersOfSubPub =numberOfM+numberOfMoneypenny+myIntelligence.size()+2;
            oof = new Thread[numbersOfSubPub];
            for (int i = 0; i < numberOfM; i++) {
                oof[i]=new Thread(MArr[i]);
            }
            for (int i = numberOfM; i <numberOfM+numberOfMoneypenny ; i++) {
                oof[i]= new Thread(MoneypennyArr[i-numberOfM]);
            }
            for (int i = numberOfM+numberOfMoneypenny; i <numberOfM+numberOfMoneypenny+myIntelligence.size() ; i++) {
                oof[i]= new Thread(intelligenceArr[i-numberOfM-numberOfMoneypenny]);
            }
            oof[oof.length-1]=new Thread(myTimeService);
            oof[oof.length-2]= new Thread(myQ);

            for (Thread anOof : oof) {
                anOof.start();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        todo: find sol for the waiting problem here
        try {
            for (Thread thread : oof) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        myInventory.printToFile(args[1]);
        Diary.getInstance().printToFile(args[2]);
    }
}
