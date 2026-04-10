package com.astrocode.backend.api.controllers;

import com.astrocode.backend.api.dto.auth.ForgotPasswordRequest;
import com.astrocode.backend.api.dto.auth.LoginRequest;
import com.astrocode.backend.api.dto.auth.LoginResponse;
import com.astrocode.backend.api.dto.auth.RefreshTokenRequest;
import com.astrocode.backend.api.dto.auth.ResendVerificationRequest;
import com.astrocode.backend.api.dto.auth.ResetPasswordRequest;
import com.astrocode.backend.config.ClientIpResolver;
import com.astrocode.backend.config.LoginRateLimiter;
import com.astrocode.backend.domain.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@Tag(name = "Autenticação", description = "Login, recuperação de senha e alteração de senha")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final LoginRateLimiter loginRateLimiter;
    private final ClientIpResolver clientIpResolver;

    public AuthController(AuthService authService,
                          LoginRateLimiter loginRateLimiter,
                          ClientIpResolver clientIpResolver) {
        this.authService = authService;
        this.loginRateLimiter = loginRateLimiter;
        this.clientIpResolver = clientIpResolver;
    }

    @Operation(summary = "Login", description = "Retorna access token, refresh token e dados do usuário (SPA persiste cookies no cliente)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request,
                                   HttpServletRequest httpRequest) {
        String clientIp = clientIpResolver.getClientIp(httpRequest);
        var bucket = loginRateLimiter.getBucketForIp(clientIp);
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header(HttpHeaders.RETRY_AFTER, "60")
                    .body(Map.of("error", "Muitas tentativas. Aguarde 1 minuto."));
        }
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Refresh token", description = "Emite novo access token e rotaciona refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request.refreshToken()));
    }

    @Operation(summary = "Reenviar e-mail de verificação", description = "Público; não revela se o e-mail existe")
    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(@RequestBody @Valid ResendVerificationRequest request,
                                                   HttpServletRequest httpRequest) {
        String clientIp = clientIpResolver.getClientIp(httpRequest);
        var bucket = loginRateLimiter.getBucketForIp("resend:" + clientIp);
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header(HttpHeaders.RETRY_AFTER, "60")
                    .build();
        }
        authService.resendVerificationEmail(request.email());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Recuperar senha", description = "Envia e-mail com link/código para redefinição de senha")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "E-mail de recuperação enviado"),
            @ApiResponse(responseCode = "400", description = "E-mail inválido ou não encontrado")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request,
                                               HttpServletRequest httpRequest) {
        String clientIp = clientIpResolver.getClientIp(httpRequest);
        var bucket = loginRateLimiter.getBucketForIp("forgot:" + clientIp);
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header(HttpHeaders.RETRY_AFTER, "60")
                    .build();
        }
        authService.requestPasswordReset(request.email());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Redefinir senha", description = "Redefine a senha usando o código recebido por e-mail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Código inválido ou expirado")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<LoginResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        LoginResponse response = authService.resetPasswordWithCode(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Logout", description = "Encerra sessão no cliente (tokens invalidados ao rotacionar refresh)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout realizado")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }
}
