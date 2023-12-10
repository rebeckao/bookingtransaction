package dev.rebeckao.bookingtransaction;

import dev.rebeckao.bookingtransaction.model.RejectedTransaction;
import dev.rebeckao.bookingtransaction.model.TransactionResponse;
import dev.rebeckao.bookingtransaction.persistence.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.web.reactive.function.BodyInserters.empty;

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

    private Flux<RejectedTransaction> processTransactions(String rawTransactions) {
        return Flux.fromArray(rawTransactions.split("\\r?\\n|\\r"))
                .flatMapSequential(bookingTransactionService::parseAndProcessTransaction)
                .mapNotNull(it -> it);
    }

    public Mono<ServerResponse> getPersistedData(ServerRequest serverRequest) {
        Flux<UserEntity> data = bookingTransactionService.getPersistedData();
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(data, UserEntity.class);
    }

    public Mono<ServerResponse> clearPersistedData(ServerRequest serverRequest) {
        return bookingTransactionService.clearPersistedData().flatMap(it -> ServerResponse.ok().body(empty()));
    }

    public Mono<ServerResponse> setCreditLimits(ServerRequest serverRequest) {
        Flux<UserEntity> updatedUsers = serverRequest.bodyToMono(new ParameterizedTypeReference<List<UserEntity>>() {})
                .flatMapIterable(it -> it)
                .flatMap(it -> bookingTransactionService.setCreditLimit(it));
        return ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(updatedUsers, UserEntity.class);
    }
}
