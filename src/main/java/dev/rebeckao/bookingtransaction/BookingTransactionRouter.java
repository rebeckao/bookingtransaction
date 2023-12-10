package dev.rebeckao.bookingtransaction;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration(proxyBeanMethods = false)
public class BookingTransactionRouter {
    @Bean
    public RouterFunction<ServerResponse> router(BookingTransactionHandler bookingTransactionHandler) {

        return route(POST("/process-transactions").and(accept(MediaType.TEXT_PLAIN)), bookingTransactionHandler::process)
                .andRoute(GET("/persisted-data"), bookingTransactionHandler::getPersistedData)
                .andRoute(DELETE("/persisted-data"), bookingTransactionHandler::clearPersistedData)
                .andRoute(PUT("/set-credit-limits").and(accept(MediaType.APPLICATION_JSON)), bookingTransactionHandler::setCreditLimits);
    }
}
