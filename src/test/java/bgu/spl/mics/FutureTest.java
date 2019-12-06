package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class FutureTest {
    private Future<Integer> toTest;
    @BeforeEach
    public void setUp(){
        toTest=new Future<>();
    }

    @Test
    public void test(){
        //TODO: change this test and add more tests :)
        fail("Not a good test");
    }

    @Test
    void get() {
        assertNull(toTest.get());
        toTest.resolve(2);
        assertNotNull(toTest.get());
    }

    @Test
    void resolve() {
        assertNotNull(toTest.get());
        toTest.resolve(2);
        assertEquals(2, (int) toTest.get());
    }

    @Test
    void isDone() {
        assertFalse(toTest.isDone());
        toTest.resolve(2);
        assertTrue(toTest.isDone());

    }

    @Test
    void testGet() {
        assertNull(toTest.get(5000, TimeUnit.MILLISECONDS));
        toTest.resolve(2);
        assertNotNull(toTest.get(5000, TimeUnit.MILLISECONDS));
    }

}
