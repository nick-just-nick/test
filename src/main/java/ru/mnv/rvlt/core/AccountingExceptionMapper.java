package ru.mnv.rvlt.core;

import ru.mnv.rvlt.core.model.AccountingException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AccountingExceptionMapper implements ExceptionMapper<AccountingException> {
    @Override
    public Response toResponse(AccountingException e) {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .header("errorCode", e.getStatus().name())
                .entity(e.getMessage())
                .build();
    }
}
