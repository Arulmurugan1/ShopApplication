package com.shop.service.tech.inventory_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfiguration {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(
                    new Info()
                    .title("Inventory Service API")
                    .version("1.0")
                    .description("API documentation for the Inventory Service")
                    .license(
                        new License().name("Apache config 2.0")
                    )
                )
                .externalDocs(
                    new ExternalDocumentation()
                    .description("Inventory Service Documentation")
                    .url("https://your-documentation-url.com")
                );
    }
}
