package com.rdurbina.iodine.dto.user.request;

import com.rdurbina.iodine.error.constant.ErrorCodes;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DeleteUserRequest(
        @NotNull(message = ErrorCodes.REQUIRED)
        @Positive(message = ErrorCodes.INVALID_FORMAT)
        Long id
) {
}
