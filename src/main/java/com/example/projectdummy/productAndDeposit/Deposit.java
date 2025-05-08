package com.example.projectdummy.productAndDeposit;

import lombok.Builder;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Setter
public class Deposit {
    private Long depositId;
    private int useFlag;
    private String depositName;
    private BigDecimal discountedRate;
    private String depositCode;
}
