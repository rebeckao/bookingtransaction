package dev.rebeckao.bookingtransaction;

import dev.rebeckao.bookingtransaction.model.RejectedTransaction;
import dev.rebeckao.bookingtransaction.persistence.CustomerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BookingTransactionService {
    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Transactional
    public Mono<RejectedTransaction> processTransaction(String rawTransaction) {
        String[] transactionParts = rawTransaction.replace("\"", "").split(",");
        if (transactionParts.length != 5) {
            throw new IllegalArgumentException("Invalid input: " + rawTransaction);
        }
        String emailId = transactionParts[2];
        int cost = Integer.parseInt(transactionParts[3]);

        Query query = Query.query(Criteria
                .where("emailId").is(emailId)
                .and("creditLimit").gte(cost));

        Update update = new Update()
                .inc("creditLimit", -cost);

        return reactiveMongoTemplate.updateFirst(query, update, CustomerEntity.class)
                .flatMap(updateResult -> {
                    if (updateResult.getMatchedCount() > 0) {
                        return Mono.empty();
                    } else {
                        return Mono.just(new RejectedTransaction(transactionParts[0], transactionParts[1], emailId, transactionParts[4]));
                    }
                });
    }

    public Flux<CustomerEntity> getPersistedData() {
        return reactiveMongoTemplate.findAll(CustomerEntity.class);
    }

    public Mono<Void> clearPersistedData() {
        return reactiveMongoTemplate.dropCollection(CustomerEntity.class);
    }

    public Mono<CustomerEntity> setCreditLimit(String emailId, Integer creditLimit) {
        return reactiveMongoTemplate.insert(new CustomerEntity(emailId, creditLimit));
    }
}
