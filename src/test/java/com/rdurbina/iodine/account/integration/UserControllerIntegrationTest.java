package com.rdurbina.iodine.account.integration;

import com.rdurbina.iodine.account.User;
import com.rdurbina.iodine.account.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest {
    private static final String VALID_REQUEST = """
            {
              "username": "johndoe",
              "fullName": "John Doe",
              "email": "john.doe@example.com",
              "password": "Password!"
            }
            """;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void clearUsers() {
        userRepository.deleteAll();
    }

    @Test
    void create_validJson_returnsCreatedUserAndPersistsHashedPassword() throws Exception {
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REQUEST))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.jwt", not(blankOrNullString())));

        User persistedUser = userRepository.findByUsername("johndoe").orElseThrow();
        assertTrue(passwordEncoder.matches("Password!", persistedUser.getPassword()));
    }

    @Test
    void create_duplicateUsernameAndEmail_returnsConflictDetails() throws Exception {
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REQUEST))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REQUEST))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorType").value("ValidationError"))
                .andExpect(jsonPath("$.message").value("ValidationFailed"))
                .andExpect(jsonPath("$.url").value("/user"))
                .andExpect(jsonPath("$.details[*].field", hasItems("Username", "Email")))
                .andExpect(jsonPath("$.details[*].code", hasItems("AlreadyInUse", "AlreadyInUse")));
    }

    @Test
    void create_invalidFields_returnsBadRequestWithFieldDetails() throws Exception {
        String invalidRequest = """
                {
                  "username": "x",
                  "fullName": "",
                  "email": "not-an-email",
                  "password": "password"
                }
                """;

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorType").value("ValidationError"))
                .andExpect(jsonPath("$.message").value("ValidationFailed"))
                .andExpect(jsonPath("$.url").value("/user"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.details[*].field", hasItems("Username", "FullName", "Email", "Password")))
                .andExpect(jsonPath("$.details[*].code", hasItems("TooShort", "Required", "InvalidFormat")));
    }

    @Test
    void create_malformedJson_returnsBadRequestError() throws Exception {
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not-json}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("InvalidRequest"))
                .andExpect(jsonPath("$.details").isEmpty());
    }

    @Test
    void login_validCredentials_returnsJwt() throws Exception {
        createUser();

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest("johndoe", "Password!")))
                .andExpect(status().isOk())
                .andExpect(result -> assertTrue(!result.getResponse().getContentAsString().isBlank()));
    }

    private void createUser() throws Exception {
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REQUEST))
                .andExpect(status().isCreated());
    }

    private String loginRequest(String username, String password) {
        return """
                {
                  "username": "%s",
                  "password": "%s"
                }
                """.formatted(username, password);
    }
}
