package com.example.todoapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuration globale de Spring Security pour l'application.
 * Gère :
 *   - l'authentification JWT
 *   - l'authentification OAuth2 (Google)
 *   - les permissions sur les endpoints
 *   - la politique CORS
 */
@EnableGlobalMethodSecurity(prePostEnabled = true) // permet @PreAuthorize dans les controllers
@Configuration
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService; // chargement des utilisateurs en DB
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter; // filtre JWT pour chaque requête

    /**
     * Bean AuthenticationManager utilisé pour l'authentification classique
     * via username/password
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       PasswordEncoder passwordEncoder,
                                                       UserDetailsServiceImpl userDetailsService)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService) // service custom pour charger l’utilisateur
                .passwordEncoder(passwordEncoder)       // encoder les passwords
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
                .cors() // active la config CORS
                .and()
                .authorizeHttpRequests()
                // autorise Swagger et ressources publiques
                .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/v3/api-docs.yaml",
                        "/swagger-resources/**",
                        "/webjars/**"
                ).permitAll()
                // endpoints d'authentification publics
                .requestMatchers("/api/auth/**", "/oauth2/**").permitAll()
                // toutes les autres requêtes nécessitent authentification
                .anyRequest().authenticated()
                .and()
                .oauth2Login() // config OAuth2
                .userInfoEndpoint()
                .userService(customOAuth2UserService) // récupère les infos user depuis Google
                .and()
                .successHandler(oAuth2SuccessHandler) // redirection après login OAuth2
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // JWT → pas de session serveur

        // ajout du filtre JWT avant le filtre standard username/password
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configuration CORS pour autoriser les requêtes du frontend
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:5174",
                "https://priorito-frpizfj0j-dervauxjuliens-projects.vercel.app",
                "https://priorito-1ngvrteuv-dervauxjuliens-projects.vercel.app",
                "https://priorito.vercel.app"
        )); // front local
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS")); // méthodes HTTP autorisées
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type")); // headers autorisés
        configuration.setAllowCredentials(true); // cookies et credentials autorisés

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // appliquer CORS à tous les endpoints
        return source;
    }
}
