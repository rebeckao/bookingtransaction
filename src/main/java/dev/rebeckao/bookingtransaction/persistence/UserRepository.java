package dev.rebeckao.bookingtransaction.persistence;

import dev.rebeckao.bookingtransaction.model.RejectedTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class UserRepository {

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<RejectedTransaction> getRejectedTransactionMono(String emailId, int cost, String firstName, String lastName, String transactionId) {
        Query query = Query.query(Criteria
                .where("emailId").is(emailId)
                .and("creditLimit").gte(cost));

        Update update = new Update()
                .inc("creditLimit", -cost);

        return reactiveMongoTemplate.updateFirst(query, update, UserEntity.class)
                .flatMap(updateResult -> {
                    if (updateResult.getMatchedCount() > 0) {
                        return Mono.empty();
                    } else {
                        return Mono.just(new RejectedTransaction(firstName, lastName, emailId, transactionId));
                    }
                });
    }

    public Flux<UserEntity> getPersistedData() {
        return reactiveMongoTemplate.findAll(UserEntity.class);
    }

    public Mono<Void> clearPersistedData() {
        return reactiveMongoTemplate.dropCollection(UserEntity.class);
    }

    public Mono<UserEntity> setCreditLimit(String emailId, Integer creditLimit) {
        return reactiveMongoTemplate.insert(new UserEntity(emailId, creditLimit));
    }
}
