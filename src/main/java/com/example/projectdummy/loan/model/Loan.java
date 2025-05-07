package com.example.projectdummy.loan.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class Loan {
    private Long loanId;
    private Integer defermentPeriod;
    private Integer longestPeriod;
    private String redemptionCode;
    private Long maximumAmount;
    private String updatedAt;
    private String loanName;
}
