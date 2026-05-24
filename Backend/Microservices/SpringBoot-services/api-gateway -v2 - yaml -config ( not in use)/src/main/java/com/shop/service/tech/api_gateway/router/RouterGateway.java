package com.shop.service.tech.api_gateway.router;

public class RouterGateway {

/*    @Bean
    public RouterFunction<ServerResponse> router() {

        RouterFunction<ServerResponse> prodService = route("product_service")
                        .route(
                                RequestPredicates.path("/api/product/**"),
                                http()
                        )
                        .before(uri("http://localhost:1000"))
                        .build();

        RouterFunction<ServerResponse> ordService = route("order_service")
                        .route(
                                RequestPredicates.path("/api/order/**"),
                                http()
                        )
                        .before(uri("http://localhost:1005"))
                        .build();

        RouterFunction<ServerResponse> invService = route("inventory_service")
                        .route(
                                RequestPredicates.path("/api/inventory/**"),
                                http()
                        )
                        .before(uri("http://localhost:1025"))
                        .build();

        return prodService.and(ordService).and(invService);
    } 
*/

}