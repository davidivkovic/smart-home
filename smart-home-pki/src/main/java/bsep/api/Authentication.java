package bsep.api;

import bsep.api.dto.authentication.*;
import bsep.users.RefreshToken;
import bsep.users.User;
import bsep.email.ConfirmEmail;
import bsep.api.dto.users.UserDTO;
import bsep.util.SecurityUtils;

import static bsep.util.Utils.mapper;

import io.quarkus.logging.Log;
import io.quarkus.security.Authenticated;

import org.bson.types.ObjectId;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

@Path("auth")
@Produces(MediaType.APPLICATION_JSON)
public class Authentication extends Resource {

    static final String REFRESH_TOKEN_COOKIE = "refresh-token";

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(
            @Valid @NotNull AuthenticationRequest request,
            @HeaderParam("User-Agent") String userAgent
    ) {
        Log.info("User-Agent: " + "david");

        var user = User.findByEmail(request.email);

        var success = User.authenticate(user, request.password);
        if (!success) return badRequest("The credentials you entered are not valid.");

        if (!user.emailConfirmed) return badRequest("Please confirm your email address before logging in.");
        if (user.lockedOut) return badRequest("Your account has been locked. Please contact an administrator.");

        if (user.MFAEnabled && request.MFACode == null) return forbidden();
        if (user.MFAEnabled && !user.verifyMFA(request.MFACode)) return badRequest(
            "The 2FA code you entered is not valid or has already expired."
        );

        var token = user.generateToken();
        var refreshToken = user.generateRefreshToken(userAgent);

        if (token == null || refreshToken == null) return badRequest(
            "We could not log you in at this time. Please try again later."
        );

        var cookie = new NewCookie(REFRESH_TOKEN_COOKIE, refreshToken, "/", null, null, 2592000, false, true);
        var response = new AuthenticationResponse(mapper.map(user, UserDTO.class), token);

        return ok(response, cookie);
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(@Valid @NotNull RegistrationRequest request) {
        var user = User.findByEmail(request.email);
        if (user != null) return badRequest("A user with this email already exists.");

        if (SecurityUtils.isCommonPassword(request.password)) return badRequest(
        "The password you entered is too easy to guess. Please choose a different one."
        );

        user = User.register(request.firstName, request.lastName, User.Roles.TENANT, request.email, request.password);
        var otp = user.generateOTP();

        ConfirmEmail.send(user.email, user.firstName, otp.code);

        return ok();
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

        return ok();
    }

    @POST
    @Authenticated
    @Path("/2fa/add")
    public Response add2FA() {
        User user = User.findById(new ObjectId(userId()));
        if (user == null) return badRequest("A user with this email does not exist.");

        if (user.MFAEnabled) return badRequest("2FA is already enabled for this user.");
        else user.generateMFASecret();

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

    @GET
    @Authenticated
    @Path("/2fa/status")
    public Response get2FAStatus() {
        User user = User.findById(new ObjectId(userId()));
        if (user == null) return badRequest("A user with this email does not exist.");

        return ok(user.MFAEnabled);
    }

    @GET
    @Authenticated
    @Path("/refresh-tokens")
    public Response getRefreshTokens(@CookieParam(REFRESH_TOKEN_COOKIE) String refreshToken) {
        User user = User.findById(new ObjectId(userId()));
        if (user == null) return badRequest("A user with this email does not exist.");

        var tokens = RefreshToken.findAll(userId());
        var response = tokens.stream().map(token -> {
            var dto = mapper.map(token, RefreshTokenDTO.class);
            dto.isThisDevice = token.value.equalsIgnoreCase(refreshToken);
            return dto;
        }).toList();

        return ok(response);
    }

    @POST
    @Authenticated
    @Path("/refresh-tokens/{id}/revoke")
    public Response revokeRefreshToken(@PathParam("id") @NotBlank @Size(max = 128) String id) {
        User user = User.findById(new ObjectId(userId()));
        if (user == null) return badRequest("A user with this email does not exist.");

        RefreshToken token = RefreshToken.findById(id, userId());
        if (token == null) return badRequest("A refresh token with this ID does not exist.");

        var success = token.revoke();
        if (!success) return badRequest("This refresh token has already been revoked.");

        var cookie = new NewCookie(REFRESH_TOKEN_COOKIE, "", "/", null, null, 0, false, true);
        return ok(cookie);
    }

    @POST
    @Path("/logout")
    public Response logout() {
        return ok("You have been logged out.");
    }

    @POST
    @Path("/token-refresh")
    public Response tokenRefresh(@CookieParam(REFRESH_TOKEN_COOKIE) String refreshToken) {
        RefreshToken token = RefreshToken.findByValue(refreshToken);
        if (token == null) return badRequest("This refresh token does not exist.");
        if (!token.isValid()) return badRequest("This refresh token is expired or has been revoked.");

        User user = User.findById(new ObjectId(token.user));
        if (user == null) return badRequest("A user with this email does not exist.");

        var jwt = user.generateToken();
        if (jwt == null) return badRequest("We could not log you in at this time. Please try again later.");

        var response = new AuthenticationResponse(mapper.map(user, UserDTO.class), jwt);
        return ok(response);
    }

}