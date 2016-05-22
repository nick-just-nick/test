package ru.mnv.rvlt.core.dispatch;

import ru.mnv.rvlt.core.model.AccountingException;
import ru.mnv.rvlt.core.model.Request;
import ru.mnv.rvlt.core.model.Result;
import ru.mnv.rvlt.core.model.ResultStatus;

import java.util.Objects;
import java.util.function.Consumer;

class Task implements Runnable {
    private final Request rqst;
    private final Consumer<Result> replyTo;

    public Task(Request rqst, Consumer<Result> replyTo) {
        Objects.requireNonNull(rqst);
        Objects.requireNonNull(replyTo);
        this.rqst = rqst;
        this.replyTo = replyTo;
    }

    @Override
    public void run() {
        replyTo.accept(safeProcess());
    }

    private Result safeProcess() {
        try {
            return rqst.process();
        } catch (AccountingException e) {
            return Result.fail(rqst.getId(), e.getStatus(), e.getMessage());
        } catch (Exception e) {
            return Result.fail(rqst.getId(), ResultStatus.UNKNOWN, e.getMessage());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        return rqst.equals(task.rqst);

    }

    @Override
    public int hashCode() {
        return rqst.hashCode();
    }
}
