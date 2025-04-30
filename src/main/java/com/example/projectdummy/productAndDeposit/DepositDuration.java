package com.example.projectdummy.productAndDeposit;

import lombok.Builder;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Builder

public class DepositDuration {
    private Long depositDurationId;
    private Long saDepositId;
    private int duration;
    private BigDecimal overInterest;
    private BigDecimal endInterest;
    private BigDecimal cancelInterest;
}
