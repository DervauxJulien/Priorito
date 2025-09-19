package com.example.todoapp.payload;

/**
 * DTO représentant la réponse après une tentative de connexion réussie.
 * Contient le token d'accès, le refresh token, le nom d'utilisateur et le rôle.
 */
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private String username;
    private String role;

    public LoginResponse(String accessToken, String refreshToken, String username, String role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.username = username;
        this.role = role;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
