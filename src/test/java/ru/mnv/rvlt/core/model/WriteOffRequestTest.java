package ru.mnv.rvlt.core.model;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class WriteOffRequestTest {

    @Test
    public void testHashCode() throws Exception {
        Account account = new Account(AccountNumber.fromString("123456"), "RUB");
        WriteOffRequest request = new WriteOffRequest(account, new BigDecimal(10), mock(RequestInfo.class));
        assertEquals(account.hashCode(), request.hashCode());
    }
}