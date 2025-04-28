package com.example.projectdummy.loan.model;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class LoanOverdue {
    private Long overdueId;
    private Long loanRepaymentId;
    private Long overdueMoney;
    private int paymentFlag;
}
