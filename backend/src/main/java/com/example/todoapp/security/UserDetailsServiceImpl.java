package com.example.todoapp.security;

import com.example.todoapp.model.User;
import com.example.todoapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

/**
 * Service utilisé par Spring Security pour charger les informations d'un utilisateur
 * à partir de la base de données.
 *
 * Cette classe renvoie un UserDetailsImpl qui contient toutes les informations
 * nécessaires pour l'authentification et la gestion des rôles.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Recherche un utilisateur par son username.
     *
     * @param username le nom d'utilisateur
     * @return un objet UserDetails pour Spring Security
     * @throws UsernameNotFoundException si aucun utilisateur n'est trouvé
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username)
                );
        return new UserDetailsImpl(user); // convertit l'entité User en UserDetails pour Spring Security
    }
}
