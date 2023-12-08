package dev.rebeckao.bookingtransaction.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Transaction(
        @JsonProperty("First Name")
        String firstName,
        @JsonProperty("Last Name")
        String lastName,
        @JsonProperty("Email Id")
        String emailId,
        @JsonProperty("Transaction Number")
        String transactionNumber
) {
}
