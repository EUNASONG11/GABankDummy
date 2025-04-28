package com.example.projectdummy.card.model;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class UserCard {
    private Long uCardId;
    private Long cardId;
    private String cardNumber;
    private String cardPassword;
    private String uCardCode;
    private String benefitCode;
    private String endAt;
}
