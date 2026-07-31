package com.rdurbina.iodine.account.dto.request;

import com.rdurbina.iodine.error.constant.ErrorCodes;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record UpdateEmailRequest(
        @NotNull(message = ErrorCodes.REQUIRED)
        @Positive(message = ErrorCodes.INVALID_FORMAT)
        Long id,
        @Pattern(
                message = ErrorCodes.INVALID_FORMAT,
                regexp = "^(?=.*\\d)(?=.*[^A-Za-z0-9])(?=.*[A-Z])(?=.*[a-z]).{8,}$"
        )
        String email
) {
}
