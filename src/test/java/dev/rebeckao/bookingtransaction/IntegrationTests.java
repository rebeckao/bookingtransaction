package dev.rebeckao.bookingtransaction;

import dev.rebeckao.bookingtransaction.model.Transaction;
import dev.rebeckao.bookingtransaction.model.TransactionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

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
                .bodyValue("""
                        "John,Doe,john@doe.com,190,TR0001"
                        "John,Doe1,john@doe1.com,200,TR0001"
                        "John,Doe2,john@doe2.com,201,TR0003"
                        "John,Doe,john@doe.com,9,TR0004"
                        "John,Doe,john@doe.com,2,TR0005"
                        """)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(TransactionResponse.class)
                .isEqualTo(new TransactionResponse(List.of(
                        new Transaction("John", "Doe2", "john@doe2.com", "TR0003"),
                        new Transaction("John", "Doe", "john@doe.com", "TR0005")
                )));

    }
}
