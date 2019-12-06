package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SquadTest {
    Squad toTest;
    List<String> serials = new LinkedList<>();
    List<String> namesList = new LinkedList<>();
    Agent[] agentsTest1={new Agent("007","James Bond"),new Agent("006","Ali Cohen")
            ,new Agent("001","GlaDos"),new Agent("0055","Pikachu")
            ,new Agent("0099","Jake"),new Agent("0047", "agent 47")};
    @BeforeEach
    public void setUp(){
        toTest=Squad.getInstance();
        for (Agent agent : agentsTest1) {
            serials.add(agent.getSerialNumber());
            namesList.add(agent.getName());
        }
    }

    @Test
    public void test(){
        //TODO: change this test and add more tests :)
        fail("Not a good test");
    }

    @Test
    void getInstance() {
        assertEquals(toTest, Squad.getInstance());
    }

    @Test
    void load() {
            toTest.load(agentsTest1);
        assertTrue(toTest.getAgents(serials));
        List<String> names = toTest.getAgentsNames(serials);
        for (String name : names) {
            assertTrue(namesList.contains(name));
        }
        }

    @Test
    void releaseAgents() {
    }

    @Test
    void sendAgents() {
    }

    @Test
    void getAgents() {
    }

    @Test
    void getAgentsNames() {
    }
}
