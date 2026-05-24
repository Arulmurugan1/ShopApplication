package com.shop.service.tech.api_gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityChain {
    
    private static final String[] SWAGGER_WHITELIST = {
        "/fallbackRoute/**",
        "/product/api-docs/**",
        "/order/api-docs/**",
        "/inventory/api-docs/**",
        "/gateway/api-docs/**",
        "/gateway/actuator/**",
        "/gateway/swagger-ui/**",
        "/gateway/swagger-ui.html"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception 
    {
        return http
                .authorizeHttpRequests(auth -> 
                    auth
                    .requestMatchers(SWAGGER_WHITELIST)
                    .permitAll()
                    .anyRequest()
                    .authenticated())
                .oauth2ResourceServer(oauth2 -> 
                    oauth2.jwt(Customizer.withDefaults()))
                .build();
    }

}
