package com.example.todoapp.security;

import com.example.todoapp.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Implémentation de UserDetails pour Spring Security.
 *
 * Permet à Spring Security de récupérer les informations nécessaires
 * sur l'utilisateur authentifié (username, mot de passe, rôles, etc.).
 */
public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    /**
     * Retourne la liste des rôles/granted authorities de l'utilisateur.
     * Spring Security utilise cette info pour gérer les autorisations.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Prefixe "ROLE_" nécessaire pour Spring Security
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // Les méthodes ci-dessous permettent de dire que le compte est actif
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    /**
     * Retourne l'ID de l'utilisateur, utile pour récupérer ses données en DB.
     */
    public Long getId() { return user.getId(); }
}
