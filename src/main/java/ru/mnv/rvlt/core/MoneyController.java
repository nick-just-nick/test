package ru.mnv.rvlt.core;

import ru.mnv.rvlt.core.dispatch.RequestDispatcher;
import ru.mnv.rvlt.core.model.Account;
import ru.mnv.rvlt.core.model.Request;
import ru.mnv.rvlt.core.model.RequestFactory;
import ru.mnv.rvlt.core.model.Result;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.function.Consumer;

@Path("/service")
@Produces(MediaType.TEXT_PLAIN)
public class MoneyController {
    private final AccountRepository accountRepository;
    private final RequestDispatcher requestDispatcher;
    private final RequestFactory requestFactory;

    public MoneyController(AccountRepository accountRepository, RequestDispatcher requestDispatcher, RequestFactory requestFactory) {
        this.accountRepository = accountRepository;
        this.requestDispatcher = requestDispatcher;
        this.requestFactory = requestFactory;
    }

    private void dispatch(Request request, Consumer<Result> replyTo) {
        requestDispatcher.dispatch(request, replyTo);
    }

    private Account account(String accountNumber) {
        return accountRepository.get(accountNumber);
    }

    private BigDecimal amount(String amount) {
        return new BigDecimal(amount);
    }

    private Response toHttpResponse(Result result) {
        if (!result.isSuccess()) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .header("errorCode", result.getStatus())
                    .entity(result.toString())
                    .build();
        } else {
            return Response
                    .status(Response.Status.OK)
                    .entity(result.toString())
                    .build();
        }
    }

    private Consumer<Result> replyTo(AsyncResponse asyncResponse) {
        return r -> asyncResponse.resume(toHttpResponse(r));
    }

    @GET
    @Path("/account/register")
    public Response registerAccount(@QueryParam("account") String accountNumber,
                                    @QueryParam("ccy") String currency) {
        accountRepository.addNew(accountNumber, currency);
        return Response.status(Response.Status.OK).entity("Account added").build();
    }

    @GET
    @Path("/account/statement")
    public Response accountStatement(@QueryParam("account") String accountNumber) {
        return Response
                .status(Response.Status.OK)
                .entity(accountRepository.get(accountNumber).getAccountStatement())
                .build();
    }

    @GET
    @Path("/account/netting")
    public Response accountNetting(@QueryParam("account") String accountNumber) {
        return Response
                .status(Response.Status.OK)
                .entity(accountRepository.get(accountNumber).getNetting().toString())
                .build();
    }

    @GET
    @Path("/refill")
    public void refill(@QueryParam("account") String accountNumber,
                       @QueryParam("amount") String amount,
                       @Suspended AsyncResponse asyncResponse) {
        dispatch(requestFactory.newRefill(account(accountNumber), amount(amount)), replyTo(asyncResponse));
    }

    @GET
    @Path("/writeoff")
    public void writeOff(@QueryParam("account") String accountNumber,
                         @QueryParam("amount") String amount,
                         @Suspended AsyncResponse asyncResponse) {
        dispatch(requestFactory.newWriteOff(account(accountNumber), amount(amount)), replyTo(asyncResponse));
    }

    @GET
    @Path("/transfer")
    public void transfer(@QueryParam("from") String from,
                         @QueryParam("to") String to,
                         @QueryParam("amount") String amount,
                         @QueryParam("comment") String comment,
                         @Suspended AsyncResponse asyncResponse) {
        Account fromAcc = accountRepository.get(from);
        Account toAcc = accountRepository.get(to);
        dispatch(requestFactory.newTransfer(fromAcc, toAcc, amount(amount), comment),
                replyTo(asyncResponse));
    }
}
