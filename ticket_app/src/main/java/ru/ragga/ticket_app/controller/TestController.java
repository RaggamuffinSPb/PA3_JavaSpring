package ru.ragga.ticket_app.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/* исключительно тестовый эндпоинт */
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping
    public String test(Authentication authentication) {
        if (authentication == null) {
            return "{\"status\" : \"error\", \"user\": \"" + authentication.getName() + "\"}";
        }
        return "{\"status\" : \"OK\", \"user\": \"" + authentication.getName() + "\"}";
    }
}