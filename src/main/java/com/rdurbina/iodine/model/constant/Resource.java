package com.rdurbina.iodine.model.constant;

import lombok.Getter;

@Getter
public enum Resource {
    USER("User");

    private final String name;

    Resource(String name) {
        this.name = name;
    }
}
