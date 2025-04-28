package com.example.projectdummy.loan.model;

import lombok.Builder;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Setter
public class LoanRateLog {
    private Long loanRateLogId;
    private Long accountId;
    private BigDecimal interestRate;
    private String changeAt;
}
