package bgu.spl.mics;

import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.subscribers.ExampleBroadcastSubscriber;
import bgu.spl.mics.example.subscribers.ExampleEventHandlerSubscriber;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class MessageBrokerTest {
    MessageBroker toTest;
    Subscriber testSub1;
    Subscriber testSub2;
    Event<String> testEvent;

    @BeforeEach
    public void setUp(){
        toTest = MessageBrokerImpl.getInstance();
        testSub1 =new ExampleBroadcastSubscriber("Agent 47", new String[]{"1"});
        testSub2= new ExampleEventHandlerSubscriber("diana", new String[] {"2"});
        testEvent = new ExampleEvent("Agent 47");
    }

    @AfterEach
    public void tearDown(){
        toTest=null;
        testSub1=null;
    }

    @Test
    public void test(){
        //TODO: change this test and add more tests :)
        fail("Not a good test");
    }

    @Test
    void getInstance() {
        try {
            assertNotNull(MessageBrokerImpl.getInstance());
        }
        catch (Exception e){fail("Unexpeted Exception "+e.getMessage());}
    }

    @Test
    void register_BroadcastSubscriber() {
        try {
            toTest.register(testSub1);
        }catch (Exception e){fail("unexcited EXCEPTION "+e.getMessage());}
    }
    @Test
    void register_EventHandlerSubscriber() {
        try {
            toTest.register(testSub2);
        }catch (Exception e){fail("unexcited EXCEPTION "+e.getMessage());}
    }

    @Test
    void subscribeEvent() {
        try {
            toTest.subscribeEvent(ExampleEvent.class,testSub2);
        }catch (Exception e){fail("unexcited EXCEPTION "+e.getMessage());}
    }

    @Test
    void subscribeBroadcast() {
    }

    @Test
    void sendBroadcast() {
        try {
            toTest.sendBroadcast(new ExampleBroadcast("Agent 47"));
        }catch (Exception e){fail("unexcited EXCEPTION "+e.getMessage());}
    }

    @Test
    void complete() {
    }

    @Test
    void sendEvent() {
    }

    @Test
    void unregister() {
    }

    @Test
    void awaitMessage() {
    }
}
