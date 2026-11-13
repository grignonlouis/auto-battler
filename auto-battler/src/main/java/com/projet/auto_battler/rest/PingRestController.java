package com.projet.auto_battler.rest;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple health-check endpoint used to verify that the API is running.
 */
@RestController
@RequestMapping("/api/v1")
public class PingRestController {

    /**
     * Answers a ping request.
     *
     * @return a JSON object with status "ok"
     */
    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("status", "ok");
    }
}