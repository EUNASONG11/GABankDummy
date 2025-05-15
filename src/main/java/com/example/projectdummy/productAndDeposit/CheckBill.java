package com.example.projectdummy.productAndDeposit;

import lombok.*;

import java.time.LocalDate;

@Builder
@Setter
public class CheckBill {
    private String checkBill;
    private Long accountId;
    private Long money;
    private int useFlag;
    private int typeFlag;
    private String recipientName;
    private int duration;
    private LocalDate usedAt;
    private LocalDate createdAt;

}
