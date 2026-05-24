package com.shop.service.tech.api_gateway.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;

@Configuration
public class RouterGateway {

        private static final Logger LOGGER = LoggerFactory.getLogger(RouterGateway.class);

        @Value("${product.service.url}")
        private String PROD_SERVICE_URL;

        @Value("${order.service.url}")
        private String ORD_SERVICE_URL;

        @Value("${inventory.service.url}")
        private String INV_SERVICE_URL;

        @Value("${product.service.predicate}")
        private String PROD_SERVICE_ROUTE;

        @Value("${order.service.predicate}")
        private String ORD_SERVICE_ROUTE;

        @Value("${inventory.service.predicate}")
        private String INV_SERVICE_ROUTE;

        @Value("${springdoc.swagger-ui.urls[0].url}")
        private String PROD_SERVICE_SWAGGER_ROUTE_URL;

        @Value("${springdoc.swagger-ui.urls[1].url}")
        private String ORD_SERVICE_SWAGGER_ROUTE_URL;

        @Value("${springdoc.swagger-ui.urls[2].url}")
        private String INV_SERVICE_SWAGGER_ROUTE_URL;

        @Bean
        public RouterFunction<ServerResponse> crudRouter() {

                // Log injected URLs and routes to verify @Value injection
                LOGGER.info("PROD_SERVICE_URL={}", PROD_SERVICE_URL);
                LOGGER.info("ORD_SERVICE_URL={}", ORD_SERVICE_URL);
                LOGGER.info("INV_SERVICE_URL={}", INV_SERVICE_URL);
                LOGGER.info("PROD_SERVICE_ROUTE={}", PROD_SERVICE_ROUTE);
                LOGGER.info("ORD_SERVICE_ROUTE={}", ORD_SERVICE_ROUTE);
                LOGGER.info("INV_SERVICE_ROUTE={}", INV_SERVICE_ROUTE);
                LOGGER.info("PROD_SERVICE_SWAGGER_ROUTE_URL={}", PROD_SERVICE_SWAGGER_ROUTE_URL);
                LOGGER.info("ORD_SERVICE_SWAGGER_ROUTE_URL={}", ORD_SERVICE_SWAGGER_ROUTE_URL);
                LOGGER.info("INV_SERVICE_SWAGGER_ROUTE_URL={}", INV_SERVICE_SWAGGER_ROUTE_URL);

                RouterFunction<ServerResponse> prodService = route("product_service")
                                .route(
                                        RequestPredicates.path(PROD_SERVICE_ROUTE)
                                        .or(RequestPredicates.path(PROD_SERVICE_SWAGGER_ROUTE_URL)), // Add Swagger docs route
                                        http())
                                .before(uri(PROD_SERVICE_URL))
                                .filter(circuitBreakerFilter("product_service_fallback")) // Add circuit breaker filter
                                .filter(this::doFilterProcessing)
                                .build();

                RouterFunction<ServerResponse> ordService = route("order_service")
                                .route(
                                        RequestPredicates.path(ORD_SERVICE_ROUTE)
                                        .or(RequestPredicates.path(ORD_SERVICE_SWAGGER_ROUTE_URL)), // Add Swagger docs route
                                        http())
                                .before(uri(ORD_SERVICE_URL))
                                .filter(circuitBreakerFilter("order_service_fallback")) // Add circuit breaker filter
                                .filter(this::doFilterProcessing) // Use method reference for pre-processing
                                .build();

                RouterFunction<ServerResponse> invService = route("inventory_service")
                                .route(
                                        RequestPredicates.path(INV_SERVICE_ROUTE)
                                        .or(RequestPredicates.path(INV_SERVICE_SWAGGER_ROUTE_URL)), // Add Swagger docs route
                                        http())
                                .before(uri(INV_SERVICE_URL))
                                .filter(circuitBreakerFilter("inventory_service_fallback")) // Add circuit breaker filter
                                .filter(this::doFilterProcessing) // Use method reference for pre-processing
                                .build();

                RouterFunction<ServerResponse> fallBackRoute = route("fallback_route")
                                .GET("/fallbackRoute",
                                        request -> ServerResponse
                                                .status(HttpStatus.SERVICE_UNAVAILABLE)
                                                .body("Service is currently unavailable. Please try again later."))
                                .build();

                RouterFunction<ServerResponse> defaultRoute = route("default_route")
                                .route(RequestPredicates.all(), http())
                                .filter(circuitBreakerFilter("default_route_fallback")) // Add circuit breaker filter
                                .before(this::doBeforeProcessing)
                                .GET("/**", request -> ServerResponse
                                        .status(HttpStatus.NOT_FOUND)
                                        .body("The requested resource was not found on the server."))
                                .build();

                return prodService.and(ordService).and(invService).and(fallBackRoute).and(defaultRoute);
        }


        private ServerRequest doBeforeProcessing(ServerRequest request) {
                // Example pre-processing logic (e.g., logging)
                LOGGER.info("Incoming request: {} {}", request.method(), request.uri());
                // You can add more complex logic here (e.g., authentication, header manipulation, etc.)

                request.attributes().put("preProcessed", true); // Example of adding an attribute to the request
                request.cookies().forEach((key, value) -> LOGGER.debug("Cookie: {}={}", key, value)); // Log cookies for debugging
                request.pathVariables().forEach((key, value) -> LOGGER.debug("Path Variable: {}={}", key, value)); // Log path variables for debugging
                request.attributes().forEach((key, value) -> LOGGER.debug("Attribute: {}={}", key, value)); // Log attributes for debugging

                return request;        
        }

        private ServerResponse doFilterProcessing(ServerRequest request, HandlerFunction<ServerResponse> next) throws Exception {
                // Example pre-processing logic (e.g., logging)
                LOGGER.info("Incoming request: {} {}", request.method(), request.uri());
                // You can add more complex logic here (e.g., authentication, header manipulation, etc.)

                request.attributes().put("preProcessed", true); // Example of adding an attribute to the request
                request.cookies().forEach((key, value) -> LOGGER.debug("Cookie: {}={}", key, value)); // Log cookies for debugging
                request.pathVariables().forEach((key, value) -> LOGGER.debug("Path Variable: {}={}", key, value)); // Log path variables for debugging
                request.attributes().forEach((key, value) -> LOGGER.debug("Attribute: {}={}", key, value)); // Log attributes for debugging
                
                return next.handle(request);
        }

        private HandlerFilterFunction<ServerResponse, ServerResponse> circuitBreakerFilter(String routerName) {
                LOGGER.info("Creating circuit breaker filter for route: {}", routerName);
                return CircuitBreakerFilterFunctions.circuitBreaker(routerName, URI.create("forward:/fallbackRoute"));
        }

}