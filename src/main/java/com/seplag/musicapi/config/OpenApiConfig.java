package com.seplag.musicapi.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Music API", version = "1.0", description = "API REST para gerenciamento de Artistas e Álbuns - SEPLAG MT", contact = @Contact(name = "SEPLAG", email = "contato@seplag.mt.gov.br")), servers = {
        @Server(url = "http://localhost:8080", description = "Servidor de Desenvolvimento")
})
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", description = "Token JWT para autenticação. Use o endpoint /api/v1/auth/login para obter o token.")
public class OpenApiConfig {
}
