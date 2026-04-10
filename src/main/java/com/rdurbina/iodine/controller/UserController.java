package com.rdurbina.iodine.controller;

import com.rdurbina.iodine.dto.user.request.UserCreationRequest;
import com.rdurbina.iodine.dto.user.response.UserCreationResponse;
import com.rdurbina.iodine.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@AllArgsConstructor
public class UserController {
    public final UserService userService;

    @PostMapping("/user")
    public ResponseEntity<UserCreationResponse> create(@RequestBody UserCreationRequest userCreationRequest) {
        UserCreationResponse persistedUser = this .userService.create(userCreationRequest);
        return ResponseEntity.ok(persistedUser);
    }
}
