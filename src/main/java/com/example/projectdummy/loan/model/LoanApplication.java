package com.example.projectdummy.loan.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LoanApplication {
    private Long loanApplicationId;
    private Long loanId;
    private Long custId;
    private LocalDateTime applicationDate;
    private int requestedAmount;
    private int requestedTerm;
    private String statusCode;
    private LocalDateTime decisionDate;
    private String rejectReason;
}
