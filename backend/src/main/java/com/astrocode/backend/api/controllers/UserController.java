package com.astrocode.backend.api.controllers;

import com.astrocode.backend.api.dto.user.DeleteAccountRequest;
import com.astrocode.backend.api.dto.user.UpdateProfileRequest;
import com.astrocode.backend.api.dto.user.UserRegistrationRequest;
import com.astrocode.backend.api.dto.user.UserResponse;
import com.astrocode.backend.api.dto.user.WhatsappPhoneRequest;
import com.astrocode.backend.api.dto.user.WhatsappVerifyRequest;
import com.astrocode.backend.config.ClientIpResolver;
import com.astrocode.backend.config.LoginRateLimiter;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Usuários", description = "Cadastro, perfil e dados do usuário autenticado")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final LoginRateLimiter loginRateLimiter;
    private final ClientIpResolver clientIpResolver;

    public UserController(UserService userService,
                          LoginRateLimiter loginRateLimiter,
                          ClientIpResolver clientIpResolver) {
        this.userService = userService;
        this.loginRateLimiter = loginRateLimiter;
        this.clientIpResolver = clientIpResolver;
    }

    @Operation(summary = "Obter perfil", description = "Retorna os dados do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @SecurityRequirement(name = "bearer-jwt")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(userService.toResponse(user));
    }

    @Operation(summary = "Atualizar perfil", description = "Atualiza nome e/ou e-mail do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    })
    @SecurityRequirement(name = "bearer-jwt")
    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateMe(@AuthenticationPrincipal User user,
                                                  @RequestBody @Valid UpdateProfileRequest request) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(userService.updateProfile(user, request));
    }

    @SecurityRequirement(name = "bearer-jwt")
    @PostMapping("/me/whatsapp/request")
    public ResponseEntity<?> requestWhatsapp(@AuthenticationPrincipal User user,
                                               @RequestBody @Valid WhatsappPhoneRequest request) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            userService.requestWhatsappVerification(user, request.phone());
            return ResponseEntity.accepted().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @SecurityRequirement(name = "bearer-jwt")
    @PostMapping("/me/whatsapp/verify")
    public ResponseEntity<?> verifyWhatsapp(@AuthenticationPrincipal User user,
                                            @RequestBody @Valid WhatsappVerifyRequest request) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            userService.verifyWhatsapp(user, request.code());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Cadastrar usuário", description = "Registro de novo usuário (endpoint público)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    })
    @PostMapping
    public ResponseEntity<UserResponse> register(@RequestBody @Valid UserRegistrationRequest request,
                                                 HttpServletRequest httpRequest) {
        String clientIp = clientIpResolver.getClientIp(httpRequest);
        var bucket = loginRateLimiter.getBucketForIp("register:" + clientIp);
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header(HttpHeaders.RETRY_AFTER, "60")
                    .build();
        }
        var userResponse = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @Operation(summary = "Confirmar e-mail", description = "Valida token enviado por e-mail (público)")
    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        userService.verifyEmail(token);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Excluir minha conta", description = "Remove todos os dados do usuário (LGPD)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Conta excluída"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "400", description = "Senha inválida")
    })
    @SecurityRequirement(name = "bearer-jwt")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal User user,
                                         @RequestBody @Valid DeleteAccountRequest request) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        userService.deleteCurrentUser(user, request.password());
        return ResponseEntity.noContent().build();
    }
}
