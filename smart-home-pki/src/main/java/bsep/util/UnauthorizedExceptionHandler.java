package bsep.util;

import io.quarkus.security.UnauthorizedException;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Singleton;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

@Alternative
@Priority(1)
public class UnauthorizedExceptionHandler implements ExceptionMapper<UnauthorizedException> {
    @Override
    public Response toResponse(UnauthorizedException exception) {
        System.out.println("UnauthorizedExceptionHandler");
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
