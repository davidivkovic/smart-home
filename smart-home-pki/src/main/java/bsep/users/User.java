package bsep.users;

import bsep.crypto.PBKDF2;

import io.quarkus.mongodb.panache.PanacheMongoEntity;

import io.smallrye.jwt.build.Jwt;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.bson.types.ObjectId;

import javax.management.relation.Role;
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

    public static boolean authenticate(User user, String password) {
        // Timing attack hardening, hash in constant time even if the user is null
        if (user == null) return PBKDF2.verify(password, User.emptySalt, User.emptyPassword);
        return PBKDF2.verify(password, user.salt, user.password);
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
}
