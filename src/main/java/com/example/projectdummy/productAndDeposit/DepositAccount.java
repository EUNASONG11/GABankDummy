package com.example.projectdummy.productAndDeposit;

import lombok.Builder;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Setter
public class DepositAccount {
    private Long accountId;
    private Long depositDurationId;
    private String endAt;
    private String cancelAt;
    private BigDecimal discountedRate;
}
