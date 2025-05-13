package com.example.projectdummy.card.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class CheckCard {
    private Long checkCardId;
    private Long accountId;
}
