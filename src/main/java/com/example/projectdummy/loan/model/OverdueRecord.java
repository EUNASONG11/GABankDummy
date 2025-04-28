package com.example.projectdummy.loan.model;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class OverdueRecord {
    private Long overdueRecordId;
    private Long originOverdueId;
    private Long remain;
    private Long overdueMoney;
    private Long targetId;
    private String overdueCode;
    private String startAt;
}
