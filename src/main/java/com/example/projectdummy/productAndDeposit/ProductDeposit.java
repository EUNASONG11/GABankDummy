package com.example.projectdummy.productAndDeposit;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductDeposit {
    private Long depositId;
    private BigDecimal discountedRate;
    private String depositCode;
}
