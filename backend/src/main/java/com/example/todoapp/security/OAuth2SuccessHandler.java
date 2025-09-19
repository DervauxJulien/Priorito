package com.example.todoapp.security;

import com.example.todoapp.model.User;
import com.example.todoapp.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * Handler déclenché après un login OAuth2 réussi.
 * Il génère un JWT pour l’utilisateur et redirige le client frontend
 * vers l’URL de redirection avec le token en paramètre.
 */
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils; // utilitaire pour créer le token JWT
    @Autowired
    private UserRepository userRepository; // accès aux utilisateurs en base

    /**
     * Méthode appelée après une authentification OAuth2 réussie.
     * @param request requête HTTP
     * @param response réponse HTTP
     * @param authentication objet Authentication Spring contenant l’utilisateur OAuth2
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // Récupération de l’utilisateur OAuth2 authentifié
        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();

        // Récupération de l’email depuis le compte OAuth2
        String email = oauthUser.getAttribute("email");

        // Recherche de l’utilisateur correspondant en base
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Génération d’un JWT pour cet utilisateur
        String token = jwtUtils.generateJwtToken(user.getUsername(), user.getRole().name());

        // Redirection du frontend avec le token en query param
        response.sendRedirect("http://localhost:5173/oauth2/redirect?token=" + token);
    }
}
