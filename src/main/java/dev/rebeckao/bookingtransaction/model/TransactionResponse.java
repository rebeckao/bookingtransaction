package dev.rebeckao.bookingtransaction.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TransactionResponse(@JsonProperty("Rejected Transactions") List<RejectedTransaction> rejectedTransactions) {
}
