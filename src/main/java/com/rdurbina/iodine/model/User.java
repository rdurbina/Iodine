package com.rdurbina.iodine.model;

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

    public enum Field {
        ID,
        USERNAME,
        FULL_NAME,
        EMAIL,
        PASSWORD
    }
}
