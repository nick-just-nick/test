package ru.mnv.rvlt.core.model;

public class AccountingException extends RuntimeException {
    private ResultStatus status;

    public AccountingException(String message, ResultStatus status) {
        super(message);
        this.status = status;
    }

    public ResultStatus getStatus() {
        return status;
    }
}
