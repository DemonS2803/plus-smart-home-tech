package ru.yandex.practicum.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Fallback endpoint для Circuit Breaker — возвращается при срабатывании
 * circuit breaker для product-service.
 */
@RestController
public class GatewayFallbackController {

    @GetMapping("/fallback/products")
    public ResponseEntity<Map<String, String>> fallback() {
        return ResponseEntity
                .status(503)
                .body(Map.of(
                        "error", "Service temporarily unavailable",
                        "message", "Service is currently unavailable. Please try again later."
                ));
    }
}
