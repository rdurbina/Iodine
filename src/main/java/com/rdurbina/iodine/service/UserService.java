package com.rdurbina.iodine.service;

import com.rdurbina.iodine.dto.user.request.LoginRequest;
import com.rdurbina.iodine.dto.user.request.UpdateEmailRequest;
import com.rdurbina.iodine.dto.user.request.UserCreationRequest;
import com.rdurbina.iodine.dto.user.response.UserResponse;
import com.rdurbina.iodine.error.NotFoundException;
import com.rdurbina.iodine.error.ConflictException;
import com.rdurbina.iodine.error.constant.ErrorMessages;
import com.rdurbina.iodine.error.constant.ErrorCodes;
import com.rdurbina.iodine.error.response.ErrorDetail;
import com.rdurbina.iodine.mapper.UserMapper;
import com.rdurbina.iodine.model.User;
import com.rdurbina.iodine.repository.UserRepository;
import com.rdurbina.iodine.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /*
     * Creates a new user after validating uniqueness constraints.
     * @param userCreationRequest the user creation payload
     * @return the created user as a {@link UserResponse}
     * @throws ConflictError if the username or email is already in use
     */
    public UserResponse create(UserCreationRequest userCreationRequest) {
        ConflictException conflictException = new ConflictException(ErrorMessages.VALIDATION);
        boolean isUsernameTaken = this.userRepository.existsByUsername(userCreationRequest.username());
        if (isUsernameTaken) {
            conflictException.addDetail(new ErrorDetail(
                    User.Field.USERNAME.getName(),
                    ErrorCodes.ALREADY_IN_USE)
            );
        }
        boolean isEmailTaken = this.userRepository.existsByEmail(userCreationRequest.email());
        if (isEmailTaken) {
            conflictException.addDetail(new ErrorDetail(
                    User.Field.EMAIL.getName(),
                    ErrorCodes.ALREADY_IN_USE)
            );
        }

        if (!conflictException.getDetails().isEmpty()) {
            throw conflictException;
        }

        //Hash the password and save the new entry
        User newUser = UserMapper.toModel(userCreationRequest);
        String hashedPassword = this.passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(hashedPassword);

        User persistedUser = this.userRepository.save(newUser);

        return UserMapper.toResponse(persistedUser);
    }

    public String login(LoginRequest loginRequest) {
        // Retrieve user from DB
        User user = this.userRepository.findByUsername(loginRequest.username()).orElseThrow(
                ()-> new NotFoundException("User not found")
        );
        // Compare passwords
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new BadCredentialsException("Bad credentials");
        }
        // Issue and return token
        return this.jwtService.generateToken(user.getUsername());
    }

    //TODO: Implement email verification mechanism before updating the email
    public UserResponse updateEmail(UpdateEmailRequest updateEmailRequest) {
        ConflictException conflictException = new ConflictException(ErrorMessages.VALIDATION);
        String email = updateEmailRequest.email();
        Long id = updateEmailRequest.id();
        boolean isEmailInUse = this.userRepository.existsByEmail(email);
        if (isEmailInUse) {
            conflictException.addDetail(new ErrorDetail(
                    User.Field.EMAIL.getName(),
                    ErrorCodes.ALREADY_IN_USE
            ));
            throw conflictException;
        }
        User user = this.userRepository.findById(id).orElseThrow(()-> new NotFoundException(ErrorMessages.NOT_FOUND));
        user.setEmail(email);
        User updatedUser = this.userRepository.save(user);
        return UserMapper.toResponse(updatedUser);
    }
}
