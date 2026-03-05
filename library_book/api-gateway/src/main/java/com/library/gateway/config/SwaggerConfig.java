package com.library.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI gatewayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Library API Gateway")
                        .description("Единая точка входа. Выберите сервис в выпадающем списке вверху.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Library System")
                                .email("admin@library.ru")));
    }
}
