package com.example.projectdummy.exchange.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ExchangeLog {
    private Long exchangeLogId;
    private String currency;
    private BigDecimal standardMoney;
    private BigDecimal chargeRate;
    private LocalDateTime createdAt;
}
