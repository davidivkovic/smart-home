package bsep.api;

import bsep.users.User;
import bsep.email.ConfirmEmail;
import bsep.api.dto.users.UserDTO;
import bsep.api.dto.authentication.AuthenticationRequest;
import bsep.api.dto.authentication.AuthenticationResponse;
import bsep.api.dto.authentication.Add2FAResponse;
import bsep.api.dto.authentication.RegistrationRequest;

import static bsep.util.Utils.mapper;

import io.quarkus.security.Authenticated;

import org.bson.types.ObjectId;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("auth")
@Produces(MediaType.APPLICATION_JSON)
public class Authentication extends Resource {

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(@Valid @NotNull AuthenticationRequest request) {
        var user = User.findByEmail(request.email);

        var success = User.authenticate(user, request.password);
        if (!success) return badRequest("The credentials you entered are not valid.");

        if (!user.emailConfirmed) return badRequest("Please confirm your email address before logging in.");
        if (user.lockedOut) return badRequest("Your account has been locked. Please contact an administrator.");

        if (user.MFAEnabled) {
            if (request.MFACode == null) return forbidden();
            if (!user.verifyMFA(request.MFACode.trim())) return badRequest(
                "The 2FA code you entered is not valid or has already expired."
            );
        }

        var token = User.generateToken(user);
        if (token == null) return badRequest("Could not log you in at this time. Please try again later.");

        var response = new AuthenticationResponse(mapper.map(user, UserDTO.class), token);
        return ok(response);
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(@Valid @NotNull RegistrationRequest request) {
        var user = User.findByEmail(request.email);
        if (user != null) return badRequest("A user with this email already exists.");

        user = User.register(request.firstName, request.lastName, User.Roles.TENANT, request.email, request.password);
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

    @POST
    @Authenticated
    @Path("/2fa/add")
    public Response add2FA() {
        User user = User.findById(new ObjectId(userId()));
        if (user == null) return badRequest("A user with this email does not exist.");

        if (!user.MFAEnabled) user.generateMFASecret();
        var qr = user.generateMFAQRCode();

        if (qr == null) return badRequest("Could not enable 2FA. Please try again later.");

        user.update();
        var response = new Add2FAResponse(qr, user.MFASecret);

        return ok(response);
    }

    @POST
    @Authenticated
    @Path("/2fa/confirm")
    public Response confirm2FA(@QueryParam("code") @NotNull int code) {
        User user = User.findById(new ObjectId(userId()));
        if (user == null) return badRequest("A user with this email does not exist.");

        if (user.MFASecret == null) return badRequest("2FA is not enabled for this user.");
        if (user.MFAEnabled) return badRequest("2FA is already enabled for this user.");

        var success = user.finishMFASetup(code);
        if (!success) return badRequest("The 2FA code you entered is not valid or has already expired.");

        return ok();
    }

    @POST
    @Authenticated
    @Path("/2fa/disable")
    public Response disable2FA(@QueryParam("code") @NotBlank @Size(min = 6, max = 16) String code) {
        User user = User.findById(new ObjectId(userId()));
        if (user == null) return badRequest("A user with this email does not exist.");

        if (!user.MFAEnabled) return badRequest("2FA is not enabled for this user.");

        var success = user.disableMFA(code);
        if (!success) return badRequest("The 2FA code you entered is not valid or has already expired.");

        return ok();
    }

}
