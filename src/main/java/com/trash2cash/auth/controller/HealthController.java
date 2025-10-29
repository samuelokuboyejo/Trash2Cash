package com.trash2cash.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/")
    public String healthCheck() {
        return "OK";
    }

    @GetMapping("/health")
    public String health() {
        return "Healthy";
    }

}
