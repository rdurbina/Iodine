package com.rdurbina.iodine.account.dto.request;


import com.rdurbina.iodine.error.constant.ErrorCodes;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserCreationRequest(
        @NotBlank(message = ErrorCodes.REQUIRED)
        @Size(min = 2, message = ErrorCodes.TOO_SHORT)
        String username,
        @NotBlank(message = ErrorCodes.REQUIRED)
        @Size(min = 3, message = ErrorCodes.TOO_SHORT)
        String fullName,
        @Email(message = ErrorCodes.INVALID_FORMAT)
        String email,
        @NotBlank(message = ErrorCodes.REQUIRED)
        @Pattern(
                message = ErrorCodes.INVALID_FORMAT,
                regexp = "^(?=.*\\d)(?=.*[^A-Za-z0-9])(?=.*[A-Z])(?=.*[a-z]).{8,}$"
        )
        String password
) {
}
