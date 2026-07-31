package com.rdurbina.iodine.account.integration;

import com.rdurbina.iodine.account.User;
import com.rdurbina.iodine.account.UserRepository;
import com.rdurbina.iodine.account.UserService;
import com.rdurbina.iodine.account.dto.request.LoginRequest;
import com.rdurbina.iodine.account.dto.request.UserCreationRequest;
import com.rdurbina.iodine.account.dto.response.UserCreationResponse;
import com.rdurbina.iodine.auth.JwtService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource("classpath:application-test.yaml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceIntegrationTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;

    @Test
    public void create_givenValidInput_shouldReturnThePersistedUser() {
        UserCreationRequest mockRequest =  new UserCreationRequest(
                "johndoe",
                "John Doe",
                "john.doe@example.com",
                "StrongAndComplicatedPassword123#!"
        );

        UserCreationResponse serviceResult = userService.create(mockRequest);

        Assertions.assertInstanceOf(UserCreationResponse.class, serviceResult);
    }

    @Test
    public void login_givenCorrectInput_shouldGenerateValidJwt() {
        String username = "johndoe";
        String rawPassword = "StrongAndComplicatedPassword123#!";
        LoginRequest loginRequest = new LoginRequest(username, rawPassword);
        User user = User.builder()
                .username(username)
                .fullName("John Doe")
                .email("john.doe@example.com")
                .password(rawPassword)
                .build();
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);

        String token = userService.login(loginRequest);

        Assertions.assertNotNull(token);
    }
}
