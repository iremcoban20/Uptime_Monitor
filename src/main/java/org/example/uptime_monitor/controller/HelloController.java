package org.example.uptime_monitor.controller;

import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class HelloController {

    @GetMapping("/api/hello")
    public Map<String, Object> sayHello() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Uptime & Performance Monitor API is running successfully!");
        response.put("status", "HEALTHY");
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
}
