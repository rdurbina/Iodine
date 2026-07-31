package com.rdurbina.iodine.account.dto.response;

public record UserResponse(
        Long id,
        String fullName,
        String username,
        String email,
        String password
) {
}
