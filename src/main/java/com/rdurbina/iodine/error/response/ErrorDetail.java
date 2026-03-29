package com.rdurbina.iodine.error.response;

public record ErrorDetail(String resource, String field, String code) {
    public ErrorDetail(String field, String code) {
        this(null, field, code);
    }
}
