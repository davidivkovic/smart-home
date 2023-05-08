package bsep.api.dto.authentication;

import java.time.LocalDateTime;

import java.time.LocalDateTime;

public class RefreshTokenDTO {

    public String id;
    public String name;
    public String device;
    public String os;
    public String browser;
    public LocalDateTime expiresAt;
    public boolean revoked;
    public boolean isThisDevice;

}

