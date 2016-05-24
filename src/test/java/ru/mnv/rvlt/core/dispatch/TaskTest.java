package ru.mnv.rvlt.core.dispatch;

import org.junit.Test;
import ru.mnv.rvlt.core.model.*;

import java.util.function.Consumer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TaskTest {

    @Test
    public void testHashCode() throws Exception {
        int hashCode = 100500;
        Request request = new Request() {
            @Override
            public Result process() {
                return null;
            }

            @Override
            public Long getId() {
                return null;
            }

            @Override
            public int hashCode() {
                return hashCode;
            }
        };
        Task task = new Task(request, mock(Consumer.class));
        assertEquals(hashCode, task.hashCode());
    }

    @Test
    public void testReturnValue() throws Exception {
        Result result = Result.fail(12l, ResultStatus.SAME_ACCOUNT, "Message");
        Request request = new Request() {
            @Override
            public Result process() {
                return result;
            }

            @Override
            public Long getId() {
                return null;
            }
        };
        Consumer<Result> consumer = mock(Consumer.class);
        new Task(request, consumer).run();
        verify(consumer,times(1)).accept(eq(result));
    }

    @Test
    public void testExceptionHandling() throws Exception {
        String message = "Something bad";
        ResultStatus status = ResultStatus.SAME_ACCOUNT;
        Request request = new Request() {
            @Override
            public Result process() {
                throw new AccountingException(message, status);
            }

            @Override
            public Long getId() {
                return null;
            }
        };
        new Task(request, (r) -> {
            assertEquals(status, r.getStatus());
            assertEquals(message, r.getMessage());
        }).run();
    }

    @Test
    public void testRawExceptionHandling() throws Exception {
        String message = "Something bad";
        Request request = new Request() {
            @Override
            public Result process() {
                throw new RuntimeException(message);
            }

            @Override
            public Long getId() {
                return null;
            }
        };
        new Task(request, (r) -> {
            assertEquals(ResultStatus.UNKNOWN, r.getStatus());
            assertEquals(message, r.getMessage());
        }).run();
    }
}