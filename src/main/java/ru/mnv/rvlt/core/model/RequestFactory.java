package ru.mnv.rvlt.core.model;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

public class RequestFactory {
    private final AtomicLong idGen;

    public RequestFactory() {
        idGen = new AtomicLong(0);
    }

    public Request newRefill(Account account, BigDecimal amount) {
        return new RefillRequest(account, amount, new RequestInfo("refill", nextId()));
    }

    public Request newWriteOff(Account account, BigDecimal amount) {
        return new WriteOffRequest(account, amount, new RequestInfo("write-off", nextId()));
    }

    public Request newTransfer(Account from, Account to, BigDecimal amount, String comment) {
        from.checkCorrespondence(to);
        RequestInfo info = new RequestInfo(comment, nextId());
        return new CompoundRequest(new WriteOffRequest(from, amount, info), new RefillRequest(to, amount, info));
    }

    private long nextId() {
        return idGen.incrementAndGet();
    }
}
