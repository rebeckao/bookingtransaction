package dev.rebeckao.bookingtransaction;

import dev.rebeckao.bookingtransaction.model.RejectedTransaction;
import dev.rebeckao.bookingtransaction.model.TransactionResponse;
import dev.rebeckao.bookingtransaction.persistence.UserEntity;
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

        client.delete()
                .uri("/persisted-data")
                .exchange();

        client.put()
                .uri("/set-credit-limits")
                .bodyValue(List.of(
                        new UserEntity("john@doe.com", 200),
                        new UserEntity("john@doe1.com", 200),
                        new UserEntity("john@doe2.com", 200)
                ))
                .exchange()
                .expectStatus()
                .isOk();

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
                        new RejectedTransaction("John", "Doe2", "john@doe2.com", "TR0003"),
                        new RejectedTransaction("John", "Doe", "john@doe.com", "TR0005")
                )));

    }
}
