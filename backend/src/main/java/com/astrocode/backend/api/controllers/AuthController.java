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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticação", description = "Login, recuperação de senha e alteração de senha")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final int COOKIE_MAX_AGE_SECONDS = 60 * 60 * 24 * 7;

    private final AuthService authService;
    private final com.astrocode.backend.config.LoginRateLimiter loginRateLimiter;
    private final String trustedProxies;

    public AuthController(AuthService authService,
                          com.astrocode.backend.config.LoginRateLimiter loginRateLimiter,
                          @Value("${app.trusted-proxies:}") String trustedProxies) {
        this.authService = authService;
        this.loginRateLimiter = loginRateLimiter;
        this.trustedProxies = trustedProxies != null ? trustedProxies.trim() : "";
    }

    @Operation(summary = "Login", description = "Define cookie httpOnly com JWT e retorna dados do usuário (token não vem no body)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request,
                                                HttpServletRequest httpRequest,
                                                HttpServletResponse httpResponse) {
        String clientIp = getClientIp(httpRequest);
        var bucket = loginRateLimiter.getBucketForIp(clientIp);
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(429)
                    .header("Retry-After", "60")
                    .build();
        }
        LoginResponse response = authService.login(request);
        var cookie = new Cookie("auth_token", response.token());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_MAX_AGE_SECONDS);
        cookie.setAttribute("SameSite", "None");
        httpResponse.addCookie(cookie);
        return ResponseEntity.ok(response.withoutToken());
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

    @Operation(summary = "Redefinir senha", description = "Redefine a senha usando o código recebido por e-mail e define cookie httpOnly com JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Código inválido ou expirado")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<LoginResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest request,
                                                       HttpServletResponse httpResponse) {
        LoginResponse response = authService.resetPasswordWithCode(request);
        var cookie = new Cookie("auth_token", response.token());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_MAX_AGE_SECONDS);
        cookie.setAttribute("SameSite", "None");
        httpResponse.addCookie(cookie);
        return ResponseEntity.ok(response.withoutToken());
    }

    @Operation(summary = "Logout", description = "Remove o cookie de autenticação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout realizado")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse httpResponse) {
        var cookie = new Cookie("auth_token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "None");
        httpResponse.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    /** IP para rate limit: com {@code app.trusted-proxies}, confia em {@code X-Forwarded-For} só se {@code RemoteAddr} for proxy ({@link IpAddressMatcher}). */
    private String getClientIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr() != null ? request.getRemoteAddr() : "unknown";
        if (trustedProxies.isBlank()) {
            return remoteAddr;
        }
        for (String cidr : trustedProxies.split(",")) {
            String trimmed = cidr.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            try {
                IpAddressMatcher matcher = new IpAddressMatcher(trimmed);
                if (matcher.matches(remoteAddr)) {
                    String client = firstClientFromForwardedFor(request.getHeader("X-Forwarded-For"));
                    if (client != null && !client.isBlank()) {
                        return client;
                    }
                    break;
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
        return remoteAddr;
    }

    private static String firstClientFromForwardedFor(String xForwardedFor) {
        if (xForwardedFor == null || xForwardedFor.isBlank()) {
            return null;
        }
        String first = xForwardedFor.split(",")[0].trim();
        if (first.startsWith("\"") && first.endsWith("\"") && first.length() >= 2) {
            first = first.substring(1, first.length() - 1).trim();
        }
        if (first.startsWith("[") && first.contains("]")) {
            return first.substring(1, first.indexOf(']'));
        }
        int lastColon = first.lastIndexOf(':');
        if (lastColon > 0 && first.chars().filter(ch -> ch == ':').count() == 1) {
            return first.substring(0, lastColon);
        }
        return first;
    }
}
