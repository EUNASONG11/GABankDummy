package com.example.projectdummy.card.model;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class Card {
    private Long cardId;
    private String cName;
    private int annualFee;
    private int flag;
    private int minRequire;
    private int available;
    private String benefitCode;
}
