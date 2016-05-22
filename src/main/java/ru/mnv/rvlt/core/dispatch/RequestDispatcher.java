package ru.mnv.rvlt.core.dispatch;

import ru.mnv.rvlt.core.dispatch.executors.ExecutorDispatcher;
import ru.mnv.rvlt.core.dispatch.executors.HashBoundedExecutorService;
import ru.mnv.rvlt.core.model.Request;
import ru.mnv.rvlt.core.model.Result;

import java.util.function.Consumer;

public class RequestDispatcher {
    private final HashBoundedExecutorService<Task> executors;

    public RequestDispatcher(int threadPoolSize) {
        this.executors = new HashBoundedExecutorService<>(new ExecutorDispatcher<>(threadPoolSize));
    }

    public void dispatch(Request request, Consumer<Result> replyTo) {
        executors.addTask(new Task(request, replyTo));
    }
}
