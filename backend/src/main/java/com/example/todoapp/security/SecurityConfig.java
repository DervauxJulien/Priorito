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
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // prévol CORS
                        // Swagger et ressources publiques
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        // endpoints login/signup classiques accessibles sans token
                        .requestMatchers("/api/auth/**").permitAll()
                        // endpoints OAuth2 (Google) publics
                        .requestMatchers("/oauth2/**").permitAll()
                        // toutes les autres requêtes nécessitent un JWT
                        .anyRequest().authenticated()
                )
                // ✅ on configure OAuth2, mais ça ne s’active que si tu appelles volontairement /oauth2/**
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/google")
                        .userInfoEndpoint().userService(customOAuth2UserService)
                        .and()
                        .successHandler(oAuth2SuccessHandler)
                )
                // JWT = pas de session côté serveur
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Filtre JWT avant UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configuration CORS pour autoriser les requêtes frontend
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
