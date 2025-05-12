package com.example.projectdummy.exchange.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ForeignExchange {
    private Long employeeId;
    private Long accountId;
    private Long exchangeLogId;
    private Long toExchange;
    private Long fromExchange;
    private int changeFlag;
    private LocalDateTime createdAt;
}
