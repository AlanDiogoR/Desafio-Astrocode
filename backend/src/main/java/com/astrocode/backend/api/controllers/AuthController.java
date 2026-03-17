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

    private String getClientIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr() != null ? request.getRemoteAddr() : "unknown";
        if (trustedProxies.isBlank()) {
            return remoteAddr;
        }
        for (String cidr : trustedProxies.split(",")) {
            String trimmed = cidr.trim();
            if (!trimmed.isEmpty() && isInCidr(remoteAddr, trimmed)) {
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isBlank()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                break;
            }
        }
        return remoteAddr;
    }

    private boolean isInCidr(String ip, String cidr) {
        if (cidr.contains("/")) {
            String[] parts = cidr.split("/");
            if (parts.length != 2) return false;
            int mask = Integer.parseInt(parts[1]);
            return ipMatchesCidr(ip, parts[0].trim(), mask);
        }
        return ip.equals(cidr.trim());
    }

    private boolean ipMatchesCidr(String ip, String network, int maskBits) {
        try {
            long ipLong = ipToLong(ip);
            long networkLong = ipToLong(network);
            long mask = maskBits == 0 ? 0 : -1L << (32 - maskBits);
            return (ipLong & mask) == (networkLong & mask);
        } catch (Exception e) {
            return false;
        }
    }

    private long ipToLong(String ip) {
        String[] octets = ip.split("\\.");
        if (octets.length != 4) return -1;
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result = (result << 8) | Integer.parseInt(octets[i].trim());
        }
        return result;
    }
}
