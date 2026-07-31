package com.rdurbina.iodine.error.response;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        String errorType,
        String message,
        String url,
        LocalDateTime timestamp,
        List<ErrorDetail> details
) {
    public ErrorResponse {
        details = List.copyOf(details);
    }
}
