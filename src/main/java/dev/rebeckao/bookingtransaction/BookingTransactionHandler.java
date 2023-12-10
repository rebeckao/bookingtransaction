package dev.rebeckao.bookingtransaction;

import dev.rebeckao.bookingtransaction.model.FailedTransaction;
import dev.rebeckao.bookingtransaction.model.TransactionResponse;
import dev.rebeckao.bookingtransaction.persistence.CustomerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class BookingTransactionHandler {
    @Autowired
    private BookingTransactionService bookingTransactionService;

    public Mono<ServerResponse> process(ServerRequest request) {
        return request.bodyToMono(String.class)
                .flatMapMany(this::processTransactions)
                .collectList()
                .flatMap(it -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(new TransactionResponse(it)))
                );
    }

    private Flux<FailedTransaction> processTransactions(String rawTransactions) {
        return Flux.fromArray(rawTransactions.split("\\r?\\n|\\r"))
                .flatMapSequential(bookingTransactionService::processTransaction)
                .mapNotNull(it -> it);
    }

    public Mono<ServerResponse> getPersistedData(ServerRequest serverRequest) {
        Flux<CustomerEntity> data = bookingTransactionService.getPersistedData();
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(data, CustomerEntity.class);
    }

    public Mono<ServerResponse> clearPersistedData(ServerRequest serverRequest) {
        return bookingTransactionService.clearPersistedData().flatMap(it -> ServerResponse.ok().body(BodyInserters.empty()));
    }

    public Mono<ServerResponse> setCreditLimit(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Integer.class)
                .flatMap(it -> bookingTransactionService.setCreditLimit(serverRequest.pathVariable("emailId"), it))
                .flatMap(it -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(it)));
    }
}
