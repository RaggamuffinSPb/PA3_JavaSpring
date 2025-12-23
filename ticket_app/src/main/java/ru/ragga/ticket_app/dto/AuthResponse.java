package ru.ragga.ticket_app.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String type = "Bearer";

    public AuthResponse(String token) {
        this.token = token;
    }
}