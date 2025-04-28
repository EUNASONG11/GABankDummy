package com.example.projectdummy.loan.model;

import lombok.Builder;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Setter
public class RateLog {
    private Long RateLogId;
    private Long loanId;
    private BigDecimal additionalRate;
    private BigDecimal baseRate;
    private BigDecimal discountedRate;
    private String baseRateName;
    private Integer useFlag;
}
