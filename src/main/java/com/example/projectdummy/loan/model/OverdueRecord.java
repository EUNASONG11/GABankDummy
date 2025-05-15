package com.example.projectdummy.loan.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OverdueRecord {
    private Long overdueRecordId;
    private Long originOverdueId;
    private Long remain;
    private Long overdueMoney;
    private Long targetId;
    private String overdueCode;
    private LocalDateTime startAt;
    private LocalDateTime createdAt;
}
