package com.rdurbina.iodine.error;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class AppError extends RuntimeException {
    private final ErrorType errorType;
    private final List<Map<Object, Object>> details;

    public AppError(ErrorType errorType,  List<Map<Object, Object>> details) {
        this.errorType = errorType;
        this.details = details;
    }

    public AppError(ErrorType errorType) {
        this.errorType = errorType;
        this.details = new ArrayList<>();
    }

    public void addDetail(Map<Object, Object> detail) {
        this.details.add(detail);
    }
}
