package com.rdurbina.iodine.service;

import com.rdurbina.iodine.dto.user.request.UserCreationRequest;
import com.rdurbina.iodine.dto.user.response.UserResponse;
import com.rdurbina.iodine.error.AppError;
import com.rdurbina.iodine.error.ErrorType;
import com.rdurbina.iodine.mapper.UserMapper;
import com.rdurbina.iodine.model.User;
import com.rdurbina.iodine.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;

    /*
     * Creates a new user after validating uniqueness constraints.
     * @param userCreationRequest the user creation payload
     * @return the created user as a {@link UserResponse}
     * @throws AppError if the username or email is already in use
     */
    public UserResponse create(UserCreationRequest userCreationRequest) {
        AppError validationError = new AppError(ErrorType.ALREADY_IN_USE);
        boolean isUsernameTaken = this.userRepository.existsByUsername(userCreationRequest.username());
        if (isUsernameTaken) {
            validationError.addDetail(Map.of("field", User.Field.USERNAME));
        }
        boolean isEmailTaken = this.userRepository.existsByEmail(userCreationRequest.email());
        if (isEmailTaken) {
            validationError.addDetail(Map.of("field", User.Field.EMAIL));
        }
        //TODO
        //Encrypt password with bcrypt

        if (!validationError.getDetails().isEmpty()) {
            throw validationError;
        }

        User persistedUser = this.userRepository.save(
                UserMapper.toModel(userCreationRequest)
        );

        return UserMapper.toResponse(persistedUser);
    }
}
