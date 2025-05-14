package com.example.projectdummy.loan.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LoanInfo {
    private Long accountId;
    private LocalDate createdAt;
    private LocalDate endAt;
    private Double loanMoney;
    private String redemptionCode;
}
