package com.example.todoapp.payload;

/**
 * DTO représentant la requête de connexion.
 * Contient le nom d'utilisateur et le mot de passe fournis par le client.
 */
public class LoginRequest {
    private String username;
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
