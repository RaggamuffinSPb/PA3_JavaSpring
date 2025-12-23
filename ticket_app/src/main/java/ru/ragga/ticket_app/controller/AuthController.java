package ru.ragga.ticket_app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ragga.ticket_app.dto.AuthRequest;
import ru.ragga.ticket_app.dto.AuthResponse;
import ru.ragga.ticket_app.service.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        log.info("Попытка входа для пользователя: {}", authRequest.getUsername());

        String token = authService.authenticateAndGetToken(
                authRequest.getUsername(),
                authRequest.getPassword()
        );

        if (token != null) {
            log.info("Успешный вход для пользователя: {}", authRequest.getUsername());
            return ResponseEntity.ok(new AuthResponse(token));
        } else {
            log.warn("Неудачная попытка входа для пользователя: {}", authRequest.getUsername());
            return ResponseEntity.status(401)
                    .body("{\"error\": \"Invalid username or password\"}");
        }
    }
    // узел для регистрации нового пользователя
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest authRequest) {
        log.info("Запрос на регистрацию пользователя: {}", authRequest.getUsername());

        try {
            authService.register(authRequest.getUsername(), authRequest.getPassword());
            return ResponseEntity.ok(Map.of(
                    "message", "Пользователь успешно зарегистрирован",
                    "username", authRequest.getUsername()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}