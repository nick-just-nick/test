package ru.mnv.rvlt.core.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class AccountNumberTest {
    @Test
    public void testHashCode() throws Exception {
        String strNumber = "123456";
        AccountNumber accountNumber = AccountNumber.fromString(strNumber);
        assertEquals(strNumber.hashCode(), accountNumber.hashCode());
    }
}