package ru.mnv.rvlt.core.model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class DailyBalanceTest {
    private DailyBalance balance;
    private BigDecimal initialBalance;
    @Before
    public void setUp() throws Exception {
        initialBalance = new BigDecimal(123456);
        balance = new DailyBalance(initialBalance);
    }

    @Test
    public void testInitialNetting() throws Exception {
        assertEquals(initialBalance,balance.getNetting());
    }

    @Test
    public void testRefill() throws Exception {
        BigDecimal amount = new BigDecimal(200);
        balance.refill(amount, mock(RequestInfo.class));
        assertEquals(initialBalance.add(amount), balance.getNetting());
    }

    @Test
    public void testRefillNegativeAmount() throws Exception {
        BigDecimal amount = new BigDecimal(200).negate();
        balance.refill(amount, mock(RequestInfo.class));
        assertEquals(initialBalance.add(amount.abs()), balance.getNetting());
    }

    @Test
    public void testWriteOff() throws Exception {
        BigDecimal amount = new BigDecimal(200);
        balance.writeOff(amount, mock(RequestInfo.class));
        assertEquals(initialBalance.subtract(amount), balance.getNetting());
    }

    @Test
    public void testWriteOffNegativeAmount() throws Exception {
        BigDecimal amount = new BigDecimal(200).negate();
        balance.writeOff(amount, mock(RequestInfo.class));
        assertEquals(initialBalance.subtract(amount.abs()), balance.getNetting());
    }

    @Test(expected = AccountingException.class)
    public void testWriteOffInsufficientFunds() {
        BigDecimal amount = new BigDecimal(200).negate();
        new DailyBalance(new BigDecimal(0)).writeOff(amount, mock(RequestInfo.class));
    }
}