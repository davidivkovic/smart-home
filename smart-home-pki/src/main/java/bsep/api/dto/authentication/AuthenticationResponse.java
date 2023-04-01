package bsep.api.dto.authentication;

import bsep.api.dto.users.UserDTO;

public record AuthenticationResponse(UserDTO user, String token) { }