package com.rdurbina.iodine.dto.user.request;

import com.rdurbina.iodine.error.constant.ErrorCodes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = ErrorCodes.REQUIRED)
        @Size(min = 2, message = ErrorCodes.TOO_SHORT)
        String username,
        @NotBlank(message = ErrorCodes.REQUIRED)
        @Pattern(
                message = ErrorCodes.INVALID_FORMAT,
                regexp = "^(?=.*\\d)(?=.*[^A-Za-z0-9])(?=.*[A-Z])(?=.*[a-z]).{8,}$"
        )
        String password
) {
}
