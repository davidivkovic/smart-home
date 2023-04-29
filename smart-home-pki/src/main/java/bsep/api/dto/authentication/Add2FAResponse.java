package bsep.api.dto.authentication;

public record Add2FAResponse(String qrCode, String secret) { }
