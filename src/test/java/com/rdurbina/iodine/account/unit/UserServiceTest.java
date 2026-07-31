package com.rdurbina.iodine.account.unit;

import com.rdurbina.iodine.account.User;
import com.rdurbina.iodine.account.UserRepository;
import com.rdurbina.iodine.account.UserService;
import com.rdurbina.iodine.account.dto.request.LoginRequest;
import com.rdurbina.iodine.account.dto.request.UpdateEmailRequest;
import com.rdurbina.iodine.account.dto.request.UserCreationRequest;
import com.rdurbina.iodine.account.dto.response.UserCreationResponse;
import com.rdurbina.iodine.auth.JwtService;
import com.rdurbina.iodine.error.NotFoundException;
import com.rdurbina.iodine.error.ConflictException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private UserService userService;

    public UserCreationRequest getMockRequest() {
        return new UserCreationRequest(
                "johndoe",
                "John Doe",
                "john.doe@example.com",
                "securePassword123"
        );
    }

    public User getMockUser() {
        return User.builder()
                .id(1L)
                .username("johndoe")
                .fullName("John Doe")
                .email("johndoe123@gmail.com")
                .password("SomeHashFromApassword1234")
                .build();
    }

    @Test
    public void create_givenInvalidUsername_shouldThrowConflictException() {
        UserCreationRequest request = getMockRequest();
        when(userRepository.existsByUsername(request.username())).thenReturn(true);
        Assertions.assertThrows(ConflictException.class, () -> {
            UserCreationResponse userCreationResponse = this.userService.create(request);
        });
        verify(userRepository).existsByUsername(request.username());
    }

    @Test
    public void create_givenInvalidEmail_shouldThrowConflictException() {
        UserCreationRequest request = getMockRequest();
        when(userRepository.existsByEmail(request.email())).thenReturn(true);
        Assertions.assertThrows(ConflictException.class, () -> {
            UserCreationResponse userCreationResponse = this.userService.create(request);
        });
        verify(userRepository).existsByEmail(request.email());
    }

    @Test
    public void updateEmail_givenInvalidUserId_shouldThrowNotFoundException() {
        UpdateEmailRequest request = new UpdateEmailRequest(1L, "john.doe@example.com");
        when(userRepository.findById(request.id())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, ()-> userService.updateEmail(request));
        verify(userRepository).findById(request.id());
    }

    @Test
    public void updateEmail_givenInvalidEmail_shouldThrowConflictException() {
        UpdateEmailRequest request = new UpdateEmailRequest(1L, "john.doe@example.com");
        when(userRepository.existsByEmail(request.email())).thenReturn(true);
        Assertions.assertThrows(ConflictException.class, ()-> userService.updateEmail(request));
        verify(userRepository).existsByEmail(request.email());
    }

    @Test
    public void login_givenInvalidUsername_shouldThrowNotFoundException() {
        LoginRequest loginRequest = new LoginRequest("johndoe", "StrongAndComplicatedPassword123#!");
        when(userRepository.findByUsername(loginRequest.username())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, ()-> userService.login(loginRequest));
        verify(userRepository).findByUsername(loginRequest.username());
    }

    @Test
    public void login_givenInvalidPassword_shouldThrowBadCredentialsException() {
        LoginRequest loginRequest = new LoginRequest("johndoe", "StrongAndComplicatedPassword123#!");
        when(userRepository.findByUsername(loginRequest.username())).thenReturn(Optional.of(getMockUser()));
        when(passwordEncoder.matches(loginRequest.password(), getMockUser().getPassword())).thenReturn(false);
        Assertions.assertThrows(BadCredentialsException.class, ()-> userService.login(loginRequest));
        verify(passwordEncoder).matches(loginRequest.password(), getMockUser().getPassword());
    }

    @Test
    public void login_givenCorrectInput_shouldReturnStringToken() {
        LoginRequest loginRequest = new LoginRequest("johndoe", "StrongAndComplicatedPassword123#!");
        when(userRepository.findByUsername(loginRequest.username())).thenReturn(Optional.of(getMockUser()));
        when(passwordEncoder.matches(loginRequest.password(), getMockUser().getPassword())).thenReturn(true);
        when(jwtService.generateToken(loginRequest.username())).thenReturn("generated-jwt-token");
        String jwt = userService.login(loginRequest);
        Assertions.assertEquals("generated-jwt-token", jwt);

    }
}
