package com.example.projectdummy.card.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CardOverDue {
    private Long overdueId;
    private Long overdueAmount;
    private LocalDateTime dueAt;
    private LocalDateTime paidAt;
}
