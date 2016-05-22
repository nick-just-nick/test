package ru.mnv.rvlt.core.model;

import java.math.BigDecimal;

final class RefillRequest extends AbstractRequest {

    public RefillRequest(Account account, BigDecimal amount, RequestInfo requestInfo) {
        super(account, amount, requestInfo);
    }

    @Override
    protected Result doProcess(DailyBalance balance, BigDecimal amount, RequestInfo requestInfo) {
        return balance.refill(amount, requestInfo);
    }
}
