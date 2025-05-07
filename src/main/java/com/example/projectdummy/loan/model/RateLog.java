package com.example.projectdummy.loan.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class RateLog {
    private Long RateLogId;
    private Long loanId;
    private BigDecimal additionalRate;
    private BigDecimal baseRate;
    private BigDecimal discountedRate;
    private String baseRateName;
    private Integer useFlag;
    private LocalDate createdAt;
}
