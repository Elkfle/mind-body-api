package com.grupo1.mindbody.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI mindBodyOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Mind&Body API")
                .description("API REST para la plataforma de actividades deportivas universitarias")
                .version("v1")
                .contact(new Contact()
                    .name("Grupo 1 — 1ACC0236-202610")
                    .email("corloscalfer@gmail.com")))
            .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Ingresa el access token obtenido en /api/v1/auth/sign-in")));
    }
}
