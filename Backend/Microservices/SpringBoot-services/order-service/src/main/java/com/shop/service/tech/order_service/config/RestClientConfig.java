package com.shop.service.tech.order_service.config;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.shop.service.tech.order_service.client.feignClient.inevntory.IInventoryClient;

@Configuration
public class RestClientConfig {

    private static final Logger log = LoggerFactory.getLogger(RestClientConfig.class);

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    @Bean
    public IInventoryClient inventoryClient() {

        log.info("Creating RestClient for Inventory Service with base URL: {}", inventoryServiceUrl);

        var restClient = RestClient.builder()
                .requestFactory(clientHttpRequestFactory())
                .baseUrl(inventoryServiceUrl)
                .build();

        log.info("RestClient for Inventory Service created successfully");

        var restClientAdapter = RestClientAdapter.create(restClient);

        log.info("RestClientAdapter created for Inventory Service");

        var httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();

        log.info("HttpServiceProxyFactory created for Inventory Service");

        return httpServiceProxyFactory
                .createClient(IInventoryClient.class);
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {

        log.info("Configuring RestClient with custom timeouts");

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(5));

        log.info("RestClient configured with connect timeout: ms and read timeout: ms");

        return factory;
    }

}
