package ru.mnv.rvlt.core.dispatch.executors;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class ExecutorDispatcherTest {
    private ExecutorDispatcher<Object> dispatcher;
    private int executorsCount;

    @Before
    public void setUp() throws Exception {
        executorsCount = 8;
        dispatcher = new ExecutorDispatcher<>(executorsCount);
    }

    @Test
    public void testSameDispatch() throws Exception {
        int hash = new Random().nextInt();
        Object task1 = new Object() {
            @Override
            public int hashCode() {
                return hash;
            }
        };
        Object task2 = new Object() {
            @Override
            public int hashCode() {
                return hash;
            }
        };
        int num1 = dispatcher.dispatchTask(task1);
        int num2 = dispatcher.dispatchTask(task2);
        assertEquals(num1, num2);
    }

    @Test
    public void testDifDispatch() throws Exception {
        int hash1 = new Random().nextInt();
        int hash2 = hash1 + 2;
        Object task1 = new Object() {
            @Override
            public int hashCode() {
                return hash1;
            }
        };
        Object task2 = new Object() {
            @Override
            public int hashCode() {
                return hash2;
            }
        };
        int num1 = dispatcher.dispatchTask(task1);
        int num2 = dispatcher.dispatchTask(task2);
        assertNotEquals(num1, num2);
    }

    @Test
    public void testRangeDispatch() throws Exception {
        int hash1 = new Random().nextInt();
        int hash2 = hash1 + executorsCount;
        Object task1 = new Object() {
            @Override
            public int hashCode() {
                return hash1;
            }
        };
        Object task2 = new Object() {
            @Override
            public int hashCode() {
                return hash2;
            }
        };
        int num1 = dispatcher.dispatchTask(task1);
        int num2 = dispatcher.dispatchTask(task2);
        assertEquals(num1, num2);
    }
}