package com.rdurbina.iodine.account.unit;

import com.rdurbina.iodine.account.dto.request.UpdateUserRequest;
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

class UpdateUserRequestValidationTest {
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
    void fullName_whenOmittedOrWithinLimit_isAccepted() {
        assertTrue(validator.validate(new UpdateUserRequest(null)).isEmpty());
        UpdateUserRequest requestWithSurroundingWhitespace = new UpdateUserRequest(
                "  " + "J".repeat(30) + "  "
        );

        assertTrue(validator.validate(requestWithSurroundingWhitespace).isEmpty());
        assertEquals("J".repeat(30), requestWithSurroundingWhitespace.fullName());
    }

    @Test
    void fullName_whenBlank_isRejected() {
        assertViolation(new UpdateUserRequest("   "), ErrorCodes.REQUIRED);
    }

    @Test
    void fullName_whenOverThirtyCharacters_isRejected() {
        assertViolation(new UpdateUserRequest("J".repeat(31)), ErrorCodes.TOO_LONG);
    }

    private void assertViolation(UpdateUserRequest request, String code) {
        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        long matchingViolations = violations.stream()
                .filter(violation -> violation.getPropertyPath().toString().equals("fullName"))
                .filter(violation -> violation.getMessage().equals(code))
                .count();

        assertEquals(1, matchingViolations, () -> "Expected fullName to fail with " + code);
    }
}
