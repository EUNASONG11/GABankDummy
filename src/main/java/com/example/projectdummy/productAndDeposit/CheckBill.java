package com.example.projectdummy.productAndDeposit;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class CheckBill {
    private String checkBill;
    private Long accountId;
    private Long money;
    private int typeFlag;
    private String recipientName;
    private int duration;
}
