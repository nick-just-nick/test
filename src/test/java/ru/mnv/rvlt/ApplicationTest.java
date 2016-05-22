package ru.mnv.rvlt;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.net.ServerSocket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ApplicationTest {
    private Application app;
    private WebTarget target;
    private final static String ERROR_CODE = "errorCode";
    private final String accNum = "123456";
    private final String ccy = "rub";

    @Before
    public void setUp() throws Exception {
        ServerSocket serverSocket = new ServerSocket(0);
        int port = serverSocket.getLocalPort();
        String address = serverSocket.getInetAddress().getHostAddress();
        serverSocket.close();
        app = new Application(port);
        app.start();
        target = JerseyClientBuilder.newClient().target(String.format("http://%s:%d/service", address, port));
    }

    @After
    public void tearDown() throws Exception {
        app.stop();
    }

    private Response registerAccount(String number, String ccy) {
        return target.path("/account/register")
                .queryParam("account", number)
                .queryParam("ccy", ccy)
                .request()
                .get();
    }

    private Response refill(String number, BigDecimal amount) {
        return target
                .path("/refill")
                .queryParam("account", number)
                .queryParam("amount", amount.toString())
                .request()
                .get();
    }

    private Response writeOff(String number, BigDecimal amount) {
        return target
                .path("/writeoff")
                .queryParam("account", number)
                .queryParam("amount", amount)
                .request()
                .get();
    }

    private Response netting(String number) {
        return target
                .path("/account/netting")
                .queryParam("account", number)
                .request()
                .get();
    }

    private Response statement(String number) {
        return target
                .path("/account/statement")
                .queryParam("account", number)
                .request()
                .get();
    }

    private Response transfer(String from, String to, BigDecimal amount, String comment) {
        WebTarget webTarget = target
                .path("/transfer")
                .queryParam("from", from)
                .queryParam("to", to)
                .queryParam("amount", amount.toString());
        if (comment != null) {
            webTarget = webTarget.queryParam("comment", comment);
        }
        return webTarget.request().get();
    }

    private void checkNetting(BigDecimal expected, Response response) {
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(String.valueOf(expected), response.readEntity(String.class));
    }

    @Test
    public void testNewAccountAndNetting() throws Exception {
        Response response = registerAccount(accNum, "RUB");
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        checkNetting(new BigDecimal(0), netting(accNum));
    }

    @Test
    public void testDuplicateAccount() throws Exception {
        Response response = registerAccount(accNum, ccy);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = registerAccount(accNum, ccy);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("DUPLICATE_ACCOUNT", response.getHeaderString(ERROR_CODE));
    }

    @Test
    public void testUnknownAccountStatement() throws Exception {
        Response response = target.path("/account/statement")
                .queryParam("account", accNum)
                .request()
                .get();
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("UNKNOWN_ACCOUNT", response.getHeaderString(ERROR_CODE));
    }

    @Test
    public void testRefill() throws Exception {
        registerAccount(accNum, ccy);
        BigDecimal amount = new BigDecimal(100500);
        assertEquals(Response.Status.OK.getStatusCode(), refill(accNum, amount).getStatus());
        checkNetting(amount, netting(accNum));
        BigDecimal secondAmount = new BigDecimal(200);
        assertEquals(Response.Status.OK.getStatusCode(), refill(accNum, secondAmount).getStatus());
        checkNetting(amount.add(secondAmount), netting(accNum));
    }

    @Test
    public void testRefillUnknownAccount() throws Exception {
        BigDecimal amount = new BigDecimal(100500);
        Response response = refill(accNum, amount);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("UNKNOWN_ACCOUNT", response.getHeaderString(ERROR_CODE));
    }

    @Test
    public void testWriteOff() throws Exception {
        registerAccount(accNum, ccy);
        BigDecimal amount = new BigDecimal(100500);
        refill(accNum, amount);
        BigDecimal debt = new BigDecimal(1000);
        Response response = writeOff(accNum, debt);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        checkNetting(amount.subtract(debt), netting(accNum));
    }

    @Test
    public void testWriteOffInsufficientFunds() throws Exception {
        registerAccount(accNum, ccy);
        BigDecimal amount = new BigDecimal(100500);
        Response response = writeOff(accNum, amount);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("INSUFFICIENT_FUNDS", response.getHeaderString(ERROR_CODE));
    }

    @Test
    public void testStatement() throws Exception {
        registerAccount(accNum, ccy);
        Response response = statement(accNum);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        String result = response.readEntity(String.class);
        assertTrue(result.contains(String.format("%s (%s)",accNum, ccy)));
    }

    @Test
    public void testTransfer() throws Exception {
        registerAccount(accNum, ccy);
        String secondAcc = accNum + "000";
        registerAccount(secondAcc, ccy);
        BigDecimal amount = new BigDecimal(100500);
        refill(accNum, amount);
        BigDecimal credit = new BigDecimal(500);
        String comment = "The quick brown fox jumps over the lazy dog";
        assertEquals(Response.Status.OK.getStatusCode(), transfer(accNum, secondAcc, credit, comment).getStatus());
        checkNetting(amount.subtract(credit), netting(accNum));
        checkNetting(credit, netting(secondAcc));
        Response response = statement(accNum);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        String statement = response.readEntity(String.class);
        assertTrue(statement.contains(comment));
    }

    @Test
    public void testTransferSameAccount() throws Exception {
        registerAccount(accNum, ccy);
        BigDecimal amount = new BigDecimal(100500);
        String comment = "";
        Response response = transfer(accNum, accNum, amount, comment);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("SAME_ACCOUNT", response.getHeaderString(ERROR_CODE));
    }

    @Test
    public void testTransferDiffCurrency() throws Exception {
        registerAccount(accNum, ccy);
        String secondAcc = accNum + "000";
        registerAccount(secondAcc, ccy+"E");
        BigDecimal amount = new BigDecimal(100500);
        String comment = "";
        Response response = transfer(accNum, secondAcc, amount, comment);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("NOT_EQUAL_CCY", response.getHeaderString(ERROR_CODE));
    }
}