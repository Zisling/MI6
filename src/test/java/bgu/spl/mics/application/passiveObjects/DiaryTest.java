package bgu.spl.mics.application.passiveObjects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiaryTest {


    Diary toTest;
    List<String> agentsSerial;
    List<String> agentsSerial2;
    List<String> agentsName;
    List<String> agentsName2;
    Report r1;
    Report r2;
    @BeforeEach
    void setUp() {
        toTest=Diary.getInstance();
        agentsSerial= new LinkedList<>();
        agentsSerial2= new LinkedList<>();
        agentsName= new LinkedList<>();
        agentsName2= new LinkedList<>();
        agentsSerial.add("007");
        agentsSerial2.add("008");
        agentsName.add("James Bond");
        agentsName2.add("Bond");
//        r1=new Report("addME1",5,5,agentsSerial,agentsName,"gad",5,6,7);
//        r2=new Report("addME2",5,5,agentsSerial2,agentsName2,"gadgad",5,6,7);
    }

    @Test
    void getInstance() {
        toTest=Diary.getInstance();
    }

    @Test
    void addReport() {
        Thread t1 = new Thread(){
            public void run(){
                toTest.addReport(r1);
            }
        };
        Thread t2 = new Thread(){
            public void run(){
                toTest.addReport(r2);
            }
        };
        t1.start();
        t2.start();
        toTest.addReport(r2);
    }

    @Test
    void getReport() {
        assertEquals("Bond", toTest.getReports().get(0).getAgentsNames().get(0));
        assertEquals("James Bond", toTest.getReports().get(1).getAgentsNames().get(0));
        assertEquals("Bond", toTest.getReports().get(2).getAgentsNames().get(0));
    }

    @Test
    void printToFile() {
        Thread t1 = new Thread(){
            public void run(){
                toTest.addReport(r1);
            }
        };
        Thread t2 = new Thread(){
            public void run(){
                toTest.addReport(r2);
            }
        };
        t1.start();
        t2.start();
        toTest.addReport(r2);
        toTest.printToFile("test_printToFile_Diary");
    }
}