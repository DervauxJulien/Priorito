package com.example.todoapp.repository;

import com.example.todoapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository pour l'entité User.
 * Fournit des méthodes pour gérer la persistance des utilisateurs
 * et effectuer des recherches spécifiques par username, email ou refresh token.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Recherche un utilisateur par son username.
     * @param username le nom d'utilisateur
     * @return un Optional contenant l'utilisateur si trouvé
     */
    Optional<User> findByUsername(String username);

    /**
     * Vérifie si un username existe déjà en base.
     * @param username le nom d'utilisateur à vérifier
     * @return true si le username existe, false sinon
     */
    boolean existsByUsername(String username);

    /**
     * Vérifie si un email existe déjà en base.
     * @param email l'email à vérifier
     * @return true si l'email existe, false sinon
     */
    boolean existsByEmail(String email);

    /**
     * Recherche un utilisateur par son refresh token.
     * @param refreshToken le token de rafraîchissement
     * @return un Optional contenant l'utilisateur si trouvé
     */
    Optional<User> findByRefreshToken(String refreshToken);

    /**
     * Recherche un utilisateur par son email.
     * @param email l'email de l'utilisateur
     * @return un Optional contenant l'utilisateur si trouvé
     */
    Optional<User> findByEmail(String email);
}
