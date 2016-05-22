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
    public AccountNumber getAccountNumber() {
        return account.getAccNumber();
    }

    @Override
    public Long getId() {
        return requestInfo.getRequestId();
    }
}
