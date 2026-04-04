package com.rdurbina.iodine.error;

import com.rdurbina.iodine.error.response.ErrorDetail;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ConflictException extends RuntimeException {
    private final List<ErrorDetail> details;

    public ConflictException(String message) {
        super(message);
        this.details = new ArrayList<>();
    }

    public void addDetail(ErrorDetail detail) {
        this.details.add(detail);
    }
}
