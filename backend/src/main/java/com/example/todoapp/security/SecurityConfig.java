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
@EnableGlobalMethodSecurity(prePostEnabled = true) // permet @PreAuthorize dans les controllers
@Configuration
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService; // service pour charger les utilisateurs
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter; // filtre JWT pour chaque requête

    /**
     * Bean AuthenticationManager pour l'authentification classique
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

        http.csrf().disable() // désactive CSRF car on utilise JWT
                .cors().configurationSource(corsConfigurationSource()) // active la config CORS
                .and()
                .authorizeHttpRequests()
                // OPTIONS : autorisé pour tous (prévol de CORS)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // endpoints Swagger/publics
                .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/v3/api-docs.yaml",
                        "/swagger-resources/**",
                        "/webjars/**"
                ).permitAll()
                // REST classique JWT accessible publiquement : login/signup
                .requestMatchers("/api/auth/**").permitAll()
                // OAuth2 uniquement pour Google login
                .requestMatchers("/oauth2/**").permitAll()
                // toutes les autres requêtes nécessitent authentification
                .anyRequest().authenticated()
                .and()
                // ----------------- OAuth2 Login -----------------
                .oauth2Login()
                .loginPage("/oauth2/authorization/google") // seulement pour bouton Google
                .userInfoEndpoint()
                .userService(customOAuth2UserService) // récupère les infos user depuis Google
                .and()
                .successHandler(oAuth2SuccessHandler) // redirection après login OAuth2
                .and()
                // ----------------- Gestion session -----------------
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // JWT → pas de session serveur

        // ----------------- Filtre JWT -----------------
        // S'assure que le JWT est vérifié avant toute authentification standard
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configuration CORS pour autoriser les requêtes du frontend
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // autorise le frontend sur Vercel et local
        configuration.setAllowedOriginPatterns(List.of(
                "https://*.vercel.app",
                "https://priorito-git-main-dervauxjuliens-projects.vercel.app",
                "http://localhost:5173",
                "http://localhost:5174"
        ));
        // méthodes HTTP autorisées
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        // headers autorisés
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        // cookies et credentials autorisés
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // appliquer CORS à tous les endpoints
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
