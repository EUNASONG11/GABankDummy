package com.example.projectdummy.card.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
public class UserCard {
    private Long uCardId;
    private Long cardId;
    private Long custId;
    private String cardNumber;
    private String cardPassword;
    private String uCardCode;
    private String endAt;
}
