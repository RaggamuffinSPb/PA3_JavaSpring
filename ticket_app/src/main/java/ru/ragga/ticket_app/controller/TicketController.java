package ru.ragga.ticket_app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.ragga.ticket_app.dto.TicketRequest;
import ru.ragga.ticket_app.dto.TicketResponse;
import ru.ragga.ticket_app.service.TicketService;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Slf4j
public class TicketController {

    private final TicketService ticketService;

    /**
     * Создание нового залогового билета - POST /api/tickets
     * TODO: логировать в консоль
     */
    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody TicketRequest request) {
        String username = getCurrentUsername();
        log.info("Запрос на создание билета от пользователя: {}", username);

        TicketResponse response = ticketService.createTicket(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Пролонгация (продление) залогового билета - POST /api/tickets/{ИДшник челочисленный}/prolong
     * TODO: логировать в консоль
     */
    @PostMapping("/{id}/prolong")
    public ResponseEntity<TicketResponse> prolongTicket(@PathVariable Long id) {
        String username = getCurrentUsername();
        log.info("Запрос на пролонгацию билета ID: {} от пользователя: {}", id, username);

        TicketResponse response = ticketService.prolongTicket(id, username);
        return ResponseEntity.ok(response);
    }

    /**
     * Вспомогательный метод: получение имени текущего аутентифицированного пользователя
     * TODO: логировать в консоль
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("Пользователь не аутентифицирован");
        }
        return authentication.getName();
    }
}