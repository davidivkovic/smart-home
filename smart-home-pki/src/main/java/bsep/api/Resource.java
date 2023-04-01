package bsep.api;

import bsep.users.User;
import io.quarkus.hibernate.validator.runtime.jaxrs.ResteasyViolationExceptionImpl;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Set;

public class Resource {

    @Context SecurityContext securityContext;

    public Response ok() {
        return Response.ok().build();
    }

    public Response ok(Object entity) {
        return Response.ok(entity).build();
    }

    public Response ok(Object entity, String mediaType) {
        return Response.ok(entity, mediaType).build();
    }

    public Response badRequest() {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    public Response badRequest(Object entity) {
        return Response.status(Response.Status.BAD_REQUEST).entity(entity).build();
    }

    public Response notFound() {
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    public <T> Response validationProblem(Set<ConstraintViolation<T>> validation) {
        throw new ResteasyViolationExceptionImpl(validation, List.of(MediaType.APPLICATION_JSON_TYPE));
    }

    public Response attachment(Object entity, String filename, String mediaType) {
        return Response
            .ok(entity, mediaType)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .build();
    }

    public boolean isAdmin() {
        return securityContext.isUserInRole(User.Roles.ADMIN);
    }

    public String userId() {
        return securityContext.getUserPrincipal().getName();
    }

}
