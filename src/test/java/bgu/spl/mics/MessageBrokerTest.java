package bgu.spl.mics;

import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.publishers.ExampleMessageSender;
import bgu.spl.mics.example.subscribers.ExampleBroadcastSubscriber;
import bgu.spl.mics.example.subscribers.ExampleEventHandlerSubscriber;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class MessageBrokerTest {
    MessageBroker toTest;
    Subscriber[] subArr = new Subscriber[4];
    boolean[] subIsRegister = new boolean[4];

    @BeforeEach
    public void setUp() {
        toTest = MessageBrokerImpl.getInstance();
        Subscriber testSub1 = new ExampleBroadcastSubscriber("Agent 47", new String[]{"1"});
        Subscriber testSub2 = new ExampleEventHandlerSubscriber("John Bradford", new String[]{"1"});
        Subscriber testSub3 = new ExampleMessageSender("Diana", new String[]{"broadcast"});
        Subscriber testSub4 = new ExampleMessageSender("Revolver Ocelot", new String[]{"event"});
        subArr[0] = testSub1;
        subArr[1] = testSub2;
        subArr[2] = testSub3;
        subArr[3] = testSub4;
    }

    @AfterEach
    public void tearDown() {
        for (int i = 0; i < subIsRegister.length; i++) {
            if (subIsRegister[i]) {
                toTest.unregister(subArr[i]);
            }
        }
        Arrays.fill(subArr, null);
        toTest = null;

    }

    @Test
    public void test() {

        //TODO: change this test and add more tests :)
//        fail("Not a good test");
    }

    @Test
    void getInstance() {
        try {
            assertNotNull(MessageBrokerImpl.getInstance());
        } catch (Exception e) {
            fail("Unexpeted Exception " + e.getMessage());
        }
    }

    @Test
    void register_unregister_BroadcastSubscriber() {
        try {
            toTest.register(subArr[0]);
        } catch (Exception e) {
            fail("didnt register a BroadcastSubscriber unexcited EXCEPTION " + e.getMessage());
        }
        try {
            toTest.unregister(subArr[0]);
        } catch (Exception e) {
            fail("didnt unregister a BroadcastSubscriber unexcited EXCEPTION " + e.getMessage());
        }
    }

    @Test
    void register_unregister_EventHandlerSubscriber() {
        try {
            toTest.register(subArr[1]);
        } catch (Exception e) {
            fail("didnt register a EventHandlerSubscriber unexcited EXCEPTION " + e.getMessage());
        }
        try {
            toTest.unregister(subArr[1]);
        } catch (Exception e) {
            fail("didnt unregister a EventHandlerSubscriber unexcited EXCEPTION " + e.getMessage());
        }
    }

    @Test
    void register_unregister_MessageSender() {
        try {
            toTest.register(subArr[2]);
        } catch (Exception e) {
            fail("didnt register a MessageSender unexcited EXCEPTION " + e.getMessage());
        }
        try {
            toTest.unregister(subArr[2]);
        } catch (Exception e) {
            fail("didnt unregister a MessageSender unexcited EXCEPTION " + e.getMessage());
        }
    }


    @Test
    void testSubscribeEvent() {
        Thread sub = new Thread(subArr[1]);
        sub.start();
        try {
            synchronized (this) {
                this.wait(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ExampleEvent send = new ExampleEvent("X-com");
        toTest.subscribeEvent(ExampleEvent.class, subArr[1]);
        assertNotNull(toTest.sendEvent(send));
        sub.interrupt();
    }

    @Test
    void testSubscribeBrodcast() {
        Thread sub = new Thread(subArr[0]);
        sub.start();
        try {
            synchronized (this) {
                this.wait(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ExampleBroadcast send = new ExampleBroadcast("Hitman");
        toTest.subscribeBroadcast(ExampleBroadcast.class, subArr[0]);
        toTest.sendBroadcast(send);
        sub.interrupt();
    }
}
