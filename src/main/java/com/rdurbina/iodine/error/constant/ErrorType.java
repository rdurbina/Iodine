package com.rdurbina.iodine.error.constant;

import lombok.Getter;

//Used for classification in the AppError class.
@Getter
public enum ErrorType {
    VALIDATION("ValidationError"),
    NOT_FOUND("NotFound");

    private final String type;
    ErrorType(String type) {
        this.type = type;
    }
}
