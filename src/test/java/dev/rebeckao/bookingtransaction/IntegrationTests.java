package dev.rebeckao.bookingtransaction;

import dev.rebeckao.bookingtransaction.model.TransactionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collections;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class IntegrationTests {
    @LocalServerPort
    private int port;

    @Test
    void processSuccessfulAndFailedTransactions() {
        WebTestClient client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();

        client.post()
                .uri("/process-transactions")
                .bodyValue("")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(TransactionResponse.class)
                .isEqualTo(new TransactionResponse(Collections.emptyList()));

    }
}
