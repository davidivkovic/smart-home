package bsep.api;

import bsep.api.dto.authentication.AuthenticationRequest;
import bsep.api.dto.authentication.AuthenticationResponse;
import bsep.api.dto.authentication.RegistrationRequest;
import bsep.api.dto.users.UserDTO;
import bsep.email.ConfirmEmail;
import bsep.users.User;

import static bsep.util.Utils.mapper;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.MailTemplate;
import io.quarkus.mailer.Mailer;
import io.quarkus.qute.Location;
import io.quarkus.security.Authenticated;

import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.time.Duration;

@Path("auth")
@Produces(MediaType.APPLICATION_JSON)
public class Authentication extends Resource {

    @GET
    @Path("/example")
    @Authenticated
    public Response getExample() {
        return ok(userId());
    }

    @GET
    @Path("/me")
    @Authenticated
    public Response me(@Context SecurityContext ctx) {
        return ok(ctx.getUserPrincipal());
    }

    @POST
    @Path("/login")
    public Response login(@Valid @NotNull AuthenticationRequest request) {
        var user = User.findByEmail(request.email);

        var success = User.authenticate(user, request.password);
        if (!success) return badRequest("The credentials you entered are not valid.");

        if (!user.emailConfirmed) return badRequest("Please confirm your email address before logging in.");

        var token = User.generateToken(user);
        if (token == null) return badRequest("Could not log you in at this time. Please try again later.");

        var response = new AuthenticationResponse(mapper.map(user, UserDTO.class), token);
        return ok(response);
    }

    @POST
    @Path("/register")
    public Response register(@Valid @NotNull RegistrationRequest request) {
        var user = User.findByEmail(request.email);
        if (user != null) return badRequest("A user with this email already exists.");

        user = User.register(request.firstName, request.lastName, User.Roles.USER, request.email, request.password);
        var otp = user.generateOTP();

        ConfirmEmail.send(user.email, user.firstName, otp.code);

        return ok(otp);
    }

    @POST
    @Path("/confirm")
    public Response confirm(
        @QueryParam("email") @NotBlank @Size(max = 128) String email,
        @QueryParam("token") @NotBlank @Size(min = 6, max = 6) String token
    ) {
        var user = User.findByEmail(email);
        if (user == null) return badRequest("A user with this email does not exist.");

        if (user.otp == null) return badRequest("This user has not requested an email confirmation.");

        var success = user.confirmEmail(token);
        if (!success) return badRequest("The token you entered is not valid or has already expired.");

        return ok();
    }

    @POST
    @Path("/resend-confirmation")
    public Response resendConfirmation(@QueryParam("email") @NotBlank @Size(max = 128) String email) {
        var user = User.findByEmail(email);
        if (user == null) return badRequest("A user with this email does not exist.");

        if (user.otp == null) return badRequest("This user has not requested an email confirmation.");

        var otp = user.generateOTP();
        ConfirmEmail.send(user.email, user.firstName, otp.code);

        return ok(otp);
    }

}
