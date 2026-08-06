package com.rdurbina.iodine.account.dto.request;

import com.rdurbina.iodine.error.constant.ErrorCodes;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Pattern(regexp = ".*\\S.*", message = ErrorCodes.REQUIRED)
        @Size(max = 30, message = ErrorCodes.TOO_LONG)
        String fullName
) {
    public UpdateUserRequest {
        if (fullName != null) {
            fullName = fullName.trim();
        }
    }
}
