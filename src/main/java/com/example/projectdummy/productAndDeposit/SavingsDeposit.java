package com.example.projectdummy.productAndDeposit;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SavingsDeposit {
    private Long depositId;
    private BigDecimal discountedRate;
    private String depositCode;
}
