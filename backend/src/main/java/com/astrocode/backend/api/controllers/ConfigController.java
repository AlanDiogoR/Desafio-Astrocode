package com.astrocode.backend.api.controllers;

import com.astrocode.backend.api.dto.config.PublicConfigResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @Value("${mp.public-key:}")
    private String mpPublicKey;

    @GetMapping("/public")
    public ResponseEntity<PublicConfigResponse> getPublicConfig() {
        return ResponseEntity.ok(new PublicConfigResponse(mpPublicKey != null ? mpPublicKey : ""));
    }
}
