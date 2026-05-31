package com.travel.discovery.controller;

import com.travel.discovery.service.CacheService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/cache")
public class CacheController {

    private final CacheService cacheService;
    private final String reloadToken;

    public CacheController(CacheService cacheService,
                           @Value("${admin.reload-token:}") String reloadToken) {
        this.cacheService = cacheService;
        this.reloadToken = reloadToken;
    }

    @PostMapping("/reload")
    public ResponseEntity<Map<String, Object>> reload(
        @RequestHeader(value = "jwt-token", required = false) String token
    ) {
        if (!StringUtils.hasText(reloadToken) || !matches(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing admin token");
        }

        Collection<String> cleared = cacheService.reloadCache();
        return ResponseEntity.ok(Map.of(
            "status", "ok",
            "clearedCaches", cleared
        ));
    }

    // Constant-time comparison to avoid leaking the token via timing.
    private boolean matches(String provided) {
        if (provided == null) {
            return false;
        }
        return MessageDigest.isEqual(
            provided.getBytes(StandardCharsets.UTF_8),
            reloadToken.getBytes(StandardCharsets.UTF_8)
        );
    }
}