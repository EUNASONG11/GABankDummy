package com.example.projectdummy.loan.model;

import lombok.Builder;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Setter
public class LoanAccount {
    private Long accountId;
    private Long lonMoney;
    private BigDecimal discountedRate;
    private BigDecimal additionalRate;
    private Long rateId;
    private String endAt;
    private String redemptionCode;
}
