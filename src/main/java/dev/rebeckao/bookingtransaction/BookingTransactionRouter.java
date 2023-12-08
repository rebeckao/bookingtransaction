package dev.rebeckao.bookingtransaction;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration(proxyBeanMethods = false)
public class BookingTransactionRouter {
    @Bean
    public RouterFunction<ServerResponse> route(BookingTransactionHandler bookingTransactionHandler) {

        return RouterFunctions
                .route(POST("/process-transactions").and(accept(MediaType.TEXT_PLAIN)), bookingTransactionHandler::process);
    }
}
