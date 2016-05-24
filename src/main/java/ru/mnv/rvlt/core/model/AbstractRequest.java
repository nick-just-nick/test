package ru.mnv.rvlt.core.model;

import java.math.BigDecimal;

abstract class AbstractRequest implements Request {
    private final Account account;
    private final BigDecimal amount;
    private final RequestInfo requestInfo;

    public AbstractRequest(Account account, BigDecimal amount, RequestInfo requestInfo) {
        this.account = account;
        this.amount = amount;
        this.requestInfo = requestInfo;
    }

    protected abstract Result doProcess(DailyBalance balance, BigDecimal amount, RequestInfo requestInfo);

    @Override
    public final Result process() {
        return account.handleRequest(b -> doProcess(b, amount, requestInfo));
    }

    @Override
    public Long getId() {
        return requestInfo.getRequestId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractRequest that = (AbstractRequest) o;

        if (!account.equals(that.account)) return false;
        if (!requestInfo.equals(that.requestInfo)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return account.hashCode();
    }
}
