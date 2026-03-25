package com.rdurbina.iodine.dto.user.response;

public record UserResponse(
        Long id,
        String fullName,
        String username,
        String email,
        String password
) {}
