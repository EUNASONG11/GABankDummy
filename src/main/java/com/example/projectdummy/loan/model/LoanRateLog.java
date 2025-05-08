package com.example.projectdummy.loan.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class LoanRateLog {
    private Long loanRateLogId;
    private Long accountId;
    private BigDecimal interestRate;
    private LocalDate changeAt;
    private LocalDate createdAt;
}
