package bsep.api.dto.authentication;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class AuthenticationRequest {
    @NotBlank @Email @Size(max = 128)
    public String email;

    @NotBlank @Size(min = 6, max = 128)
    public String password;

    @Size(min = 6, max = 16) @Pattern(regexp = "^[0-9A-Za-z]*$")
    public String MFACode;
}