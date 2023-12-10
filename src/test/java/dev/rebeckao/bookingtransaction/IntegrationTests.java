package dev.rebeckao.bookingtransaction;

import dev.rebeckao.bookingtransaction.model.FailedTransaction;
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

        for (String emailId : List.of("john@doe.com", "john@doe1.com", "john@doe2.com")) {
            client.put()
                    .uri("/set-credit-limit/" + emailId)
                    .bodyValue(200)
                    .exchange();
        }

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
                        new FailedTransaction("John", "Doe2", "john@doe2.com", "TR0003"),
                        new FailedTransaction("John", "Doe", "john@doe.com", "TR0005")
                )));

    }
}
