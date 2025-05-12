package com.example.projectdummy.loan.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class LoanAccount {
    private Long accountId;
    private Long loanMoney;
    private BigDecimal discountedRate;
    private BigDecimal additionalRate;
    private Long rateId;
    private LocalDate endAt;
    private String redemptionCode;
    private Long loanApplicationId;
}
