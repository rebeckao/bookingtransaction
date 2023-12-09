package dev.rebeckao.bookingtransaction;

import dev.rebeckao.bookingtransaction.model.Transaction;
import dev.rebeckao.bookingtransaction.model.TransactionResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BookingTransactionHandler {
    public Mono<ServerResponse> process(ServerRequest request) {
        return request.bodyToMono(String.class)
                .map(BookingTransactionHandler::processTransactions)
                .flatMap(it -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(new TransactionResponse(it)))
                );
    }

    private static List<Transaction> processTransactions(String rawTransactions) {
        Map<String, Integer> creditLimits = new HashMap<>();
        List<Transaction> failedTransactions = new ArrayList<>();
        for (String line : rawTransactions.split("\\r?\\n|\\r")) {
            String[] transactionParts = line.replace("\"", "").split(",");
            if (transactionParts.length != 5) {
                throw new IllegalArgumentException("Invalid input: " + line);
            }
            String emailId = transactionParts[2];
            int cost = Integer.parseInt(transactionParts[3]);
            creditLimits.putIfAbsent(emailId, 200);
            Integer currentCredit = creditLimits.get(emailId);
            if (currentCredit >= cost) {
                creditLimits.put(emailId, currentCredit - cost);
            } else {
                failedTransactions.add(new Transaction(transactionParts[0], transactionParts[1], emailId, transactionParts[4]));
            }
        }
        return failedTransactions;
    }
}
