package com.shop.service.tech.order_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.shop.service.tech.order_service.client.feignClient.inevntory.IInventoryClient;

@Configuration
public class RestClientConfig {
    
    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    @Bean
    public IInventoryClient inventoryClient() {
        
        var restClient = RestClient.builder()
                            .baseUrl(inventoryServiceUrl)
                            .build();   

        var restClientAdapter = RestClientAdapter.create(restClient);
        var httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();

        return httpServiceProxyFactory
                .createClient(IInventoryClient.class);
    }

}
