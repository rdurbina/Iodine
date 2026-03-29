package com.rdurbina.iodine.service;

import com.rdurbina.iodine.dto.user.request.UserCreationRequest;
import com.rdurbina.iodine.dto.user.response.UserResponse;
import com.rdurbina.iodine.error.ValidationException;
import com.rdurbina.iodine.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    public static UserCreationRequest getValidRequest() {
        return new UserCreationRequest(
                "johndoe",
                "John Doe",
                "john.doe@example.com",
                "securePassword123"
        );
    }

    @Test
    public void shouldFailAsTheUsernameIsTaken() {
        UserCreationRequest request = getValidRequest();
        when(userRepository.existsByUsername(request.username())).thenReturn(true);
        Assertions.assertThrows(ValidationException.class, () -> {
            UserResponse userResponse = this.userService.create(request);
        });
        verify(userRepository).existsByUsername(request.username());
    }

    @Test
    public void shouldFailAsTheEmailIsTaken() {
        UserCreationRequest request = getValidRequest();
        when(userRepository.existsByEmail(request.email())).thenReturn(true);
        Assertions.assertThrows(ValidationException.class, () -> {
            UserResponse userResponse = this.userService.create(request);
        });
        verify(userRepository).existsByEmail(request.email());
    }
}
