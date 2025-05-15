package com.example.projectdummy.loan.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LoanOverdue {
    private Long overdueId;
    private Long loanRepaymentId;
    private Long overdueMoney;
    private int paymentFlag;
    private LocalDate updatedAt;
    private LocalDate createdAt;
    private Long overdueInterest;
}
