package com.rdurbina.iodine.model;

import com.rdurbina.iodine.error.FieldType;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private String password;

    @Getter
    public enum Field implements FieldType {
        ID("Id"),
        USERNAME("Username"),
        FULL_NAME("FullName"),
        EMAIL("Email"),
        PASSWORD("Password");

        private final String name;

        Field(String name) {
            this.name = name;
        }
    }

}
