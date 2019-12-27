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
import java.util.concurrent.CountDownLatch;

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
        Thread[] threadsArr=null;

        try {
            JsonReader read = new JsonReader(new FileReader(args[0]));
            JsonObject inputJson = gson.fromJson(read, JsonObject.class);
//            load inventory
            String [] inventoryGadget =gson.fromJson(inputJson.get("inventory"), String[].class);
            myInventory.load(inventoryGadget);
//            load agents
            Agent[] squad = gson.fromJson(inputJson.get("squad"), Agent[].class);
            mySquad.load(squad);
//            go to json services
            JsonObject services = gson.fromJson(inputJson.get("services"), JsonObject.class);
//            crate timeService
            TimeService myTimeService = new TimeService(services.get("time").getAsInt());
//            crate Q
//            crate M's
            int numberOfM = services.get("M").getAsInt();
            int numberOfMoneypenny = services.get("Moneypenny").getAsInt();
            //            crate Intelligence subs
            JsonArray myIntelligence = gson.fromJson(services.get("intelligence"), JsonArray.class);
            Intelligence[] intelligenceArr= new Intelligence[myIntelligence.size()];
            M[] MArr = new M[numberOfM];
            int numbersOfSubPub =numberOfM+numberOfMoneypenny+myIntelligence.size()+2;
            CountDownLatch latch = new CountDownLatch(numbersOfSubPub-1);
            Q myQ = new Q("Q",latch);
            Moneypenny[] MoneypennyArr = new Moneypenny[numberOfMoneypenny];
//            crate M
            for (int i = 0; i < numberOfM; i++) {
                MArr[i]= new M(Integer.toString(i),latch);
            }
//            crate MoneyPenny
            for (int i = 0; i < numberOfMoneypenny; i++) {
                MoneypennyArr[i]= new Moneypenny(Integer.toString(i),latch);
            }

//            crate MissionInfo
            for (int i = 0; i < myIntelligence.size(); i++) {
                JsonObject m = gson.fromJson(myIntelligence.get(i), JsonObject.class);
                MissionInfo[] missions = gson.fromJson(m.get("missions"), MissionInfo[].class);
                HashMap<Integer , Queue<MissionInfo>> map = new HashMap<>();
//                enter the MissionInfo in to map
                for (MissionInfo mission : missions) {
                    if (!map.containsKey(mission.getTimeIssued())){map.put(mission.getTimeIssued(),new LinkedList<>());}
                    map.get(mission.getTimeIssued()).add(mission);
                }
//                put all intelligence in to Arr
                intelligenceArr[i] = new Intelligence(Integer.toString(i),map,latch);
            }

            threadsArr = new Thread[numbersOfSubPub];
            for (int i = 0; i < numberOfM; i++) {
                threadsArr[i]=new Thread(MArr[i]);
            }
            for (int i = numberOfM; i <numberOfM+numberOfMoneypenny ; i++) {
                threadsArr[i]= new Thread(MoneypennyArr[i-numberOfM]);
            }
            for (int i = numberOfM+numberOfMoneypenny; i <numberOfM+numberOfMoneypenny+myIntelligence.size() ; i++) {
                threadsArr[i]= new Thread(intelligenceArr[i-numberOfM-numberOfMoneypenny]);
            }
            threadsArr[threadsArr.length-2]= new Thread(myQ);
            threadsArr[threadsArr.length-1]=new Thread(myTimeService);

            for (int i = 0; i < threadsArr.length-1; i++) {
                threadsArr[i].start();
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            threadsArr[threadsArr.length-1].start();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        todo: find sol for the waiting problem here
        if (threadsArr!=null){
        try {
            for (Thread thread : threadsArr) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        }
        myInventory.printToFile(args[1]);
        Diary.getInstance().printToFile(args[2]);
    }
}
