package com.astrocode.backend.api.controllers;

import com.astrocode.backend.api.dto.openfinance.OpenFinanceWaitlistRequest;
import com.astrocode.backend.config.ClientIpResolver;
import com.astrocode.backend.config.LoginRateLimiter;
import com.astrocode.backend.domain.entities.User;
import com.astrocode.backend.domain.services.OpenFinanceWaitlistService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Open Finance", description = "Lista de espera")
@RestController
@RequestMapping("/api/open-finance")
public class OpenFinanceWaitlistController {

    private final OpenFinanceWaitlistService waitlistService;
    private final LoginRateLimiter loginRateLimiter;
    private final ClientIpResolver clientIpResolver;

    public OpenFinanceWaitlistController(
            OpenFinanceWaitlistService waitlistService,
            LoginRateLimiter loginRateLimiter,
            ClientIpResolver clientIpResolver) {
        this.waitlistService = waitlistService;
        this.loginRateLimiter = loginRateLimiter;
        this.clientIpResolver = clientIpResolver;
    }

    @PostMapping("/waitlist")
    public ResponseEntity<Void> join(
            @RequestBody @Valid OpenFinanceWaitlistRequest body,
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest) {
        String clientIp = clientIpResolver.getClientIp(httpRequest);
        var bucket = loginRateLimiter.getBucketForIp("of-wait:" + clientIp);
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header(HttpHeaders.RETRY_AFTER, "60")
                    .build();
        }
        waitlistService.join(body.email(), user);
        return ResponseEntity.accepted().build();
    }
}
