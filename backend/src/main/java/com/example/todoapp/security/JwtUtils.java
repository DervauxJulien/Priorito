package com.example.todoapp.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Classe utilitaire pour gérer la création, la lecture et la validation des JWT.
 * Elle centralise toute la logique liée aux tokens pour l'authentification.
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret; // clé secrète pour signer et vérifier les JWT

    @Value("${jwt.expiration}")
    private long jwtExpirationMs; // durée de validité d’un token en millisecondes

    /**
     * Génère un JWT pour un utilisateur donné avec son rôle.
     *
     * @param username l'identifiant unique de l'utilisateur
     * @param role     le rôle de l'utilisateur (USER ou ADMIN)
     * @return le token JWT signé
     */
    public String generateJwtToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username) // identifiant de l’utilisateur
                .claim("role", role)  // rôle injecté dans le JWT
                .setIssuedAt(new Date()) // date de création
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // date d'expiration
                .signWith(SignatureAlgorithm.HS512, jwtSecret) // signature HMAC SHA-512
                .compact();
    }

    /**
     * Extrait le username depuis le JWT.
     *
     * @param token le JWT
     * @return le username contenu dans le token
     */
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Valide un JWT : signature correcte et pas expiré.
     *
     * @param token le JWT
     * @return true si valide, false sinon
     */
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            System.out.println("JWT error: " + e.getMessage()); // log simple pour debug
        }
        return false;
    }
}
