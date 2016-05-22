package ru.mnv.rvlt.core.model;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.math.BigDecimal;

class Operation {
    private final BigDecimal amount;
    private final DateTime timeStamp;
    private final RequestInfo requestInfo;

    public Operation(BigDecimal amount, DateTime timeStamp, RequestInfo requestInfo) {
        this.amount = amount;
        this.timeStamp = timeStamp;
        this.requestInfo = requestInfo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "rqst: " + requestInfo.getRequestId() + " at " + timeStamp + ": " + amount + " " + requestInfo.getComment();
    }

    public static Operation newOperation(BigDecimal amount, RequestInfo requestInfo) {
        return new Operation(amount, DateTime.now(DateTimeZone.UTC), requestInfo);
    }
}
