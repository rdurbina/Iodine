package com.rdurbina.iodine.account;

import com.rdurbina.iodine.error.FieldType;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "app_user",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_app_user_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_app_user_email", columnNames = "email")
        }
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(nullable = false, length = 30)
    private String fullName;
    @Column(nullable = false, length = 20)
    private String username;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
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
