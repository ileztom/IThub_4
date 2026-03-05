package com.library.loan.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Loan Service API")
                        .description("Сервис учёта выдачи книг. Выдача книг читателям, возврат, " +
                                "просмотр активных и просроченных выдач. Автоматически отправляет " +
                                "события в RabbitMQ для уведомлений.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Library System")
                                .email("admin@library.ru")))
                .servers(List.of(
                        new Server().url("http://localhost:8083").description("Direct"),
                        new Server().url("http://localhost:8080").description("Via API Gateway")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME, new SecurityScheme()
                                .name(SECURITY_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT токен. Получите через POST /api/auth/login на user-service")));
    }
}
