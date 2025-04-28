package com.example.projectdummy.card.model;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class CreditOverdue {
    private Long overdueId;
    private Long creditPaymentId;
    private Long overdueAmount;
}
