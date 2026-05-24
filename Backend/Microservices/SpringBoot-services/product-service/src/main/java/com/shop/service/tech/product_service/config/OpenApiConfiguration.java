package com.shop.service.tech.product_service.config;

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
                    .title("Product Service API")
                    .version("1.0")
                    .description("API documentation for the Product Service")
                    .license(
                        new License().name("Apache config 2.0")
                    )
                )
                .externalDocs(
                    new ExternalDocumentation()
                    .description("Product Service Documentation")
                    .url("https://your-documentation-url.com")
                );
    }
}
