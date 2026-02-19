package com.astrocode.backend.api.controllers;

import com.astrocode.backend.api.dto.auth.ForgotPasswordRequest;
import com.astrocode.backend.api.dto.auth.LoginRequest;
import com.astrocode.backend.api.dto.auth.LoginResponse;
import com.astrocode.backend.api.dto.auth.ResetPasswordRequest;
import com.astrocode.backend.domain.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticação", description = "Login, recuperação de senha e alteração de senha")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Login", description = "Retorna token JWT para acessar endpoints protegidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Recuperar senha", description = "Envia e-mail com link/código para redefinição de senha")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "E-mail de recuperação enviado"),
            @ApiResponse(responseCode = "400", description = "E-mail inválido ou não encontrado")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        authService.requestPasswordReset(request.email());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Redefinir senha", description = "Redefine a senha usando o código recebido por e-mail e retorna novo token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Código inválido ou expirado")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<LoginResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        LoginResponse response = authService.resetPasswordWithCode(request);
        return ResponseEntity.ok(response);
    }
}
