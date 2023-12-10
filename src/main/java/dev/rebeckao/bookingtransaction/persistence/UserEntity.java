package dev.rebeckao.bookingtransaction.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document("Users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public @Data class UserEntity implements Serializable {
    @Id
    private String emailId;
    private int creditLimit;
}
