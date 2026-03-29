package com.rdurbina.iodine.dto.user.request;


import com.rdurbina.iodine.error.constant.ValidationErrorCodes;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserCreationRequest(
        @NotBlank(message = ValidationErrorCodes.REQUIRED)
        @Size(min = 2, message = ValidationErrorCodes.TOO_SHORT)
        String username,
        @NotBlank(message = ValidationErrorCodes.REQUIRED)
        @Size(min = 3, message = ValidationErrorCodes.TOO_SHORT)
        String fullName,
        @Email(message = ValidationErrorCodes.INVALID_FORMAT)
        String email,
        @NotBlank(message = ValidationErrorCodes.REQUIRED)
        @Pattern(
                message = ValidationErrorCodes.INVALID_FORMAT,
                regexp = "^(?=.*\\d)(?=.*[^A-Za-z0-9])(?=.*[A-Z])(?=.*[a-z]).{8,}$"
        )
        String password
) {
}
