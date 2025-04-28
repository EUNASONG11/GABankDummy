package com.example.projectdummy.loan.model;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class Loan {
    private Long loanId;
    private int defermentPeriod;
    private int longestPeriod;
    private String redemptionCode;
    private Long maximumAmount;
    private String updatedAt;
    private String loanName;
}
