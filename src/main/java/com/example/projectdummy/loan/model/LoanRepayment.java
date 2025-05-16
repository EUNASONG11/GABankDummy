package com.example.projectdummy.loan.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class LoanRepayment {
    private Long loanRepaymentId;
    private Long accountId;
    private Long totalDue;
    private Long principal;
    private Long interest;
    private LocalDateTime dueAt;
    private LocalDateTime finalAt;
    private String dueCode;
}
