package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
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
    Agent[] agentsTest1={new Agent("007","James Bond"),new Agent("006","Ali Cohen")
            ,new Agent("001","GlaDos"),new Agent("0055","Pikachu")
            ,new Agent("0099","Jake"),new Agent("0047", "agent 47")};
    Agent[] agentsTest2={new Agent("00123","Sam Fisher"),new Agent("0069", "Solid Snake")};
    @BeforeEach
    public void setUp(){
        toTest=Squad.getInstance();
        for (Agent agent : agentsTest1) {
            serials1.add(agent.getSerialNumber());
            serials2.add(agent.getSerialNumber());
            namesList1.add(agent.getName());
            namesList2.add(agent.getName());
        }
    }

    @Test
    public void test(){
        Squad toTest2=Squad.getInstance();
        toTest2.load(agentsTest1);
        assertEquals(toTest,toTest2);

    }

    @Test
    void getInstance() {
        assertEquals(toTest, Squad.getInstance());
    }

    @Test
    void load() {
            toTest.load(agentsTest1);
        assertTrue(toTest.getAgents(serials1));
        List<String> names = toTest.getAgentsNames(serials1);
        for (String name : names) {
                assertTrue(namesList1.contains(name));
//                remove name to test if there any duplicate in List names
            namesList1.remove(name);
        }
    }

    @Test
    void releaseAgents() {
//        two agent go to mission
        toTest.load(agentsTest2);
        toTest.getAgents(serials2);
//        check if in mission
        assertFalse(agentsTest2[0].isAvailable());
        assertFalse(agentsTest2[1].isAvailable());
        toTest.releaseAgents(serials2);
//        check if there release
        assertTrue(agentsTest2[0].isAvailable());
        assertTrue(agentsTest2[1].isAvailable());
        serials2.remove("0069");
        toTest.getAgents(serials2);
//        check if Sam Fisher is in mission
        assertFalse(agentsTest2[0].isAvailable());
        assertTrue(agentsTest2[1].isAvailable());
        toTest.releaseAgents(serials2);
        assertTrue(agentsTest2[0].isAvailable());
        serials2.add("0069");
        toTest.getAgents(serials2);
        serials2.remove("00123");
        toTest.releaseAgents(serials2);
        //        one is aborted
        assertTrue(agentsTest2[0].isAvailable());
        assertFalse(agentsTest2[1].isAvailable());
    }

    @Test
    void sendAgents() throws InterruptedException {
        toTest.load(agentsTest2);
        assertTrue(agentsTest2[0].isAvailable());
        long testTime = System.currentTimeMillis();
        toTest.sendAgents(serials2, 100);
//        assert the different above 10 millis sec
        assertEquals(10, (System.currentTimeMillis()-testTime)/10);
        assertTrue(agentsTest2[0].isAvailable());
    }

    @Test
    void getAgents() {
        toTest.load(agentsTest2);
        assertTrue( toTest.getAgents(serials2));
        assertTrue( toTest.getAgents(serials2));
        assertFalse(toTest.getAgents(serials1));
        List<String > temp = Arrays.asList("0069", "0055");
        assertFalse(toTest.getAgents(temp));
    }

    @Test
    void getAgentsNames() {
        toTest.load(agentsTest1);
        List<String > names = toTest.getAgentsNames(serials1);
        for (String s : namesList1) {
            assertTrue(names.contains(s));
        }
        assertFalse(names.contains("Sam Fisher"));
        assertFalse(names.contains("Sterling Malory Archer"));
    }
}
