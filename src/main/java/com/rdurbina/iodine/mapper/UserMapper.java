package com.rdurbina.iodine.mapper;

import com.rdurbina.iodine.dto.user.request.UserCreationRequest;
import com.rdurbina.iodine.dto.user.response.UserCreationResponse;
import com.rdurbina.iodine.dto.user.response.UserResponse;
import com.rdurbina.iodine.model.User;

public class UserMapper {
    public static User toModel(UserCreationRequest userCreationRequest) {
        return User.builder()
                .fullName(userCreationRequest.fullName())
                .username(userCreationRequest.username())
                .password(userCreationRequest.password())
                .email(userCreationRequest.email())
                .build();
    }

    public static UserCreationResponse toCreationResponse(User user, String token) {
        return new UserCreationResponse(
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                token
        );
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword()
        );
    }
}
