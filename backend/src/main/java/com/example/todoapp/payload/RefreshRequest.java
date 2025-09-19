package com.example.todoapp.payload;

/**
 * DTO pour la requête de refresh token.
 * Contient uniquement le refreshToken envoyé par le client
 * afin de générer un nouveau JWT et un nouveau refresh token.
 */
public class RefreshRequest {
    private String refreshToken;

    public String getRefreshToken() { return refreshToken; }

    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
