package com.example.projectdummy.productAndDeposit;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class DepositAccount {
    private Long accountId;
    private Long depositDurationId;
    private LocalDateTime endAt;
    private LocalDateTime cancelDate;
    private BigDecimal discountedRate;
}
