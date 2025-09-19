package com.example.todoapp.payload;

/**
 * DTO représentant la réponse JWT envoyée au client après authentification.
 * Contient le token JWT, le nom d'utilisateur et le rôle de l'utilisateur.
 */
public class JwtResponse {
    private String token;
    private String username;
    private String role;

    public JwtResponse(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }

    public String getToken() { return token; }

    public String getUsername() { return username; }

    public String getRole() { return role; }
}
