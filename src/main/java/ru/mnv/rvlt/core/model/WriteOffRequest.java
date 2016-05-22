package ru.mnv.rvlt.core.model;

import java.math.BigDecimal;

final class WriteOffRequest extends AbstractRequest {

    public WriteOffRequest(Account account, BigDecimal amount, RequestInfo requestInfo) {
        super(account, amount, requestInfo);
    }

    @Override
    protected Result doProcess(DailyBalance balance, BigDecimal amount, RequestInfo requestInfo) {
        return balance.writeOff(amount, requestInfo);
    }
}
