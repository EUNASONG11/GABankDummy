package com.example.projectdummy.loan.model;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class LoanRepayment {
    private Long loanRepaymentId;
    private Long accountId;
    private Long totalDue;
    private String dueAt;
    private String dueCode;
}
