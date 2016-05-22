package ru.mnv.rvlt.core.model;

import java.math.BigDecimal;
import java.util.Objects;

public final class Account {
    private final AccountNumber accNumber;
    private final String ccy;
    private final DailyBalance balance;

    public Account(AccountNumber accNumber, String ccy) {
        Objects.requireNonNull(ccy, "Currency must be set");
        this.accNumber = accNumber;
        this.ccy = ccy;
        this.balance = new DailyBalance(new BigDecimal(0));
    }

    public AccountNumber getAccNumber() {
        return accNumber;
    }

    public void checkCorrespondence(Account other){
        if (this.accNumber.equals(other.accNumber))
            throw new AccountingException(
                    String.format("Same account number can not be used: %s, %s", this.accNumber, other.accNumber),
                    ResultStatus.SAME_ACCOUNT);
        if (!this.ccy.equalsIgnoreCase(other.ccy))
            throw new AccountingException(
                    String.format("Accounts with different currency: %s, %s", this.ccy, other.ccy),
                    ResultStatus.NOT_EQUAL_CCY);
    }

    public BigDecimal getNetting() {
        return balance.getNetting();
    }

    public String getAccountStatement() {
        return String.format("%s (%s)\n%s",accNumber, ccy, balance.getOperationsLog());
    }

    Result handleRequest(RequestCallback callback) {
        return callback.execute(balance);
    }
}
