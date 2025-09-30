package com.example.todoapp.service;

import com.example.todoapp.model.User;
import com.example.todoapp.payload.LoginRequest;
import com.example.todoapp.payload.LoginResponse;
import com.example.todoapp.payload.RefreshRequest;
import com.example.todoapp.payload.SignupRequest;
import com.example.todoapp.repository.UserRepository;
import com.example.todoapp.security.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final MailService mailService;

    @Value("${FRONTEND_URL}")
    private String frontendUrl;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils,
                       MailService mailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.mailService = mailService;
    }

    public String signup(SignupRequest req) {
        if (userRepository.existsByUsername(req.getUsername()) || userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Identifiants déjà incorrect");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        userRepository.save(user);

        // Envoi email vérification
        String token = jwtUtils.generateTemporaryToken(user.getUsername());
        String link = frontendUrl + "/verify-email?token=" + token;
        mailService.sendEmail(user.getEmail(), "Vérifiez votre email",
                "Cliquez sur ce lien pour vérifier votre email (15min) :\n" + link);

        return "Utilisateur créé, vérifiez votre email.";
    }

    public LoginResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        User user = userRepository.findByUsername(req.getUsername()).orElseThrow();
        String role = user.getRole().name();
        String accessToken = jwtUtils.generateJwtToken(user.getUsername(), role);
        String refreshToken = UUID.randomUUID().toString();

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new LoginResponse(accessToken, refreshToken, user.getUsername(), role);
    }

    public LoginResponse refresh(RefreshRequest req) {
        User user = userRepository.findByRefreshToken(req.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token invalide"));

        String role = user.getRole().name();
        String accessToken = jwtUtils.generateJwtToken(user.getUsername(), role);
        String newRefreshToken = UUID.randomUUID().toString();

        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return new LoginResponse(accessToken, newRefreshToken, user.getUsername(), role);
    }

    public String verifyEmail(String token) {
        if (!jwtUtils.validateJwtToken(token)) {
            throw new RuntimeException("Token invalide ou expiré");
        }
        String username = jwtUtils.getUsernameFromJwtToken(token);
        User user = userRepository.findByUsername(username).orElseThrow();
        user.setEnabled(true);
        userRepository.save(user);
        return "Email vérifié !";
    }

    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        String token = jwtUtils.generateTemporaryToken(user.getUsername());
        String link = frontendUrl + "/reset-password?token=" + token;

        mailService.sendEmail(user.getEmail(), "Réinitialisation du mot de passe",
                "Cliquez ici (15min valide) :\n" + link);

        return "Email de réinitialisation envoyé.";
    }

    public String resetPassword(String token, String newPassword) {
        if (!jwtUtils.validateJwtToken(token)) {
            throw new RuntimeException("Token invalide ou expiré");
        }
        String username = jwtUtils.getUsernameFromJwtToken(token);
        User user = userRepository.findByUsername(username).orElseThrow();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "Mot de passe réinitialisé.";
    }
}

