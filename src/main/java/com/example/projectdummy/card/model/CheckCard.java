package com.example.projectdummy.card.model;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class CheckCard {
    private Long checkCardId;
    private Long accountId;
}
