package com.rdurbina.iodine.account.dto.request;


import com.rdurbina.iodine.error.constant.ErrorCodes;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserCreationRequest(
        @NotBlank(message = ErrorCodes.REQUIRED)
        @Size(min = 2, message = ErrorCodes.TOO_SHORT)
        @Size(max = 20, message = ErrorCodes.TOO_LONG)
        String username,
        @NotBlank(message = ErrorCodes.REQUIRED)
        @Size(max = 30, message = ErrorCodes.TOO_LONG)
        String fullName,
        @NotBlank(message = ErrorCodes.REQUIRED)
        @Email(message = ErrorCodes.INVALID_FORMAT)
        String email,
        @NotBlank(message = ErrorCodes.REQUIRED)
        @Pattern(
                message = ErrorCodes.INVALID_FORMAT,
                regexp = "^(?=.*[A-Z])(?=.*[^A-Za-z0-9\\s])\\S{8,}$"
        )
        String password
) {
}
