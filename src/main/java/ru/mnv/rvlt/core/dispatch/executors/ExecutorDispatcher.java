package ru.mnv.rvlt.core.dispatch.executors;

public class ExecutorDispatcher<T> {
    private final int executorsCount;

    public ExecutorDispatcher(int executorsCount) {
        this.executorsCount = executorsCount;
    }

    public int getExecutorsCount() {
        return executorsCount;
    }

    public int dispatchTask(T task) {
        return Math.abs(task.hashCode() % executorsCount);
    }
}
