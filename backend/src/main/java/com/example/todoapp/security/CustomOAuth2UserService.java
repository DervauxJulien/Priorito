package com.example.todoapp.security;

import com.example.todoapp.model.Role;
import com.example.todoapp.model.User;
import com.example.todoapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Service personnalisé pour récupérer les informations d'un utilisateur via OAuth2.
 * Étend DefaultOAuth2UserService pour gérer la logique de création ou récupération
 * de l'utilisateur dans la base de données.
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Charge un utilisateur OAuth2 à partir de la requête.
     * Si l'utilisateur n'existe pas en base, il est créé avec un rôle USER.
     *
     * @param userRequest la requête OAuth2
     * @return OAuth2User représentant l'utilisateur authentifié
     * @throws OAuth2AuthenticationException si l'authentification échoue
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Récupération des informations brutes de l'utilisateur via le service parent
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Extraction de l'email et du nom depuis les attributs OAuth2
        String email = oAuth2User.getAttribute("email");
        String username = oAuth2User.getAttribute("name");

        // Vérifie si l'utilisateur existe en base, sinon le crée
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setRole(Role.USER); // rôle par défaut pour les utilisateurs OAuth2
            return userRepository.save(newUser);
        });

        // Retourne un DefaultOAuth2User avec les autorités de l'utilisateur
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())), // rôle utilisateur
                oAuth2User.getAttributes(), // attributs OAuth2
                "email" // attribut principal utilisé comme identifiant
        );
    }
}
