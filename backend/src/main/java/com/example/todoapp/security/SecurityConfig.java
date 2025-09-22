package com.example.todoapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

/**
 * Configuration globale de Spring Security pour l'application.
 * Gère :
 *   - l'authentification JWT (login/signup classique)
 *   - l'authentification OAuth2 (Google)
 *   - les permissions sur les endpoints
 *   - la politique CORS
 */
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService; // service pour charger les utilisateurs
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter; // filtre JWT pour chaque requête

    /**
     * Bean AuthenticationManager pour l'authentification classique
     * (login/signup via REST)
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       PasswordEncoder passwordEncoder,
                                                       UserDetailsServiceImpl userDetailsService)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }

    /**
     * Bean pour encoder les mots de passe avec BCrypt
     * (utilisé lors de la création d'utilisateur)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuration principale des filtres Spring Security
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           CustomOAuth2UserService customOAuth2UserService,
                                           OAuth2SuccessHandler oAuth2SuccessHandler) throws Exception {

        http
                // Désactive CSRF car on utilise JWT et pas de session côté serveur
                .csrf().disable()

                // Active la config CORS pour autoriser les requêtes frontend
                .cors().configurationSource(corsConfigurationSource())
                .and()

                // Autorisations sur les endpoints
                .authorizeHttpRequests(auth -> auth
                        // Prévol CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Swagger et ressources publiques
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // Endpoints login/signup classiques accessibles sans token
                        .requestMatchers("/api/auth/**").permitAll()

                        // Endpoints OAuth2 (Google) publics
                        .requestMatchers("/oauth2/**").permitAll()

                        // Toutes les autres requêtes nécessitent un JWT
                        .anyRequest().authenticated()
                )

                // Configuration OAuth2
                // - Ne s’active que si l'utilisateur appelle volontairement /oauth2/**
                // - Déclenche OAuth2SuccessHandler après login
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/google") // page de login OAuth2
                        .userInfoEndpoint().userService(customOAuth2UserService) // récupère infos utilisateur
                        .and()
                        .successHandler(oAuth2SuccessHandler) // après login, génération JWT
                )

                // JWT = pas de session côté serveur
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Filtre JWT avant UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configuration CORS pour autoriser les requêtes frontend
     * - Autorise localhost pour dev
     * - Autorise Vercel pour prod
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:5173",
                "http://localhost:5174",
                "https://*.vercel.app",
                "https://priorito.onrender.com"
        ));
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
