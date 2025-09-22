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
 * Il génère un JWT et redirige le frontend avec le token.
 */
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils; // utilitaire pour générer le token JWT
    @Autowired
    private UserRepository userRepository;

    // URL du frontend, injectée depuis application.properties ou .env
    @org.springframework.beans.factory.annotation.Value("${FRONTEND_URL}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Génération du JWT
        String token = jwtUtils.generateJwtToken(user.getUsername(), user.getRole().name());

        // Redirection vers le frontend avec token en query param
        response.sendRedirect(frontendUrl + "/oauth2/redirect?token=" + token);
    }
}
