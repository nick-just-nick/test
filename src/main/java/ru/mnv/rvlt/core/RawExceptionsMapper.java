package ru.mnv.rvlt.core;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

@Provider
public class RawExceptionsMapper implements ExceptionMapper<Throwable> {
    private static final Logger log = Logger.getLogger(RawExceptionsMapper.class.getName());
    @Override
    public Response toResponse(Throwable e) {
        log.severe(e.getMessage());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
}
