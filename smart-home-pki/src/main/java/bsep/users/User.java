package bsep.users;

import bsep.crypto.PBKDF2;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;

import com.j256.twofactorauth.TimeBasedOneTimePasswordUtil;

import static com.j256.twofactorauth.TimeBasedOneTimePasswordUtil.DEFAULT_OTP_LENGTH;
import static com.j256.twofactorauth.TimeBasedOneTimePasswordUtil.DEFAULT_QR_DIMENTION;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.smallrye.jwt.build.Jwt;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.bson.types.ObjectId;

import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.*;

public class User extends PanacheMongoEntity {

    public static class Roles {

        public static final String ADMIN = "admin";
        public static final String LANDLORD = "landlord";
        public static final String TENANT = "tenant";
        public static final List<String> all = List.of(ADMIN, LANDLORD, TENANT);
    }

    /**
     * One Time Password
     */
    public static class OTP {
        public String code;
        public Date expiresAt;

        public OTP() {
            this.code = RandomStringUtils.random(6, false, true);
            this.expiresAt = DateUtils.addMinutes(new Date(), 30);
        }

        public boolean isValid(String code) {
            return this.code.equals(code) && this.expiresAt.after(new Date());
        }
    }

    private static final String emptySalt = "Ga7zWuh6aiRPLgF41UsLZw4mQzN5FY1e3nbMiYxACHo5";
    private static final String emptyPassword = PBKDF2.hash("TIMING_ATTACK_HARDENING", emptySalt);

    public String firstName;
    public String lastName;
    public String fullName;
    public String email;
    public String role;
    public boolean emailConfirmed;
    public String password;
    public String salt;
    public OTP otp;
    public boolean MFAEnabled;
    public String MFASecret;
    public boolean lockedOut;
    public int failedLoginAttempts;

    public static User register(String firstName, String lastName, String role, String email, String password) {
        User user = new User();

        user.salt = RandomStringUtils.random(32, true, true);
        user.password = PBKDF2.hash(password, user.salt);

        user.firstName = firstName;
        user.lastName = lastName;
        user.fullName = firstName + " " + lastName;
        user.email = email;
        user.role = role;

        user.persist();

        return user;
    }

    public void incrementLockout() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 5) this.lockedOut = true;
        this.update();
    }

    public void resetLockout() {
        this.failedLoginAttempts = 0;
        this.lockedOut = false;
        this.update();
    }

    // Timing attack hardening, hash in constant time even if the user is null
    public static boolean authenticate(User user, String password) {
        if (user == null) return PBKDF2.verify(password, User.emptySalt, User.emptyPassword);
        var success = PBKDF2.verify(password, user.salt, user.password);

        if (success) user.resetLockout();
        else user.incrementLockout();

        return success;
    }

    public static String generateToken(User user) {
        return Jwt
            .issuer("http://localhost:8080/auth")
            .audience("http://localhost:5173")
            .upn(user.id.toString())
            .subject(user.email)
            .groups(new HashSet<>(List.of(user.role)))
            .expiresIn(Duration.ofHours(24))
            .sign();
    }

    public OTP generateOTP() {
        this.otp = new OTP();
        this.update();
        return this.otp;
    }

    public void updateRole(String role) {
        this.role = role;
        this.update();
    }

    public static User findById(ObjectId id) {
        return find("_id", id).firstResult();
    }

    public static User findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public boolean confirmEmail(String token) {
        if (this.otp == null || !this.otp.isValid(token)) return false;

        this.emailConfirmed = true;
        this.otp = null;
        this.update();

        return true;
    }

    public static List<User> search(String query, int page, int pageSize) {
        if (query == null || query.trim().isEmpty()) {
            return findAll().page(page, pageSize).list();
        }
        return find("fullName like ?1", "/" + query + "/i")
                .page(page, pageSize)
                .list();
    }

    public boolean isAdmin() {
        return this.role.equals(Roles.ADMIN);
    }

    public void generateMFASecret() {
        this.MFASecret = TimeBasedOneTimePasswordUtil.generateBase32Secret();
    }

    public String generateMFAQRCode() {
        var authenticatorURL = "otpauth://totp/" + "Smart%20Home:" + this.email
            + "?secret=" + this.MFASecret
            + "&issuer=smart-home"
            + "&digits=" + DEFAULT_OTP_LENGTH
            + "&period=" + 30;
        var stream = new ByteArrayOutputStream();
        try {
            MatrixToImageWriter.writeToStream(
                new QRCodeWriter().encode(
                    authenticatorURL,
                    BarcodeFormat.QR_CODE,
                    DEFAULT_QR_DIMENTION,
                    DEFAULT_QR_DIMENTION
                ),
                "png",
                stream
            );
            return Base64.getEncoder().encodeToString(stream.toByteArray());
        }
        catch (Exception e) { return null; }
    }

    public boolean finishMFASetup(int code) {
        try {
            this.MFAEnabled = TimeBasedOneTimePasswordUtil.validateCurrentNumber(
                this.MFASecret, code, 5000
            );
            this.update();
        }
        catch (Exception e) { /* Ignore */ }
        return this.MFAEnabled;
    }

    public boolean disableMFA(String code) {
        if (!this.MFAEnabled || !verifyMFA(code)) return false;
        this.MFAEnabled = false;
        this.MFASecret = null;
        this.update();
        return true;
    }

    public boolean verifyMFA(String code) {
        if (!this.MFAEnabled) return false;

        boolean success = false;
        try {
            if (code.length() == 6) success = TimeBasedOneTimePasswordUtil.validateCurrentNumber(
                this.MFASecret,
                Integer.parseInt(code),
                5000
            );
            else if (code.length() == 16) success = code.equalsIgnoreCase(this.MFASecret);
        }
        catch (Exception e) { /* Ignore */ }

        if (success) this.resetLockout() ;
        else this.incrementLockout();

        return success;
    }

}
