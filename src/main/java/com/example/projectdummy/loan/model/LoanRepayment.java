package com.example.projectdummy.loan.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LoanRepayment {
    private Long loanRepaymentId;
    private Long accountId;
    private Long totalDue;
    private Long principal;
    private Long interest;
    private LocalDate dueAt;
    private LocalDate finalAt;
    private String dueCode;
}
