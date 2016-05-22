package ru.mnv.rvlt.core.model;

import java.util.Objects;

public class AccountNumber {
    private final String accNumber;

    private AccountNumber(String accNumber) {
        this.accNumber = accNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountNumber that = (AccountNumber) o;

        return accNumber.equals(that.accNumber);

    }

    @Override
    public int hashCode() {
        return accNumber.hashCode();
    }

    @Override
    public String toString() {
        return accNumber;
    }

    public static AccountNumber fromString(String number) {
        Objects.requireNonNull(number, "Account number must be set");
        if (!number.matches("[0-9]+"))
            throw new AccountingException("Invalid account number format: " + number, ResultStatus.ILLEGAL_ACCOUNT_NUMBER);
        return new AccountNumber(number);
    }
}
