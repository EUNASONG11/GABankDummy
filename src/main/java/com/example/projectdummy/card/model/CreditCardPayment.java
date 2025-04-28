package com.example.projectdummy.card.model;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class CreditCardPayment {
    private Long creditPaymentId;
    private Long creditId;
    private String dueCode;
    private int cnt;
    private Long dcAmount;
    private String dueAt;
    private String paidAt;
}
