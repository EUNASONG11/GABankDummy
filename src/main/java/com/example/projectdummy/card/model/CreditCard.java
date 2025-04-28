package com.example.projectdummy.card.model;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class CreditCard {
    private Long checkCardId;
    private int dayLimit;
    private int monthLimit;
    private String bankCode;
    private Long accountId;
}
