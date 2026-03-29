package com.rdurbina.iodine.error.response;

import java.time.LocalDateTime;

public record ErrorResponse(String errorType, String url, LocalDateTime timestamp) {
}
