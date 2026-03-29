package com.rdurbina.iodine.error;

import com.rdurbina.iodine.error.response.ErrorDetail;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationException extends RuntimeException {
    private final List<ErrorDetail> details;

    public ValidationException(String message) {
        super(message);
        this.details = new ArrayList<>();
    }

    public void addDetail(ErrorDetail detail) {
        this.details.add(detail);
    }
}
