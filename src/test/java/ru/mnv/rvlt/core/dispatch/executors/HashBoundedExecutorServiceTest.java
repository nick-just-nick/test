package ru.mnv.rvlt.core.dispatch.executors;

import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class HashBoundedExecutorServiceTest {
    private HashBoundedExecutorService<? super Runnable> service;

    private static class AffinityTask implements Runnable {
        private Set<String> results;
        private CountDownLatch latch;
        private int party;

        private AffinityTask(Set<String> results, CountDownLatch latch, int party) {
            this.results = results;
            this.latch = latch;
            this.party = party;
        }

        @Override
        public void run() {
            results.add(Thread.currentThread().toString());
            latch.countDown();
        }

        @Override
        public int hashCode() {
            return party;
        }
    }

    private static class OrderTask implements Runnable {
        private List<Integer> result;
        private Optional<CyclicBarrier> barrier;
        private CountDownLatch latch;
        private Integer item;

        private OrderTask(List<Integer> result, CyclicBarrier barrier, CountDownLatch latch, Integer item) {
            this.result = result;
            this.barrier = Optional.of(barrier);
            this.latch = latch;
            this.item = item;
        }

        private OrderTask(List<Integer> result, CountDownLatch latch, Integer item) {
            this.result = result;
            this.barrier = Optional.empty();
            this.latch = latch;
            this.item = item;
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public void run() {
            barrier.ifPresent(b -> {
                try {
                    b.await();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            result.add(item);
            latch.countDown();
        }

        public Integer getItem() {
            return item;
        }
    }

    @Before
    public void setUp() throws Exception {
        service = new HashBoundedExecutorService<>(new ExecutorDispatcher<Runnable>(4) {
            @Override
            public int dispatchTask(Runnable task) {
                return task.hashCode();
            }
        });
    }

    @Test
    public void testThreadAffinity() throws Exception {
        CountDownLatch latch = new CountDownLatch(6);
        Set<String> result1 = new HashSet<>();
        service.addTask(new AffinityTask(result1, latch, 1));
        service.addTask(new AffinityTask(result1, latch, 1));
        service.addTask(new AffinityTask(result1, latch, 1));
        Set<String> result2 = new HashSet<>();
        service.addTask(new AffinityTask(result2, latch, 2));
        service.addTask(new AffinityTask(result2, latch, 2));
        service.addTask(new AffinityTask(result2, latch, 2));
        latch.await();
        assertEquals(1, result1.size());
        assertEquals(1, result2.size());
        assertNotEquals(result1.iterator().next(), result2.iterator().next());
    }

    @Test
    public void testTaskOrder() throws Exception {
        List<Integer> result = new ArrayList<>();
        CyclicBarrier barrier = new CyclicBarrier(2);
        CountDownLatch latch = new CountDownLatch(6);
        Random random = new Random();
        List<OrderTask> tasks = new ArrayList<>();
        tasks.add(new OrderTask(result, barrier, latch, random.nextInt()));
        IntStream.range(1,6).forEach(i -> tasks.add(new OrderTask(result, latch, random.nextInt())));
        List<Integer> source = tasks.stream().map(OrderTask::getItem).collect(Collectors.toList());
        tasks.forEach(service::addTask);
        barrier.await();
        latch.await();
        assertEquals(source, result);
    }
}