package ru.mnv.rvlt.core.model;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class AccountTest {
    private String strNumber = "123456";
    private String ccy = "RUB";
    @Test
    public void testHashCod() throws Exception {
        AccountNumber accountNumber = AccountNumber.fromString(strNumber);
        Account account = new Account(accountNumber, "RUB");
        assertEquals(accountNumber.hashCode(), account.hashCode());
    }

    @Test
    public void testCheckCorrespondenceHappyWay() throws Exception {
        Account account1 = new Account(AccountNumber.fromString(strNumber), ccy);
        Account account2 = new Account(AccountNumber.fromString(strNumber+"7"), ccy);
        account1.checkCorrespondence(account2);
    }

    @Test(expected = AccountingException.class)
    public void testCheckCorrespondenceDiffCcy() throws Exception {
        Account account1 = new Account(AccountNumber.fromString(strNumber), ccy);
        Account account2 = new Account(AccountNumber.fromString(strNumber+"7"), ccy+"E");
        account1.checkCorrespondence(account2);
    }

    @Test(expected = AccountingException.class)
    public void testCheckCorrespondenceSameAccount() throws Exception {
        Account account1 = new Account(AccountNumber.fromString(strNumber), ccy);
        Account account2 = new Account(AccountNumber.fromString(strNumber), ccy);
        account1.checkCorrespondence(account2);
    }

    @Test
    public void testNetting() throws Exception {
        Account account = new Account(AccountNumber.fromString(strNumber), ccy);
        assertEquals(new BigDecimal(0), account.getNetting());
        int amount1 = 10;
        int amount2 = 20;
        int amount3 = 5;
        account.handleRequest(b -> b.refill(new BigDecimal(amount1), mock(RequestInfo.class)));
        account.handleRequest(b -> b.refill(new BigDecimal(amount2), mock(RequestInfo.class)));
        account.handleRequest(b -> b.writeOff(new BigDecimal(amount3), mock(RequestInfo.class)));
        assertEquals(new BigDecimal(amount1+amount2-amount3), account.getNetting());
    }
}