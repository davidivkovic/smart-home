package bsep.api.dto.authentication;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class RegistrationRequest {
    @NotBlank @Email @Size(max = 128)
    public String email;

    @NotBlank @Size(min = 8, max = 128) @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).+$")
    public String password;

    @NotBlank @Size(max = 64) @Pattern(regexp = "[a-zA-Z\\s]*")
    public String firstName;

    @NotBlank @Size(max = 64) @Pattern(regexp = "[a-zA-Z\\s]*")
    public String lastName;
}
