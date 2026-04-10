package com.rdurbina.iodine.dto.user.response;

public record UserCreationResponse(
        Long id,
        String fullName,
        String username,
        String email,
        String password,
        String jwt
) {
}
