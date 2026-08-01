package com.rdurbina.iodine.error;

import com.rdurbina.iodine.error.constant.ErrorMessages;
import com.rdurbina.iodine.error.response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void badCredentials_returnsGenericUnauthorizedResponse() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/login");

        ResponseEntity<ErrorResponse> response = handler.handleBadCredentials(
                new BadCredentialsException("internal authentication detail"),
                request
        );
        ErrorResponse body = response.getBody();

        assertNotNull(body);
        assertAll(
                () -> assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode()),
                () -> assertEquals("AuthenticationError", body.errorType()),
                () -> assertEquals(ErrorMessages.BAD_CREDENTIALS, body.message()),
                () -> assertEquals("/login", body.url()),
                () -> assertTrue(body.details().isEmpty())
        );
    }
}
