package com.rdurbina.iodine.account.unit;

import com.rdurbina.iodine.account.User;
import com.rdurbina.iodine.account.UserRepository;
import com.rdurbina.iodine.account.UserService;
import com.rdurbina.iodine.account.dto.request.LoginRequest;
import com.rdurbina.iodine.account.dto.request.UpdateEmailRequest;
import com.rdurbina.iodine.account.dto.request.UpdateUserRequest;
import com.rdurbina.iodine.account.dto.request.UserCreationRequest;
import com.rdurbina.iodine.account.dto.response.UserCreationResponse;
import com.rdurbina.iodine.account.dto.response.UserResponse;
import com.rdurbina.iodine.auth.JwtService;
import com.rdurbina.iodine.error.ConflictException;
import com.rdurbina.iodine.error.NotFoundException;
import com.rdurbina.iodine.error.constant.ErrorCodes;
import com.rdurbina.iodine.error.constant.ErrorMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private UserService userService;

    private UserCreationRequest getMockRequest() {
        return new UserCreationRequest(
                "johndoe",
                "John Doe",
                "john.doe@example.com",
                "Password!"
        );
    }

    private User getMockUser() {
        return User.builder()
                .id(1L)
                .username("johndoe")
                .fullName("John Doe")
                .email("johndoe123@gmail.com")
                .password("SomeHashFromApassword1234")
                .build();
    }

    @Test
    void create_givenInvalidUsername_shouldThrowConflictException() {
        UserCreationRequest request = getMockRequest();
        when(userRepository.existsByUsername(request.username())).thenReturn(true);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> userService.create(request)
        );

        assertAll(
                () -> assertEquals(1, exception.getDetails().size()),
                () -> assertEquals("Username", exception.getDetails().getFirst().field()),
                () -> assertEquals(ErrorCodes.ALREADY_IN_USE, exception.getDetails().getFirst().code())
        );
        verify(userRepository).existsByUsername(request.username());
        verify(userRepository, never()).save(any());
    }

    @Test
    void create_givenInvalidEmail_shouldThrowConflictException() {
        UserCreationRequest request = getMockRequest();
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> userService.create(request)
        );

        assertAll(
                () -> assertEquals(1, exception.getDetails().size()),
                () -> assertEquals("Email", exception.getDetails().getFirst().field()),
                () -> assertEquals(ErrorCodes.ALREADY_IN_USE, exception.getDetails().getFirst().code())
        );
        verify(userRepository).existsByEmail(request.email());
        verify(userRepository, never()).save(any());
    }

    @Test
    void create_givenValidInput_shouldHashPasswordPersistUserAndReturnToken() {
        UserCreationRequest request = getMockRequest();
        String encodedPassword = "$2a$encoded-password";
        when(passwordEncoder.encode(request.password())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });
        when(jwtService.generateToken(request.username())).thenReturn("generated-jwt-token");

        UserCreationResponse response = userService.create(request);

        assertAll(
                () -> assertEquals(1L, response.id()),
                () -> assertEquals(request.username(), response.username()),
                () -> assertEquals(request.email(), response.email()),
                () -> assertEquals("generated-jwt-token", response.jwt())
        );
        verify(passwordEncoder).encode(request.password());
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(request.username());
    }

    // TODO: Email update tests are incomplete as of now because OTP code generation is not yet implemented

    @Test
    void updateEmail_givenInvalidUserId_shouldThrowNotFoundException() {
        UpdateEmailRequest request = new UpdateEmailRequest(1L, "john.doe@example.com");
        when(userRepository.findById(request.id())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateEmail(request));

        verify(userRepository).findById(request.id());
    }

    @Test
    void updateEmail_givenInvalidEmail_shouldThrowConflictException() {
        UpdateEmailRequest request = new UpdateEmailRequest(1L, "john.doe@example.com");
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.updateEmail(request));

        verify(userRepository).existsByEmail(request.email());
    }

    @Test
    void login_givenUnknownUsername_shouldThrowGenericBadCredentialsException() {
        LoginRequest request = new LoginRequest("unknown", "Password!");
        when(userRepository.findByUsername(request.username())).thenReturn(Optional.empty());

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> userService.login(request)
        );

        assertEquals(ErrorMessages.BAD_CREDENTIALS, exception.getMessage());
        verify(userRepository).findByUsername(request.username());
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_givenWrongPassword_shouldThrowGenericBadCredentialsException() {
        LoginRequest request = new LoginRequest("johndoe", "WrongPassword!");
        User user = getMockUser();
        when(userRepository.findByUsername(request.username())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(false);

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> userService.login(request)
        );

        assertEquals(ErrorMessages.BAD_CREDENTIALS, exception.getMessage());
        verify(passwordEncoder).matches(request.password(), user.getPassword());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_givenCorrectInput_shouldReturnToken() {
        LoginRequest request = new LoginRequest("johndoe", "Password!");
        User user = getMockUser();
        when(userRepository.findByUsername(request.username())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(request.username())).thenReturn("generated-jwt-token");

        String jwt = userService.login(request);

        assertEquals("generated-jwt-token", jwt);
        verify(jwtService).generateToken(request.username());
    }

    @Test
    void update_givenFullName_shouldUpdateOnlyFullNameAndPersistUser() {
        User user = getMockUser();
        UpdateUserRequest request = new UpdateUserRequest("Jane Doe");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserResponse response = userService.update(user.getUsername(), request);

        assertAll(
                () -> assertEquals(user.getId(), response.id()),
                () -> assertEquals("Jane Doe", response.fullName()),
                () -> assertEquals("johndoe", response.username()),
                () -> assertEquals("johndoe123@gmail.com", response.email())
        );
        verify(userRepository).findByUsername(user.getUsername());
        verify(userRepository).save(user);
    }

    @Test
    void update_givenOmittedFullName_shouldKeepCurrentValue() {
        User user = getMockUser();
        UpdateUserRequest request = new UpdateUserRequest(null);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserResponse response = userService.update(user.getUsername(), request);

        assertEquals("John Doe", response.fullName());
        verify(userRepository).save(user);
    }

    @Test
    void update_givenUnknownAuthenticatedUser_shouldThrowNotFoundException() {
        UpdateUserRequest request = new UpdateUserRequest("Jane Doe");
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update("unknown", request));

        verify(userRepository).findByUsername("unknown");
        verify(userRepository, never()).save(any());
    }
}
