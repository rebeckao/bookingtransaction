package dev.rebeckao.bookingtransaction;

import dev.rebeckao.bookingtransaction.model.RejectedTransaction;
import dev.rebeckao.bookingtransaction.persistence.UserEntity;
import dev.rebeckao.bookingtransaction.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BookingTransactionService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Mono<RejectedTransaction> parseAndProcessTransaction(String rawTransaction) {
        String[] transactionParts = rawTransaction.replace("\"", "").split(",");
        if (transactionParts.length != 5) {
            throw new IllegalArgumentException("Invalid input: " + rawTransaction);
        }
        String firstName = transactionParts[0];
        String lastName = transactionParts[1];
        String emailId = transactionParts[2];
        int cost = Integer.parseInt(transactionParts[3]);
        String transactionId = transactionParts[4];

        return userRepository.processTransaction(emailId, cost, firstName, lastName, transactionId);
    }

    public Flux<UserEntity> getPersistedData() {
        return userRepository.getPersistedData();
    }

    public Mono<Void> clearPersistedData() {
        return userRepository.clearPersistedData();
    }

    public Mono<UserEntity> setCreditLimit(UserEntity userEntity) {
        return userRepository.setCreditLimit(userEntity);
    }
}
