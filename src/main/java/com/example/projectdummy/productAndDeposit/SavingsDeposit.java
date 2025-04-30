package com.example.projectdummy.productAndDeposit;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class SavingsDeposit {
    private Long saDepositId;
    private String savingCode;
    private int outFlag;
}
