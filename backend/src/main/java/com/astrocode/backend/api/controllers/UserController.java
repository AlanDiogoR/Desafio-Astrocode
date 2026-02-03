package com.astrocode.backend.api.controllers;

import com.astrocode.backend.api.dto.UserRegistrationRequest;
import com.astrocode.backend.api.dto.UserResponse;
import com.astrocode.backend.domain.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsável pelos endpoints relacionados a usuários.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint para cadastro de novo usuário.
     * 
     * @param request DTO com os dados do usuário a ser cadastrado
     * @return ResponseEntity com status 201 CREATED e o objeto UserResponse
     */
    @PostMapping
    public ResponseEntity<UserResponse> register(@RequestBody @Valid UserRegistrationRequest request) {
        var userResponse = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }
}
