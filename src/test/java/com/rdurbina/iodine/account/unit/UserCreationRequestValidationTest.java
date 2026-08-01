package com.rdurbina.iodine.account.unit;

import com.rdurbina.iodine.account.dto.request.LoginRequest;
import com.rdurbina.iodine.account.dto.request.UserCreationRequest;
import com.rdurbina.iodine.error.constant.ErrorCodes;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserCreationRequestValidationTest {
    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    void validRequest_acceptsAllBoundaryValues() {
        UserCreationRequest minimums = request("ab", "J", "j@example.com", "ABCDEFG!");
        UserCreationRequest maximums = request(
                "a".repeat(20),
                "J".repeat(30),
                "john.doe@example.com",
                "Password!"
        );

        assertTrue(validator.validate(minimums).isEmpty());
        assertTrue(validator.validate(maximums).isEmpty());
    }

    @Test
    void username_outsideLengthRange_isRejected() {
        assertViolation(request("a", "John", "j@example.com", "Password!"), "username", ErrorCodes.TOO_SHORT);
        assertViolation(request("a".repeat(21), "John", "j@example.com", "Password!"), "username", ErrorCodes.TOO_LONG);
    }

    @Test
    void fullName_blankOrOverThirtyCharacters_isRejected() {
        assertViolation(request("john", " ", "j@example.com", "Password!"), "fullName", ErrorCodes.REQUIRED);
        assertViolation(request("john", "J".repeat(31), "j@example.com", "Password!"), "fullName", ErrorCodes.TOO_LONG);
    }

    @Test
    void email_missingOrMalformed_isRejected() {
        assertViolation(request("john", "John", null, "Password!"), "email", ErrorCodes.REQUIRED);
        assertViolation(request("john", "John", "not-an-email", "Password!"), "email", ErrorCodes.INVALID_FORMAT);
    }

    @Test
    void password_requiresEightCharactersCapitalAndSpecialCharacter() {
        assertViolation(request("john", "John", "j@example.com", "Short!"), "password", ErrorCodes.INVALID_FORMAT);
        assertViolation(request("john", "John", "j@example.com", "password!"), "password", ErrorCodes.INVALID_FORMAT);
        assertViolation(request("john", "John", "j@example.com", "Password"), "password", ErrorCodes.INVALID_FORMAT);
    }

    @Test
    void login_blankCredentials_areRejected() {
        LoginRequest request = new LoginRequest("", "");

        assertViolation(request, "username", ErrorCodes.REQUIRED);
        assertViolation(request, "password", ErrorCodes.REQUIRED);
    }

    private void assertViolation(UserCreationRequest request, String field, String code) {
        assertViolationFor(request, field, code);
    }

    private void assertViolation(LoginRequest request, String field, String code) {
        assertViolationFor(request, field, code);
    }

    private <T> void assertViolationFor(T request, String field, String code) {
        Set<ConstraintViolation<T>> violations = validator.validate(request);
        long matchingViolations = violations.stream()
                .filter(violation -> violation.getPropertyPath().toString().equals(field))
                .filter(violation -> violation.getMessage().equals(code))
                .count();

        assertEquals(1, matchingViolations, () -> "Expected " + field + " to fail with " + code);
    }

    private UserCreationRequest request(String username, String fullName, String email, String password) {
        return new UserCreationRequest(username, fullName, email, password);
    }
}
