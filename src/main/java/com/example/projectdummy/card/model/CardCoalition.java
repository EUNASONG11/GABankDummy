package com.example.projectdummy.card.model;

import lombok.Builder;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Setter
public class CardCoalition {
    private Long cardId;
    private Long coalitionId;
    private BigDecimal discount;
    private int limitDiscount;
    private int minPayment;
}
