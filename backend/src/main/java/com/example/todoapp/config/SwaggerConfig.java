package com.example.todoapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Swagger / OpenAPI pour la documentation de l'API.
 *
 * - Définit le titre et la version de l'API.
 * - Ajoute la sécurité JWT (Bearer) pour tous les endpoints.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Bean OpenAPI personnalisé.
     * Définit les informations de base et la configuration de sécurité.
     *
     * @return OpenAPI configuré
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // Informations générales sur l'API
                .info(new Info()
                        .title("Priorito")
                        .version("1.0")
                )
                // Déclare la sécurité pour Swagger UI
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT") // format token attendu
                        )
                );
    }
}
