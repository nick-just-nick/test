package ru.mnv.rvlt.core.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

class DailyBalance {
    private final BigDecimal balance;
    private final List<Operation> operationLog;

    public DailyBalance(BigDecimal balance) {
        this.balance = balance;
        this.operationLog = new CopyOnWriteArrayList<>();
    }

    public BigDecimal getNetting() {
        return operationLog.stream().map(Operation::getAmount).reduce(balance, BigDecimal::add);
    }

    private Result registerAmount(BigDecimal amount, RequestInfo requestInfo) {
        operationLog.add(Operation.newOperation(amount, requestInfo));
        return Result.success(requestInfo.getRequestId());
    }

    public Result refill(BigDecimal amount, RequestInfo requestInfo) {
        return registerAmount(amount.abs(), requestInfo);
    }

    public Result writeOff(BigDecimal amount, RequestInfo requestInfo) {
        BigDecimal net = getNetting();
        BigDecimal absAmount = amount.abs();
        if (net.compareTo(absAmount) < 0)
            throw new AccountingException("Insufficient funds for write-off: " + net, ResultStatus.INSUFFICIENT_FUNDS);
        else {
            return registerAmount(absAmount.negate(), requestInfo);
        }
    }

    public String getOperationsLog() {
        return operationLog.stream().map(Operation::toString).collect(Collectors.joining("\n"));
    }
}
