package com.example.todoapp.controller;

import com.example.todoapp.model.User;
import com.example.todoapp.payload.*;
import com.example.todoapp.repository.UserRepository;
import com.example.todoapp.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Crée un nouvel utilisateur.
     *
     * Vérifie si le username ou l'email existe déjà avant création.
     *
     * @param req données de l'utilisateur à créer (username, email, password)
     * @return message de succès ou d'erreur
     */
    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest req) {
        if (userRepository.existsByUsername(req.getUsername()))
            return "Erreur: Identifiants déjà utilisés";
        if (userRepository.existsByEmail(req.getEmail()))
            return "Erreur: Identifiants déjà utilisés";

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        // Password encodé avant sauvegarde pour sécurité
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        userRepository.save(user);

        return "Utilisateur créé avec succès!";
    }

    /**
     * Authentifie un utilisateur et génère un access + refresh token.
     *
     * @param req loginRequest contenant username et password
     * @return LoginResponse avec accessToken, refreshToken, username et rôle
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        // Vérifie les identifiants
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        User user = userRepository.findByUsername(req.getUsername()).orElseThrow();

        String role = user.getRole().name(); // ADMIN ou USER
        String accessToken = jwtUtils.generateJwtToken(user.getUsername(), role);
        String refreshToken = UUID.randomUUID().toString();

        // Sauvegarde le refresh token pour future utilisation
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new LoginResponse(accessToken, refreshToken, user.getUsername(), role);
    }

    /**
     * Rafraîchit le token d'un utilisateur en utilisant un refreshToken valide.
     *
     * @param req RefreshRequest contenant le refreshToken
     * @return nouveau LoginResponse avec accessToken et refreshToken
     */
    @PostMapping("/refresh")
    public LoginResponse refresh(@RequestBody RefreshRequest req) {
        User user = userRepository.findByRefreshToken(req.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token invalide"));

        String role = user.getRole().name();
        String accessToken = jwtUtils.generateJwtToken(user.getUsername(), role);
        String newRefreshToken = UUID.randomUUID().toString();

        // Remplace l'ancien refresh token par le nouveau
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return new LoginResponse(accessToken, newRefreshToken, user.getUsername(), role);
    }
}


