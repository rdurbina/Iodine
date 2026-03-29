package com.rdurbina.iodine.service;

import com.rdurbina.iodine.dto.user.request.UserCreationRequest;
import com.rdurbina.iodine.dto.user.response.UserResponse;
import com.rdurbina.iodine.error.ValidationException;
import com.rdurbina.iodine.error.constant.ErrorMessages;
import com.rdurbina.iodine.error.constant.ValidationErrorCodes;
import com.rdurbina.iodine.error.response.ErrorDetail;
import com.rdurbina.iodine.mapper.UserMapper;
import com.rdurbina.iodine.model.User;
import com.rdurbina.iodine.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /*
     * Creates a new user after validating uniqueness constraints.
     * @param userCreationRequest the user creation payload
     * @return the created user as a {@link UserResponse}
     * @throws AppError if the username or email is already in use
     */
    public UserResponse create(UserCreationRequest userCreationRequest) {
        ValidationException validationError = new ValidationException(ErrorMessages.Validation);
        boolean isUsernameTaken = this.userRepository.existsByUsername(userCreationRequest.username());
        if (isUsernameTaken) {
            validationError.addDetail(new ErrorDetail(
                    User.Field.USERNAME.getName(),
                    ValidationErrorCodes.ALREADY_IN_USE)
            );
        }
        boolean isEmailTaken = this.userRepository.existsByEmail(userCreationRequest.email());
        if (isEmailTaken) {
            validationError.addDetail(new ErrorDetail(
                    User.Field.EMAIL.getName(),
                    ValidationErrorCodes.ALREADY_IN_USE)
            );
        }

        if (!validationError.getDetails().isEmpty()) {
            throw validationError;
        }

        //Hash the password and save the new entry
        User newUser = UserMapper.toModel(userCreationRequest);
        String hashedPassword = this.passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(hashedPassword);

        User persistedUser = this.userRepository.save(newUser);

        return UserMapper.toResponse(persistedUser);
    }
}
