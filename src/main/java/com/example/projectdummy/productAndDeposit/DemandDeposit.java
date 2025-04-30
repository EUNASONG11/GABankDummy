package com.example.projectdummy.productAndDeposit;

import lombok.Builder;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Setter
public class DemandDeposit {
    private Long deDepositId;
    private BigDecimal interest;
}
