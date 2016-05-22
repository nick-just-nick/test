package ru.mnv.rvlt.core.model;

public interface Request {
    Result process();
    AccountNumber getAccountNumber();
    Long getId();
}
