package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SquadTest {
    Squad toTest;
    List<String> serials1 = new LinkedList<>();
    List<String> serials2 = new LinkedList<>();
    List<String> namesList1 = new LinkedList<>();
    List<String> namesList2 = new LinkedList<>();
    Agent[] agentsTest1;
    Agent[] agentsTest2;

    @BeforeEach
    public void setUp(){
        toTest=Squad.getInstance();
        agentsTest1= new Agent[]{new Agent("007", "James Bond"), new Agent("006", "Ali Cohen")
                , new Agent("001", "GlaDos"), new Agent("0055", "Pikachu")
                , new Agent("0099", "Jake"), new Agent("0047", "agent 47")};
        agentsTest2= new Agent[]{new Agent("00123", "Sam Fisher"), new Agent("0069", "Solid Snake")};
        for (Agent agent : agentsTest1) {
            serials1.add(agent.getSerialNumber());
            namesList1.add(agent.getName());
        }
        for (Agent agent : agentsTest2) {
            serials2.add(agent.getSerialNumber());
            namesList2.add(agent.getName());
        }
    }
    @AfterEach
    public void tearDown(){
        Arrays.fill(agentsTest1, null);
        Arrays.fill(agentsTest2, null);
        toTest=null;
        serials1.clear();
        serials2.clear();
        namesList1.clear();
        namesList2.clear();
    }

    @Test
    public void test(){
        Squad toTest2=Squad.getInstance();
        toTest2.load(agentsTest2);
        assertEquals(toTest,toTest2);

    }

    @Test
    void getInstance(){
        try {
            assertNotNull(toTest);
            assertEquals(toTest, Squad.getInstance());
        } catch (Exception e){
            fail("Unexpected Exception " + e.getMessage());
        }
    }

    @Test
    void load() {
        try {
            toTest.load(agentsTest1);
        } catch (Exception e){fail("Unexpected Exception (didn't load)" + e.getMessage());}
    }

    @Test
    void getAgents() {
        toTest.load(agentsTest2);
        List<String > temp = Arrays.asList("0069", "0055");
        try {
            assertTrue( toTest.getAgents(serials2));
            assertFalse(toTest.getAgents(serials1));
            assertFalse(toTest.getAgents(temp));}
        catch (Exception e){fail("Unexpected Exception (didn't get)" + e.getMessage());}
    }

    @Test
    void releaseAgents() {
//        two agent go to mission
        toTest.load(agentsTest2);
        toTest.getAgents(serials2);
//        check if in mission
        assertFalse(agentsTest2[0].isAvailable());
        assertFalse(agentsTest2[1].isAvailable());
        try {toTest.releaseAgents(serials2);
//        check if there release
        assertTrue(agentsTest2[0].isAvailable());
        assertTrue(agentsTest2[1].isAvailable());
        }catch (Exception e){fail("Unexpected Exception (didn't release Agents)" + e.getMessage());}
        serials2.remove("0069");
        toTest.getAgents(serials2);
//        check if Sam Fisher is in mission
        assertFalse(agentsTest2[0].isAvailable());
        assertTrue(agentsTest2[1].isAvailable());
        try {
            toTest.releaseAgents(serials2);
        }catch (Exception e){fail("Unexpected Exception (didn't release Agent Sam Fisher 1)" + e.getMessage());}
        assertTrue(agentsTest2[0].isAvailable());
        serials2.add("0069");
        toTest.getAgents(serials2);
        serials2.remove("00123");
        try{toTest.releaseAgents(serials2);}catch (Exception e){fail("Unexpected Exception (didn't release Agent Solid Snake 2)" + e.getMessage());}
        //        one is aborted
        assertFalse(agentsTest2[0].isAvailable());
        assertTrue(agentsTest2[1].isAvailable());
    }

    @Test
    void sendAgents() {
        toTest.load(agentsTest2);
        assertTimeout(Duration.ofMillis(125), ()->{toTest.sendAgents(serials2, 100);});
        assertTrue(agentsTest2[0].isAvailable());
    }

    @Test
    void getAgentsNames() {
        toTest.load(agentsTest2);
        List<String > names = toTest.getAgentsNames(serials2);
        for (String s : namesList2) {
            assertTrue(names.contains(s));
        }
        assertFalse(names.contains("James Bond"));
        assertFalse(names.contains("Sterling Malory Archer"));
    }
}
