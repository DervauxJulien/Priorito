package com.example.todoapp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre de sécurité qui intercepte chaque requête HTTP pour vérifier
 * la présence et la validité d'un token JWT.
 *
 * Si le JWT est valide, il renseigne le SecurityContext avec l'utilisateur authentifié.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils; // utilitaire pour manipuler les JWT

    @Autowired
    private UserDetailsServiceImpl userDetailsService; // service pour charger l'utilisateur depuis la DB

    /**
     * Intercepte chaque requête HTTP.
     * Vérifie le header Authorization pour récupérer le JWT.
     * Si le JWT est valide, crée une authentification Spring Security.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Récupère le header Authorization
        String authHeader = request.getHeader("Authorization");
        System.out.println("Auth header: " + authHeader);

        String username = null;
        String token = null;

        // Vérifie que le header commence par "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // supprime "Bearer " pour ne garder que le token
            username = jwtUtils.getUsernameFromJwtToken(token); // extrait le username depuis le JWT
            System.out.println("Username from token: " + username);
        }

        // Si on a un username et qu'aucune authentification n'est encore définie
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Charge l'utilisateur depuis la base de données
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            System.out.println("UserDetails loaded: " + (userDetails != null));

            // Valide le token
            if (jwtUtils.validateJwtToken(token)) {
                // Crée une authentification Spring Security
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                // Définit l'utilisateur authentifié dans le contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("Authentication set in context");
            } else {
                System.out.println("JWT invalid");
            }
        }

        // Passe la requête au filtre suivant
        filterChain.doFilter(request, response);
    }

}
