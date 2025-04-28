package com.example.projectdummy.card.model;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class CardOptionLog {
    private Long cardOptionLogId;
    private Long uCardId;
    private String cOptionCode;
    private int flag;
}
