package com.rdurbina.iodine.account.dto.response;

public record UserCreationResponse(
        Long id,
        String fullName,
        String username,
        String email,
        String password,
        String jwt
) {
}
