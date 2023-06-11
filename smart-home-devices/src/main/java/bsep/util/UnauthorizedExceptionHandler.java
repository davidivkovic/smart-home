package bsep.util;

import io.quarkus.security.UnauthorizedException;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

@Alternative
@Priority(1)
public class UnauthorizedExceptionHandler implements ExceptionMapper<UnauthorizedException> {
    @Override
    public Response toResponse(UnauthorizedException exception) {
        System.out.println("UnauthorizedExceptionHandler");
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
