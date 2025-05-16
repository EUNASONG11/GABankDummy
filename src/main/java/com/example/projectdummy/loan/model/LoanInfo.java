package com.example.projectdummy.loan.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class LoanInfo {
    private Long accountId;
    private LocalDate createdAt;
    private LocalDateTime endAt;
    private Double loanMoney;
    private String redemptionCode;
}
