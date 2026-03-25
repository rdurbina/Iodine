package com.rdurbina.iodine.dto.user.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreationRequest(
        @NotBlank String username,
        @NotBlank String fullName,
        @Email String email,
        @NotBlank String password
) {}
