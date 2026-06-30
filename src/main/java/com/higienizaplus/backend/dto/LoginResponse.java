package com.higienizaplus.backend.dto;

public record LoginResponse(
        String token,
        String tipo,
        String username,
        long expiresInMs
) {
    public static LoginResponse of(String token, String username, long expiresInMs) {
        return new LoginResponse(token, "Bearer", username, expiresInMs);
    }
}
