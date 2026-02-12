package com.astrocode.backend.api.controllers;

import com.astrocode.backend.api.dto.user.UserRegistrationRequest;
import com.astrocode.backend.api.dto.user.UserResponse;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(userService.toResponse(user));
    }

    @PostMapping
    public ResponseEntity<UserResponse> register(@RequestBody @Valid UserRegistrationRequest request) {
        var userResponse = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }
}
