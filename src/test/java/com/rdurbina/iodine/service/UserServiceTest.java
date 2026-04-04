package com.rdurbina.iodine.service;

import com.rdurbina.iodine.dto.user.request.UpdateEmailRequest;
import com.rdurbina.iodine.dto.user.request.UserCreationRequest;
import com.rdurbina.iodine.dto.user.response.UserResponse;
import com.rdurbina.iodine.error.NotFoundException;
import com.rdurbina.iodine.error.ConflictException;
import com.rdurbina.iodine.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    public static UserCreationRequest getMockRequest() {
        return new UserCreationRequest(
                "johndoe",
                "John Doe",
                "john.doe@example.com",
                "securePassword123"
        );
    }

    @Test
    public void create_givenInvalidUsername_shouldThrowConflictException() {
        UserCreationRequest request = getMockRequest();
        when(userRepository.existsByUsername(request.username())).thenReturn(true);
        Assertions.assertThrows(ConflictException.class, () -> {
            UserResponse userResponse = this.userService.create(request);
        });
        verify(userRepository).existsByUsername(request.username());
    }

    @Test
    public void create_givenInvalidEmail_shouldThrowConflictException() {
        UserCreationRequest request = getMockRequest();
        when(userRepository.existsByEmail(request.email())).thenReturn(true);
        Assertions.assertThrows(ConflictException.class, () -> {
            UserResponse userResponse = this.userService.create(request);
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
}
