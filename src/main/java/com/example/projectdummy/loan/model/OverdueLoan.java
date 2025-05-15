package com.example.projectdummy.loan.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OverdueLoan {
    private LocalDateTime updatedAt;
    private LocalDateTime finalAt;
    private Long overdueInterest;
    private Long overdueMoney;
    private Long overdueId;
    private Long loanRepaymentId;
}
