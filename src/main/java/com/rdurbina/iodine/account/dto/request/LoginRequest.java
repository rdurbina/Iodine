package com.rdurbina.iodine.account.dto.request;

import com.rdurbina.iodine.error.constant.ErrorCodes;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = ErrorCodes.REQUIRED)
        String username,
        @NotBlank(message = ErrorCodes.REQUIRED)
        String password
) {
}
