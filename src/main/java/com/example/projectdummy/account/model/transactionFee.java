package com.example.projectdummy.account.model;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class transactionFee {
    private Long transactionFeeId;
    private int fee;
    private String reason;
}
