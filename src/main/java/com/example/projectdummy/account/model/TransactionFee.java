package com.example.projectdummy.account.model;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class TransactionFee {
    private Long transactionFeeId;
    private int fee;
    private String reason;
}
