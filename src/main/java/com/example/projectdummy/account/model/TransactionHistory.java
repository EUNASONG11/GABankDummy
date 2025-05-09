package com.example.projectdummy.account.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionHistory {
    private Long historyId;
    private Long transactionFeeId;
    private Long accountId;
    private int flag;
    private Long money;
    private String toName;
    private String accountNum;
    private String toBankCode;
    private String location;
    private Long hsMoney;
    private String atmCOde;
    private String useAccount;
    private String useBankCode;
    private LocalDateTime createdAt;
}
