package ru.mnv.rvlt.core;

import ru.mnv.rvlt.core.model.Account;
import ru.mnv.rvlt.core.model.AccountNumber;
import ru.mnv.rvlt.core.model.AccountingException;
import ru.mnv.rvlt.core.model.ResultStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountRepository {
    private final Map<AccountNumber, Account> accounts;

    public AccountRepository() {
        accounts = new ConcurrentHashMap<>();
    }

    public Account get(String number) {
        AccountNumber accountNumber = AccountNumber.fromString(number);
        if (!accounts.containsKey(accountNumber))
            throw new AccountingException("Unknown account: " + number, ResultStatus.UNKNOWN_ACCOUNT);
        return accounts.get(accountNumber);
    }

    public void addNew(String number, String currency) {
        synchronized (accounts) {
            AccountNumber accountNumber = AccountNumber.fromString(number);
            if (accounts.containsKey(accountNumber))
                throw new AccountingException("Account already registered: " + number, ResultStatus.DUPLICATE_ACCOUNT);
            accounts.put(accountNumber, new Account(accountNumber, currency));
        }
    }
}
