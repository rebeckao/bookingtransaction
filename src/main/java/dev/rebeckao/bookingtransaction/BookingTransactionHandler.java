package dev.rebeckao.bookingtransaction;

import dev.rebeckao.bookingtransaction.model.TransactionResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
public class BookingTransactionHandler {
    public Mono<ServerResponse> process(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new TransactionResponse(Collections.emptyList())));
    }
}
