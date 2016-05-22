package ru.mnv.rvlt.core.dispatch.executors;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class HashBoundedExecutorService<T extends Runnable> {
    private final ExecutorDispatcher<T> dispatcher;
    private final ExecutorService service;
    private final Map<Integer, BlockingQueue<T>> queues;

    public HashBoundedExecutorService(ExecutorDispatcher<T> dispatcher) {
        this.dispatcher = dispatcher;
        this.queues = new HashMap<>(dispatcher.getExecutorsCount());
        this.service = Executors.newFixedThreadPool(dispatcher.getExecutorsCount());
        for (int i=0; i < dispatcher.getExecutorsCount(); i++) {
            BlockingQueue<T> queue = new LinkedBlockingQueue<>();
            queues.put(i, queue);
            service.submit(() -> executeNext(queue));
        }
    }

    private void executeNext(BlockingQueue<T> queue) {
        try {
            while (!service.isTerminated()) {
                queue.take().run();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void addTask(T task) {
        try {
            queues.get(dispatcher.dispatchTask(task)).put(task);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
