package com.fiap.sishotel.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SisHotel API")
                        .description("API REST para gestão de reservas de hotel (check-in / check-out) — FIAP AOS 3ESPR CP2 2026. " +
                                "GETs são públicos; operações de escrita exigem JWT. " +
                                "Usuário padrão: admin / 123456")
                        .version("1.0"))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Obtenha o token em POST /auth/login (usuário: admin, senha: 123456)")));
    }
}
