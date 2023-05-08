package bsep.users;

import io.quarkus.mongodb.panache.PanacheMongoEntity;

import org.bson.types.ObjectId;

import ua_parser.Parser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class RefreshToken extends PanacheMongoEntity {

    public String value;
    public String name;
    public String user;
    public String device;
    public String os;
    public String browser;
    public LocalDateTime expiresAt;
    public LocalDateTime createdAt;
    public LocalDateTime revokedAt;
    public boolean revoked;

    public RefreshToken() {}

    public RefreshToken(String userId, String ua) {
        this.user = userId;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = createdAt.plusDays(30);
        this.value = UUID.randomUUID().toString().replace("-", "");

        var client = new Parser().parse(ua);

        this.device = client.device.family;
        this.os = client.os.family + " " + client.os.major;
        this.browser = client.userAgent.family + " " + client.userAgent.major;

        this.name = this.os + " (" + this.browser + ")";
    }

    public boolean revoke() {
        if (!isValid()) return false;

        revoked = true;
        revokedAt = LocalDateTime.now();
        this.update();

        return true;
    }

    public boolean isValid() {
        return !revoked && !isExpired();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public static List<RefreshToken> findAll(String userId) {
        return RefreshToken.find("revoked = ?1 AND user = ?2", false, userId).list();
    }

    public static RefreshToken findByValue(String value) {
        return RefreshToken
            .find(
                "value = ?1 AND revoked = ?2",
                value, false
            )
            .firstResult();
    }

    public static RefreshToken findById(String tokenId, String userId) {
        return RefreshToken
            .find(
                "user = ?1 AND _id = ?2 AND revoked = ?3",
                userId, new ObjectId(tokenId), false
            )
            .firstResult();
    }

}
