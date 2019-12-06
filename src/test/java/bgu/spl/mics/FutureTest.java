package bgu.spl.mics;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.rmi.UnexpectedException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

public class FutureTest {
    private Future<Integer> toTest;
    @BeforeEach
    public void setUp(){
        toTest=new Future<>();
    }
    @AfterEach
    public void tearDown(){
        toTest=null;
    }
    @Test
    public void test(){
        //TODO: change this test and add more tests :)
        fail("Not a good test");
    }

    @Test
    void get_resolved() {
        toTest.resolve(2);
        try{
            assertEquals(2,toTest.get());
        }
        catch (Exception e)
        {
            fail("Unexpected exception"+e.getMessage());
        }
    }
    @Test
    void get_unresolved(){
        try {
            toTest.get();
            fail("Should throw Exception");
        }
        catch (IllegalStateException e)
        {
            assertTrue(true);
        }

    }
    @Test
    void resolve() {
        try {
            toTest.resolve(2);
            assertEquals(2, (int) toTest.get());
        }
        catch (Exception e)
        {
            fail("Future not Resolved");
        }
    }

    @Test
    void isDone() {
        assertFalse(toTest.isDone());
        toTest.resolve(2);
        assertTrue(toTest.isDone());
    }

    @Test
    void testGet_work(){
        try {
            toTest.resolve(2);
            assertEquals(2,toTest.get(5000, TimeUnit.MILLISECONDS));
        }
        catch (Exception e){
            fail("Unexpected exception"+e.getMessage());
        }
    }

    @Test
    void testGetTime_fail() {
        try {
            assertNull(toTest.get(5000, TimeUnit.MILLISECONDS));
            assertTimeout(Duration.ofMillis(6000),()->{
                toTest.get(5000,TimeUnit.MILLISECONDS);
            });
            fail("Should throw exception");
        } catch (Exception e){
            assertTrue(true);
        }

    }
    @Test
    void testGetTime_succeed(){
        try{
            toTest.resolve(2);
            assertEquals(toTest.get(5000,TimeUnit.MILLISECONDS),2);
        }
        catch (Exception e)
        {
            fail("Unexpected Exception"+e.getMessage());
        }
    }

}
