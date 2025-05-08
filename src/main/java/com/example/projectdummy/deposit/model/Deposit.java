package com.example.projectdummy.deposit.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class Deposit {
    private Long depositId;
    private Integer useFlag;
    private String depositName;
    private String endAt;
    private BigDecimal discountedRate;
    private String depositCode;
}
