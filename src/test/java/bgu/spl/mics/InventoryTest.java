package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Inventory;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryTest {
    Inventory toTest;
    String[] gadgetTest1 = {"cow", "bow", "broadsword", "vodka", "hola"};
    String[] gadgetTest2 = {"rat", "lama", "sword", "pina colda", "octupus"};

    @BeforeEach
    public void setUp() {
        toTest = Inventory.getInstance();
    }

    @Test
    public void test() {
//        Test to check if the instance is a Singleton
        Inventory toTest2 = Inventory.getInstance();
        toTest2.load(gadgetTest1);
        assertEquals(toTest, toTest2);
        for (String s : gadgetTest1) {
            assertTrue(toTest.getItem(s));
            assertFalse(toTest2.getItem(s));
        }
    }

    @Test
    void getInstance() {
        assertEquals(toTest, Inventory.getInstance());
    }

    @Test
    void load() {
        toTest.load(gadgetTest1);
        for (String s : gadgetTest1) {
            assertTrue(toTest.getItem(s));
        }

    }

    @Test
    void getItem() {
        toTest.load(gadgetTest2);
        for (String s : gadgetTest2) {
            assertTrue(toTest.getItem(s));
            assertFalse(toTest.getItem(s));
        }
        for (String s : gadgetTest1) {
            assertFalse(toTest.getItem(s));
        }
    }


    @Test
    void printToFile() throws IOException {
        toTest.printToFile("test.json");
        Gson gson = new Gson();
        JsonReader read = new JsonReader(new FileReader("test.json"));
        String[] Injson = gson.fromJson(read, String[].class);
        checkIfIn(Injson,gadgetTest1);
        checkIfIn(gadgetTest1,Injson);

        }

    private void checkIfIn(String[] in,String[] out){
        for (String s : in) {
            boolean stop = false;
            for (int i = 0; i < out.length && !stop; i++) {
                String s1 = out[i];
                if (s.equals(s1)) {
                    stop = true;
                }
            }
            if (!stop) {
                fail(s + " is not present in the json");
            }
        }
    }
}
