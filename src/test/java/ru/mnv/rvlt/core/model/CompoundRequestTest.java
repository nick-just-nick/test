package ru.mnv.rvlt.core.model;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CompoundRequestTest {
    @Test
    public void testHappyWay() throws Exception {
        Request rq1 = mock(Request.class);
        Result success = mock(Result.class);
        when(success.isSuccess()).thenReturn(true);
        when(rq1.process()).thenReturn(success);
        Request rq2 = mock(Request.class);
        Result result = mock(Result.class);
        when(rq2.process()).thenReturn(result);
        CompoundRequest compoundRequest = new CompoundRequest(rq1, rq2);
        assertEquals(result, compoundRequest.process());
        verify(rq1,times(1)).process();
        verify(rq2,times(1)).process();
    }

    @Test
    public void testRq1Failed() throws Exception {
        Request rq1 = mock(Request.class);
        Result fail = mock(Result.class);
        when(fail.isSuccess()).thenReturn(false);
        when(rq1.process()).thenReturn(fail);
        Request rq2 = mock(Request.class);
        CompoundRequest compoundRequest = new CompoundRequest(rq1, rq2);
        assertEquals(fail, compoundRequest.process());
        verify(rq1,times(1)).process();
        verifyZeroInteractions(rq2);
    }

    @Test(expected = RuntimeException.class)
    public void testRq1Exception() throws Exception {
        Request rq1 = mock(Request.class);
        when(rq1.process()).thenThrow(RuntimeException.class);
        Request rq2 = mock(Request.class);
        new CompoundRequest(rq1, rq2).process();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidConfiguration() throws Exception {
        Request rq1 = mock(Request.class);
        new CompoundRequest(rq1, rq1);
    }

}